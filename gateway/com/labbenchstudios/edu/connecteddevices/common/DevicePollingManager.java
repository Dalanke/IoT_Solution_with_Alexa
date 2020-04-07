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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Provides a simple mechanism for passing {@link Runnable}
 * implementations to a scheduler based on Java's
 * {@link ScheduledExecutorService}.
 * 
 * @author Andrew King
 *
 */
public class DevicePollingManager
{
	// static
	
	private static final Logger _Logger =
		Logger.getLogger(DevicePollingManager.class.getName());
	
	public static final int  DEFAULT_THREAD_POOL_SIZE =  5;
	public static final int  MAX_THREAD_POOL_SIZE     = 20;
	public static final long MIN_POLL_CYCLE           =  1L;
	
	// params
	
	private ScheduledExecutorService _scheduler;
	private List<ScheduledFuture<?>> _futureTaskList;
	
	private int  _threadPoolSize = DEFAULT_THREAD_POOL_SIZE;
	

	// constructors
	
	/**
	 * Default.
	 * 
	 */
	public DevicePollingManager()
	{
		this(DEFAULT_THREAD_POOL_SIZE);
	}
	
	/**
	 * Constructor. Initializes the locally scoped scheduler.
	 * 
	 * @param threadPoolSize The number of threads to maintain in the scheduler
	 * pool. Must be greater than 1, and less than {@link #MAX_THREAD_POOL_SIZE}.
	 * This will default to {@link #DEFAULT_THREAD_POOL_SIZE}.
	 */
	public DevicePollingManager(int threadPoolSize)
	{
		super();
		
		if (threadPoolSize >= 1) {
			_threadPoolSize = threadPoolSize;
		}
		
		initScheduler();
	}
	
	
	// public methods
	
	/**
	 * Schedules the passed in Runnable to be executed immediately.
	 * If the scheduler has been shutdown, it will be re-initialized
	 * along with the future task list.
	 * 
	 * @param poller The Runnable to schedule for execution.
	 * @param rateInSecs The poll rate in seconds. Must be >= 1L, else
	 * will be set to {@link #MIN_POLL_CYCLE}.
	 */
	public synchronized void schedulePollingTask(Runnable poller, long rateInSecs)
	{
		if (_scheduler == null || _scheduler.isTerminated() || _scheduler.isShutdown()) {
			initScheduler();
		}
		
		if (rateInSecs < MIN_POLL_CYCLE) {
			rateInSecs = MIN_POLL_CYCLE;
		}
		
		if (poller != null) {
			ScheduledFuture<?> futureTask =
				_scheduler.scheduleAtFixedRate(poller, 0L, rateInSecs, TimeUnit.SECONDS);
			
			_futureTaskList.add(futureTask);
		}
	}
	
	/**
	 * Issues a cancel request to all future tasks that have been scheduled.
	 * If the task is already cancelled, a warning message will be logged.
	 * Each future task stored in the future task list will be handled in
	 * sequence, whether or not the cancel attempt fails or throws an exception.
	 * 
	 */
	public synchronized void stopPolling()
	{
		for (ScheduledFuture<?> futureTask : _futureTaskList) {
			String taskName = futureTask.toString();
			
			try {
				if (futureTask.cancel(true)) {
					_Logger.info(taskName + " successfully cancelled.");
				} else {
					_Logger.warning(taskName + " already cancelled or can't be cancelled.");
				}
			} catch (Exception e) {
				_Logger.log(Level.SEVERE, "Failed in temporarily stop scheduled polling task: " + taskName, e);
			}
		}
	}
	
	/**
	 * Terminates all polling. Invokes {@link #stopPolling()}, then forces
	 * the internal scheduler to shutdown immediately.
	 * 
	 */
	public synchronized void terminatePolling()
	{
		try {
			stopPolling();
			
			_scheduler.shutdownNow();
		} catch (Exception e) {
			_Logger.log(Level.SEVERE, "Failed in orderly shutdown of scheduled tasks.", e);
		}
	}
	
	
	// private methods
	
	/**
	 * Initializes the scheduler and an internal future task list for tracking
	 * tasks so they can be cancelled at a later date (if desired).
	 * 
	 */
	private void initScheduler()
	{
		_scheduler      = Executors.newScheduledThreadPool(_threadPoolSize);
		_futureTaskList = new ArrayList<ScheduledFuture<?>>();
	}
	
}
