package net.bioclipse.cdk.jchempaint;

import net.bioclipse.cdk.jchempaint.business.IJChemPaintManager;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plug-in life cycle
 * 
 * @author jonalv
 */
public class Activator extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "net.bioclipse.cdk.jchempaint";

    // The shared instance
    private static Activator plugin;
    
    // tracks the example manager
    private ServiceTracker finderTracker;
    
    /**
     * The constructor
     */
    public Activator() {
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        
        finderTracker = new ServiceTracker( context, 
                                            IJChemPaintManager.class.getName(), 
                                            null );
        finderTracker.open();
    }

    /**
     * Returns a reference to the example manager object
     * 
     * @return the exampleManager
     */
    public IJChemPaintManager getExampleManager() {
        IJChemPaintManager exampleManager = null;
        try {
            exampleManager = (IJChemPaintManager) finderTracker.waitForService(1000*30);
        } catch (InterruptedException e) {
            throw new IllegalStateException("Could not get example manager", e);
        }
        if(exampleManager == null) {
            throw new IllegalStateException("Could not get example manager");
        }
        return exampleManager;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static Activator getDefault() {
        return plugin;
    }
}
