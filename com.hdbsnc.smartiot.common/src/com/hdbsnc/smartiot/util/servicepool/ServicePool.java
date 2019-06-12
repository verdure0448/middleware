package com.hdbsnc.smartiot.util.servicepool;

import java.util.Date;
import java.util.TimerTask;

import com.hdbsnc.smartiot.util.servicepool.future.FutureRunnable;


public interface ServicePool {
	public void execute(Runnable run) throws AlreadyClosedException;
	public Object execute(FutureRunnable task) throws InterruptedException;
	public Object execute(FutureRunnable task, int timeout) throws InterruptedException;
	public void close()throws AlreadyClosedException;

	public void addScheduleAtFixedRate(Runnable run, Date firstTime, int period);
	public void addSchedule(Runnable run, Date firstTime);

	public void addSchedule(Runnable run, Date firstTime, int period);
	public void addScheduleAtFixedRate(TimerTask task, Date firstTime, int period);
	public void addSchedule(TimerTask task, Date firstTime, int period);
	public void addSchedule(TimerTask task, Date firstTime);
	public void addSchedule(TimerTask task, long delay, int period);
	public void cancel();

	public String getStatus();

	public int getAllowedIdleCount();

	public int getMinThreadCount();

	public int getMaxThreadCount();

	public int getCreatedThreadCount();

	public int getWorkThreadCount();

	public int getIdleThreadCount();

}
