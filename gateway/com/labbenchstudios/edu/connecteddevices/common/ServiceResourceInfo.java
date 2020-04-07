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

import java.io.Serializable;

/**
 * A container class for storing information about a remote resource.
 * 
 * @author aking
 */
public class ServiceResourceInfo implements Serializable
{
	// static
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3058430996561552339L;
	
	// private var's
	
	private String _parentId    = null;
	private String _name        = null;
	private String _description = null;
	private String _topic       = null;
	private String _id          = null;
	
	
	// constructors
	
	/**
	 * Default.
	 * 
	 */
	public ServiceResourceInfo()
	{
		super();
	}
	
	/**
	 * Constructor.
	 * 
	 * @param name A human-readable name for the resource.
	 * @param description A human-readable description of the resource.
	 */
	public ServiceResourceInfo(String name, String description)
	{
		this(name, description, null, null);
	}
	
	/**
	 * Constructor.
	 * 
	 * @param name A human-readable name for the resource.
	 * @param description A human-readable description of the resource.
	 * @param topic The computer-mappable name of the resource.
	 * @param id The unique ID representing the resource.
	 */
	public ServiceResourceInfo(String name, String description, String topic, String id)
	{
		_name        = name;
		_description = description;
		_topic       = topic;
		_id          = id;
	}
	
	// public methods
	
	/**
	 * Returns the human-readable description of the resource.
	 * 
	 * @return String
	 */
	public String getDescription()
	{
		return _description;
	}
	
	/**
	 * Returns the unique ID representing the resource.
	 * 
	 * @return String
	 */
	public String getId()
	{
		return _id;
	}
	
	/**
	 * Returns the human-readable name for the resource.
	 * 
	 * @return String
	 */
	public String getName()
	{
		return _name;
	}
	
	/**
	 * Returns the unique parent ID that contains the resource.
	 * 
	 * @return String
	 */
	public String getParentId()
	{
		return _parentId;
	}
	
	/**
	 * Returns the computer-mappable name of the resource.
	 * 
	 * @return String
	 */
	public String getTopic()
	{
		return _topic;
	}
	
	/**
	 * Sets the unique parent ID that will contain the resource.
	 * If null or empty, it will still be set. A null will essentially
	 * disable the parent ID.
	 * 
	 * @param id
	 */
	public void setParentId(String id)
	{
		_parentId = id;
	}
	
	/**
	 * Returns a stringified representation of the elements stored in this
	 * instance, separated by ':' characters, as follows:
	 * parentID:name:description:topic:ID
	 * 
	 * @return String
	 */
	public String toString()
	{
		return (_parentId + ":" + _name + ":" + _description + ":" + _topic + ":" + _id);
	}
	
}
