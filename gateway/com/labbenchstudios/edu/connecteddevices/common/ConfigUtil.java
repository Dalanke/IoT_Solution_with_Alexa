/**
 * Copyright (c) 2018-2019. Andrew D. King. All Rights Reserved.
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

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.configuration.*;

/**
 * A simple utility wrapper around the Apache Commons
 * configuration infrastructure.
 * 
 * @author aking
 */
public class ConfigUtil
{
	// static
	
	private static final Logger     _Logger =
		Logger.getLogger(ConfigUtil.class.getName());
	
	private static final ConfigUtil _Instance = new ConfigUtil();

	/**
	 * Returns the Singleton instance of this class.
	 * 
	 * @return ConfigUtil
	 */
	public static final ConfigUtil getInstance()
	{
		return _Instance;
	}
	
	
	// private var's
	
	private HierarchicalINIConfiguration _sectionProperties = null;
	
	private boolean _isLoaded = false;
	
	
	// constructors
	
	/**
	 * Default (private).
	 * 
	 * Creates a new instance of {@link HierarchichalINIConfiguration}.
	 */
	private ConfigUtil()
	{
		super();
		
		initBackingProperties();
	}
	
	
	// public methods
	
	/**
	 * Returns the requested property from the given section.
	 * 
	 * @param section The section from which to retrieve 'propName'. If it
	 * doesn't exist, it will be created.
	 * @param propName The name of the property to retrieve.
	 * @return String The value for 'propName'. If no mapping exists,
	 * a {@link ConversionException will be thrown}.
	 * @Exception ConversionException Thrown if 'propName' does not map to a String.
	 */
	public synchronized String getProperty(String section, String propName)
	{
		SubnodeConfiguration subNodeConfig = _sectionProperties.getSection(section);
		
		return subNodeConfig.getString(propName);
	}
	
	/**
	 * Returns the requested property from the given section.
	 * 
	 * @param section The section from which to retrieve the value for 'propName'. If it
	 * If the section doesn't exist, it will be created.
	 * @param propName The name of the property to retrieve.
	 * @return boolean The value for 'propName'. If no mapping exists,
	 * a {@link ConversionException will be thrown}.
	 * @Exception ConversionException Thrown if 'propName' does not map to a boolean.
	 */
	public synchronized boolean getBooleanProperty(String section, String propName)
	{
		SubnodeConfiguration subNodeConfig = _sectionProperties.getSection(section);
		
		return subNodeConfig.getBoolean(propName);
	}
	
	/**
	 * Returns the requested property from the given section.
	 * 
	 * @param section The section from which to retrieve 'propName'. If it
	 * doesn't exist, it will be created.
	 * @param propName The name of the property to retrieve.
	 * @return int The value for 'propName'. If no mapping exists,
	 * a {@link ConversionException will be thrown}.
	 * @Exception ConversionException Thrown if 'propName' does not map to a int.
	 */
	public synchronized int getIntegerProperty(String section, String propName)
	{
		SubnodeConfiguration subNodeConfig = _sectionProperties.getSection(section);
		
		return subNodeConfig.getInt(propName);
	}
	
	/**
	 * Returns the requested property from the given section.
	 * 
	 * @param section The section from which to retrieve 'propName'. If it
	 * doesn't exist, it will be created.
	 * @param propName The name of the property to retrieve.
	 * @return float The value for 'propName'. If no mapping exists,
	 * a {@link ConversionException will be thrown}.
	 * @Exception ConversionException Thrown if 'propName' does not map to a float.
	 */
	public synchronized float getFloatProperty(String section, String propName)
	{
		SubnodeConfiguration subNodeConfig = _sectionProperties.getSection(section);
		
		return subNodeConfig.getFloat(propName);
	}
	
	/**
	 * Returns true if the requested property exists in the given section.
	 * 
	 * @param section The section from which to retrieve 'propName'. If it
	 * doesn't exist, it will be created.
	 * @param propName The name of the property to retrieve.
	 * @return boolean True if the property exists; false otherwise.
	 * @Exception ConversionException Thrown if 'propName' does not map to a String.
	 */
	public synchronized boolean hasProperty(String section, String propName)
	{
		SubnodeConfiguration subNodeConfig = _sectionProperties.getSection(section);
		
		return (subNodeConfig.getProperty(propName) != null);
	}
	
	/**
	 * Returns true if the requested section exists and / or contains
	 * any properties within the loaded configuration.
	 * <p>
	 * NOTE: The underlying configuration handler will create 'section' if
	 * it doesn't already exist; hence, it can make for large configurations
	 * if this method is called with a different non-existent section name
	 * often, even if they remain empty (which is the default case).
	 * 
	 * @param section The section from which to retrieve 'propName'. If it
	 * doesn't exist, it will be created as an empty section.
	 * @return boolean True on success; false otherwise.
	 * @Exception ConversionException Thrown if 'section' does not map to a String.
	 */
	public synchronized boolean hasSection(String section)
	{
		SubnodeConfiguration subNodeConfig = _sectionProperties.getSection(section);
		
		return (subNodeConfig != null);// && subNodeConfig.isEmpty());
	}
	
	/**
	 * Attempts to load the default configuration file. This method
	 * will invoke {@link #loadConfig(String) with the file name
	 * specified as {@link ConfigConst#DEFAULT_CONFIG_FILE_NAME}.
	 * 
	 * @return boolean True on success; false otherwise.
	 */
	public synchronized boolean loadConfig()
	{
		return loadConfig(ConfigConst.DEFAULT_CONFIG_FILE_NAME);
	}
	
	/**
	 * Attempts to load the default configuration file, as specified
	 * by {@link ConfigConst#DEFAULT_CONFIG_FILE_NAME}.
	 * 
	 * @return boolean True on success; false otherwise.
	 */
	public synchronized boolean loadConfig(String configFile)
	{
		boolean success = false;
		
		try {
			if (configFile == null || configFile.trim().length() == 0) {
				configFile = ConfigConst.DEFAULT_CONFIG_FILE_NAME;
				
				_Logger.warning("Config file is invalid. Using default: " + configFile);
			}
			
			// ensure config file exists
			File cfgFile = new File(configFile);
			
			if (cfgFile.exists() && cfgFile.canRead()) {
				// init the backing properties, or clear out the existing one
				initBackingProperties();
				
				_sectionProperties.load(configFile);
				_isLoaded = true;
			} else {
				_Logger.warning("Config file either doesn't exist or can't be read.");
			}
		} catch (ConfigurationException e) {
			_Logger.log(Level.WARNING, "Failed to load configuration file: " + configFile, e);
		}
		
		success = _isLoaded;
		
		if (! success) {
			_Logger.warning(
				"Config file not loaded: " + configFile + ". See stack trace for failure details.");
		}
		
		return success;
	}
	
	/**
	 * Returns the flag indicating if either of the most recently called
	 * {@link #loadConfig()} or {@link #loadConfig(String)} methods was
	 * successful or failed.
	 * 
	 * @return true True if the most recent load invocation was successful;
	 * false otherwise.
	 */
	public boolean isConfigDataLoaded()
	{
		return _isLoaded;
	}
	
	// private methods
	
	/**
	 * Initializes the backing properties store - this will either create a new
	 * instance (if the current ref is null) or clear the contents from the
	 * existing instance.
	 * 
	 */
	private void initBackingProperties()
	{
		_sectionProperties = new HierarchicalINIConfiguration();
	}
	
}
