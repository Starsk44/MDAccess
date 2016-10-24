package com.sodius.mdaccess.doors.snippets.read;

import com.sodius.mdw.core.model.Model;
import com.sodius.mdw.metamodel.doors.Attribute;
import com.sodius.mdw.metamodel.doors.FormalModule;
import com.sodius.mdw.metamodel.doors.Type;

/*
 * Print in the console the content of a DOORS module.
 *
 * Expected Program Arguments:
 * args[0] = module identifier (e.g. "/Folder/MyModule")
 *
 * The module identifier might be:
 * - a DOORS module qualified name (e.g. "/myFolder/MyModule")
 * - a DOORS module id (see DOORS Item id in DXL help)
 * - a DOORS URL (e.g. "doors://...")
 *
 * Expected VM arguments:
 * -Dmdw.license=myLicenseFileOrServerAddress
 */
public class PrintModuleContentSnippet extends AbstractReadSnippet {

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            throw new IllegalArgumentException("Expects a module identifier as argument");
        }
        String moduleIdentifier = args[0];
        new PrintModuleContentSnippet(moduleIdentifier).run();
    }

    private final String moduleIdentifier;

    public PrintModuleContentSnippet(String moduleIdentifier) {
        this.moduleIdentifier = moduleIdentifier;
    }

    @Override
    protected void run(Model model) {

        // lookup for the module based on its identifier
        FormalModule module = readModule(model, moduleIdentifier);
        if (module == null) {
            throw new IllegalArgumentException("No such module: " + moduleIdentifier);
        }

        // print its content
        printContent(module);
    }

    private void printContent(FormalModule module) {
        System.out.println("Reading content of module: " + moduleIdentifier);
        
        // module information
        System.out.println("Name: " + module.getName());
        System.out.println("Full Name: " + module.getQualifiedName());
        System.out.println("Last Modified On: " + module.getLastModifiedOn());
        System.out.println();

        // types
        printTypes(module);
        System.out.println();

        // attributes
        printAttributes(module);
        System.out.println();
        
        // objects
        printObjects(module);
    }

    private void printTypes(FormalModule module) {
        System.out.println(module.getTypes().size() + " types found:");
        for (Type type : module.getTypes()) {
            printType(type);
        }
    }

    private void printType(Type type) {
        if (type.isSystem()) {
            System.out.println("- " + type.getName());
        } else {
            // If this is not a system type (i.e. this is a type defined by the module administrator),
            // we display its base system type as well.
            System.out.println("- " + type.getName() + " (" + type.getBaseType() + ")");
        }
    }

    private void printAttributes(FormalModule module) {
        System.out.println(module.getAttributes().size() + " attributes found:");
        for (Attribute attribute : module.getAttributes()) {
            printAttribute(attribute);
        }
    }

    private void printAttribute(Attribute attribute) {
        System.out.println("- " + attribute.getName() + ": " + attribute.getType());
    }

    private void printObjects(FormalModule module) {
        System.out.println(module.getAllObjects().size() + " objects found:");
        for (com.sodius.mdw.metamodel.doors.Object object : module.getAllObjects()) {
            printObject(object);
        }
    }

    private void printObject(com.sodius.mdw.metamodel.doors.Object object) {

        // An Object might have
        // - an "Object Heading" (see object.getObjectHeading())
        // - an "Object Text" (see object.getObjectText()),
        // - and/or an "Object Short Text" (see object.getObjectShortText())
        // Object.toString() returns the first text slot that is not empty
        // and returns a plain text version (the Object Text might be formatted with rich text content).
        String text = object.toString();

        System.out.println("- [" + object.getObjectIdentifier() + "] " + object.getObjectNumber() + ": " + text);
    }

}
