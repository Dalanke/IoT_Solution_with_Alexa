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

/**
 * Interface detailing a generic API that can be used to desribe
 * the connection and interface between a local client and remote
 * cloud service.
 * 
 * @author aking
 */
public interface IIotCloudGateway
{
	/**
	 * Checks the connection status between the client and the remote
	 * cloud service.
	 * 
	 * @return boolean True if currently connected to the remote cloud
	 * services; false otherwise.
	 */
	boolean isConnected();
	
	/**
	 * Attempts to connect to the remote cloud service. This method
	 * may throw an exception, depending on the implementing class
	 * functionality.
	 * 
	 */
	void connect();
	
	/**
	 * Attempts to disconnect from the remote cloud service. This method
	 * may throw an exception, depending on the implementing class
	 * functionality.
	 * 
	 */
	void disconnect();
	
	/**
	 * Publishes the given 'payload' directly to the remote broker using the resource
	 * information contained within 'resource'.
	 * 
	 * @param resource The resource container to use for publishing 'payload'
	 * {@link ServiceResourceInfo}.
	 * @param qosLevel The QoS level to use for publishing 'payload'.
	 * @param payload The data to publish.
	 * @return boolean True on successful publish; false otherwise.
	 */
	boolean publishMessage(ServiceResourceInfo resource, int qosLevel, byte[] payload);
	
	/**
	 * Attempts to subscribe to all registered topics on the remote broker.
	 * 
	 * @return boolean True on successful subscribe; false otherwise.
	 */
	boolean subscribeToAll();
	
	/**
	 * Attempts to subscribe to the topic 'topic' on the remote broker.
	 * 
	 * @param resource The resource container to use for subscribing
	 * {@link ServiceResourceInfo}.
	 * @return boolean True on successful subscribe; false otherwise.
	 */
	boolean subscribeToTopic(ServiceResourceInfo resource);
	
}