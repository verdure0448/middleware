package com.hdbsnc.smartiot.util.servicepool.impl;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.hdbsnc.smartiot.util.servicepool.AlreadyClosedException;
import com.hdbsnc.smartiot.util.servicepool.ServicePool;
import com.hdbsnc.smartiot.util.servicepool.future.FutureRunnable;
import com.hdbsnc.smartiot.util.servicepool.future.FutureThread;
import com.hdbsnc.smartiot.util.servicepool.pool.ThreadPool;

public class DefaultServicePool implements ServicePool{

	private ThreadPool threadPool;
	private Timer timer;
	
	private int maxScheduleCount;
	private int scheduleCount;
	
	public DefaultServicePool(	int initThreadCount,
								int maxThreadCount,
								int minThreadCount,
								int allowedIdleCount,
								int maxShcheduleCount){
		this.threadPool = new ThreadPool(	initThreadCount,
											maxThreadCount,
											minThreadCount,
											allowedIdleCount);
		
		this.timer = new Timer();
		this.maxScheduleCount = maxShcheduleCount;
		
	}
	
	public void addSchedule(Runnable run, Date firstTime) {
		this.timer.schedule(new Schedule(run), firstTime);
		scheduleCount++;
		purge();
	}

	public void addSchedule(Runnable run, Date firstTime, int period) {
		this.timer.schedule(new Schedule(run),firstTime, period);
		scheduleCount++;
		purge();
	}

	public void addSchedule(TimerTask task, Date firstTime, int period) {
		this.timer.scheduleAtFixedRate(task, firstTime, period);
		scheduleCount++;
		purge();
	}

	public void addSchedule(TimerTask task, long delay, int period) {
		this.timer.schedule(task, delay, period);
		scheduleCount++;
		purge();
	}

	public void addSchedule(TimerTask task, Date firstTime) {
		this.timer.schedule(task, firstTime);
		scheduleCount++;
		purge();
	}

	public void addScheduleAtFixedRate(Runnable run, Date firstTime, int period) {
		this.timer.schedule(new Schedule(run),firstTime, period);
		scheduleCount++;
		purge();
	}

	public void addScheduleAtFixedRate(TimerTask task, Date firstTime, int period) {
		this.timer.scheduleAtFixedRate(task, firstTime, period);
		scheduleCount++;
		purge();
	}

	private void purge() {
		if(scheduleCount > maxScheduleCount) {
			scheduleCount = 0;
			this.timer.purge();
		}
	}
	
	public void cancel() {
		this.timer.cancel();
	}

	public void execute(Runnable run) throws AlreadyClosedException {
		this.threadPool.execute(run);
	}

	public Object execute(FutureRunnable task) throws InterruptedException {
		return new FutureThread(task).getResult();
	}

	public Object execute(FutureRunnable task, int timeout) throws InterruptedException{
		return new FutureThread(task).getResult(timeout);
	}

	public void close() throws AlreadyClosedException{
		this.threadPool.close();
	}

	public String getStatus(){
		return this.threadPool.getStatus();
	}

	private class Schedule extends TimerTask{

		private Runnable run;

		public Schedule(Runnable run){
			super();
			this.run = run;
		}

		public void run(){
			run.run();
		}
	}

	public int getAllowedIdleCount() {
		return this.threadPool.getAllowedIdleCount();
	}

	public int getCreatedThreadCount() {
		return this.threadPool.getCreatedThreadCount();
	}

	public int getIdleThreadCount() {
		return this.threadPool.getIdleThreadCount();
	}

	public int getMaxThreadCount() {
		return this.threadPool.getMaxThreadCount();
	}

	public int getMinThreadCount() {
		return this.threadPool.getMinThreadCount();
	}

	public int getWorkThreadCount() {
		return this.threadPool.getWorkThreadCount();
	}
}
