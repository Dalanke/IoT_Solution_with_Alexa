/**
 * Copyright (c) 2015-2019. Andrew D. King. All Rights Reserved.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.labbenchstudios.edu.connecteddevices.common;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides basic application functionality for all
 * sub-classes. It will enable easy processing of command line
 * arg's, as well as process startup (initialization) and shutdown.
 *
 * @author aking
 */
public abstract class BaseDeviceApp
{
    // static member var section
	private static final Logger _Logger =
		Logger.getLogger(BaseDeviceApp.class.getName());
	
    /** Indicates a normal exit when used as the exit code */
    public static final int NORMAL_EXIT_CODE =  0;

    /** Indicates an abnormal, but not erroneous, exit */
    public static final int ABNORMAL_EXIT_CODE =  1;

    /** Indicates an error-forced exit */
    public static final int ERROR_EXIT_CODE = -1;

    /** Default server port */
    public static final int DEFAULT_SERVER_PORT = 45832;
    
    /** Default server host */
    public static final String DEFAULT_SERVER_HOST = "localhost";
    
    /** Cmd line arg indicator ('-') */
    public static final String DASH_STR = "-";

    /** Help cmd line arg - long */
    public static final String HELP_ARG_LONG = "help";

    /** Help cmd line arg - short */
    public static final String HELP_ARG_SHORT = "h";

    // static method section

    
    // private member var section
    
    private Properties _configProps = null;
    private String     _appName     = this.getClass().getSimpleName();
    private String[]   _args        = null;
    private int        _exitCode    = NORMAL_EXIT_CODE;


    // constructor section

    /**
     * Constructs a default BaseApp using the default name (simple class name)
     * and no arguments.
     * <p>
     * Use of this constructor assumes there are no command-line args, nor
     * is there an available configuration file.
     * 
     */
    protected BaseDeviceApp()
    {
        this(null, null);
    }

    /**
     * Constructs a BaseApp object with the given name parameter.
     * <p>
     * Use of this constructor assumes there are no command-line args, nor
     * is there an available configuration file.
     * 
     * @param appName The name of the application. If null, the simple
     * class name will be used instead.
     */
    protected BaseDeviceApp(String appName)
    {
    	this(appName, null);
    }

    /**
     * Constructs a BaseApp object with the given name parameter and
     * argument array.
     *
     * @param appName The name of the application. If null, the simple
     * class name will be used instead.
     * @param args The command line args passed in to the app. If null,
     * empty, or otherwise invalid, this parameter will be ignored.
     */
    protected BaseDeviceApp(String appName, String[] args)
    {
        setAppName(appName);
        processCommandArgs(args);
    }


    // public methods

    /**
     * Returns the name of this application (set by the sub-class, or
     * if not, the default simple class name will be used).
     *
     * @return String The name of this application.
     */
    public final String getAppName()
    {
        return _appName;
    }
    
    /**
     * Returns the original arguments passed in during construction.
     * 
     * @return String[]
     */
    public final String[] getArgs()
    {
    	return _args;
    }
    
    /**
     * Returns the current exit code - if set - which should be set internally,
     * or by the sub-class. This method should always be called to retrieve
     * the exit code, vs. just using the variable (in case the sub-class
     * overrides the get/set method(s).
     *
     * @return int The exit code for this app.
     */
    public final int getExitCode()
    {
        return _exitCode;
    }
    
    /**
     * Entry point to start the application. This will invoke
     * {@link #start()} within a try / catch block, exiting
     * gracefully (if possible) should an exception get thrown.
     * 
     */
    public final void startApp()
    {
    	try {
    		start();
    	} catch (Exception e) {
    		_Logger.log(Level.SEVERE, "Failed to start application: " + this.getAppName(), e);
    		
    		setExitCode(-1);
    		stopApp();
    	}
    }

    /**
     * Entry point to stop the application. This will invoke
     * {@link #stop()} withi a try / catch block, exiting the app
     * gracefully (if possible) should an exception get thrown.
     *
     */
    public final void stopApp()
    {
        try {
        	stop();
        	
            _Logger.log(
            	Level.INFO,
            	"App stop() method invoked. Exiting application: " +
            	getExitCode());
        } catch (Exception e) {
        	if (getExitCode() == 0) {
        		setExitCode(-1);
        	}
        	
        	_Logger.log(
        		Level.SEVERE,
        		"Failed to cleanly stop app. Exiting application:" +
        		getExitCode(),
        		e);
        } finally {
            System.exit(getExitCode());
        }
    }

    
    // protected methods

    /**
     * Returns the Properties for this application.
     * 
     * @return Properties
     */
    protected final Properties getConfigurationProps()
    {
    	return _configProps;
    }
    
    /**
     * Sets the internally used exit code. This method will generally be
     * used by the sub-class to custom handle exit code values as they
     * relate to the concrete implementation.
     *
     * @param exitCode The exit code (int) for this app.
     */
    protected final void setExitCode(int exitCode)
    {
        _exitCode = exitCode;
    }

    /**
     * Starts this application. The sub-class must implement this method.
     *
     * @exception DeviceApplicationException Thrown if an exception occurs during startup.
     */
    protected abstract void start()
        throws DeviceApplicationException;

    /**
     * Stops this application. The sub-class must implement this method.
     *
     * @exception DeviceApplicationException Thrown if an exception occurs during stop.
     */
    protected abstract void stop()
    	throws DeviceApplicationException;


    // private methods

    /**
     * This method processes the standard command-line args, such as those
     * that handle displaying help and usage information. All others are
     * relegated to the sub-classes implementation of processLocalArgs.
     *
     */
    private void processCommandArgs(String[] args)
    {
    	_args = args;
    	
        if (args != null && args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                
                // check for 'help' arg
            }
        }
    }

	/**
     * This method simply sets the name of the application. Null
     * is allowed.
     *
     * @param appNameThe name of the application. Usually used in UI controls.
     */
    private void setAppName(String appName)
    {
        if (appName != null && appName.length () > 0) {
            _appName = appName;
        }
    }

} // class BaseApp
