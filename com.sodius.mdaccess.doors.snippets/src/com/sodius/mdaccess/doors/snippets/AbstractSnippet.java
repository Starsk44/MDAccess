package com.sodius.mdaccess.doors.snippets;

import org.eclipse.core.runtime.CoreException;

import com.sodius.mdw.core.util.DefaultPropertySet;
import com.sodius.mdw.core.util.PropertySet;
import com.sodius.mdw.metamodel.doors.connectors.dxl.Common;
import com.sodius.mdw.metamodel.doors.connectors.dxl.DoorsUtils;
import com.sodius.mdw.platform.doors.DoorsApplication;

public abstract class AbstractSnippet implements Runnable {

    /*
     * A DoorsApplication is what is used to connect to DOORS and execute DXL code to retrieve information from Java.
     * By default, the connection is made to the active DOORS client; a connection which is called 'interactive mode'.
     *
     * You can configure the Java VM arguments to create a connection to a private hidden DOORS batch client:
     * -Ddoors.isSilent=true
     * -Ddoors.path="C:\Program Files\IBM\Rational\DOORS\9.6\bin\doors.exe"
     * -Ddoors.portserver=36677@myHost
     * -Ddoors.user=myUser
     * -Ddoors.password=myPassword
     */
    protected final DoorsApplication createDoorsApplication() throws CoreException {
        
        // batch mode is requested in System properties?
        if (Boolean.getBoolean(Common.PREFERENCE_DOORS_IS_SILENT)) {

            // batch mode
            PropertySet properties = new DefaultPropertySet();
            properties.setProperty(Common.PREFERENCE_DOORS_IS_SILENT, true);
            properties.setProperty(Common.PREFERENCE_DOORS_PATH, System.getProperty(Common.PREFERENCE_DOORS_PATH));
            properties.setProperty(Common.PREFERENCE_DOORS_PORTSERVER, System.getProperty(Common.PREFERENCE_DOORS_PORTSERVER));
            properties.setProperty(Common.PREFERENCE_DOORS_USER, System.getProperty(Common.PREFERENCE_DOORS_USER));
            properties.setProperty(Common.PREFERENCE_DOORS_PASSWORD, System.getProperty(Common.PREFERENCE_DOORS_PASSWORD));
            return DoorsUtils.createApplication(properties);
        }
        
        else {
            // interactive mode (the default)
            return new DoorsApplication();
        }
    }

}
