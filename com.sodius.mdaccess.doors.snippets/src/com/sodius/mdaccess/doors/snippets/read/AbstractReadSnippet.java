package com.sodius.mdaccess.doors.snippets.read;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import com.sodius.mdaccess.doors.snippets.AbstractSnippet;
import com.sodius.mdw.core.CoreException;
import com.sodius.mdw.core.MDWorkbench;
import com.sodius.mdw.core.MDWorkbenchFactory;
import com.sodius.mdw.core.model.Model;
import com.sodius.mdw.metamodel.doors.DoorsPackage;
import com.sodius.mdw.metamodel.doors.Folder;
import com.sodius.mdw.metamodel.doors.FormalModule;
import com.sodius.mdw.metamodel.doors.connectors.dxl.Common;
import com.sodius.mdw.platform.doors.DoorsApplication;

public abstract class AbstractReadSnippet extends AbstractSnippet {

    public final void run() {
        MDWorkbench workbench = null;
        DoorsApplication application = null;
        Model model = null;
        try {
            System.out.println("Connecting to DOORS...");
            application = createDoorsApplication();
            workbench = MDWorkbenchFactory.create();
            model = readDoorsModel(workbench, application);

            run(model);
        } catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            // release the connection to DOORS and MDWorkbench license
            if (model != null) {
                model.clear();
            }
            if (application != null) {
                application.dispose();
            }
            if (workbench != null) {
                workbench.shutdown();
            }
        }
    }
    
    protected abstract void run(Model model);

    /*
     * Creates an EMF representation of DOORS data.
     * By default, the EMF model will contain only the DOORS database root folder instance.
     * Information is read from DOORS on demand, when requested by using getters on model instances.
     */
    private Model readDoorsModel(MDWorkbench workbench, DoorsApplication application) throws CoreException {
        Model model = workbench.getMetamodelManager().getMetamodel(DoorsPackage.eINSTANCE).createModel();
        model.read("Application", null, createReaderOptions(application));
        return model;
    }

    /*
     * Get the instance representing the DOORS root database.
     * This root folder is guaranteed to be available in the Model.
     * It is the first Folder instance registered in the Model.
     * There might be additional Folder instances later, as Folders get discovered when navigating in DOORS data.
     */
    private Folder getRootFolder(Model model) {
        return model.<Folder> getInstances(DoorsPackage.Literals.FOLDER).first();
    }

    /*
     * Request access to a module given its identifier.
     * 
     * The module identifier might be:
     * - a DOORS module qualified name (e.g. "/myFolder/MyModule")
     * - a DOORS module id (see DOORS Item id in DXL help)
     * - a DOORS URL (e.g. "doors://...")
     * 
     * If this module is not already read at this time,
     * the on-demand process reconnects to DOORS to retrieve the necessary data.
     */
    protected FormalModule readModule(Model model, String moduleIdentifier) {

        // get the EMF resource used to resolve element given they id (here the qualified name)
        Folder rootFolder = getRootFolder(model);
        Resource resource = rootFolder.eResource();

        // resolve the module
        EObject object = resource.getEObject(moduleIdentifier);

        // check the type of the resolved object, just in case the qualified name refers to a folder for example
        if (object instanceof FormalModule) {
            return (FormalModule) object;
        } else {
            return null;
        }
    }

    /*
     * Options to tweak the connection to the DOORS database.
     */
    protected Map<String, Object> createReaderOptions(DoorsApplication application) {
        Map<String, Object> options = new HashMap<String, Object>();

        // Avoid to display a connection dialog
        options.put(Common.OPTION_IGNORE_CONNECTOR_UI, true);

        // Request to use the specified DOORS application (the default is to connect to the active DOORS client)
        options.put(Common.OPTION_DOORS_APPLICATION, application);

        return options;
    }
}
