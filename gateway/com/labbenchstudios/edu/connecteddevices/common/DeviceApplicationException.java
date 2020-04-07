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

/**
 * This is a generic checked exception wrapper for any exception
 * which occurs during application startup or stop. This is used
 * for typing only. It is a simple sub-class of {@link Exception}.
 * 
 * @author aking
 */
public class DeviceApplicationException extends Exception
{
	// static
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5879668359715917569L;

	
	// constructors
	
	/**
	 * Default.
	 * 
	 */
	public DeviceApplicationException()
	{
		super();
	}
	
	/**
	 * Constructor
	 * 
	 * @param message
	 */
	public DeviceApplicationException(String message)
	{
		super(message);
	}
	
	/**
	 * Constructor.
	 * 
	 * @param t
	 */
	public DeviceApplicationException(Throwable t)
	{
		super(t);
	}
	
	/**
	 * Constructor.
	 * 
	 * @param message
	 * @param t
	 */
	public DeviceApplicationException(String message, Throwable t)
	{
		super(message, t);
	}
	
	/**
	 * Constructor.
	 * 
	 * @param message
	 * @param t
	 * @param flagA
	 * @param flagB
	 */
	public DeviceApplicationException(String message, Throwable t, boolean flagA, boolean flagB)
	{
		super(message, t, flagA, flagB);
	}
	
}
