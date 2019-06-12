package com.hdbsnc.smartiot.util.servicepool.pool;


import com.hdbsnc.smartiot.util.servicepool.AlreadyClosedException;

public class ThreadPool extends ThreadGroup {
	public static final int DEAFULT_MAX_THREAD_COUNT = 30;
	public static final int DEAFULT_MIN_THREAD_COUNT = 0;
	public static final int DEFAULT_INITIAL_THREAD_COUNT = 10;
	public static final int DEFAULT_ALLOWED_IDLE_COUNT = 5;

	private WorkQueue pool = new WorkQueue();
	private int minThreadCount;
	private int maxThreadCount;
	private int createdThreadCount = 0;
	private int workThreadCount = 0;

	/**
	 * idleThreadCount = createdThreadCount - workThreadCount
	 */
	private int idleThreadCount = 0;
	private int allowedIdleCount = 0;
	private boolean closed = false;
	private static int groupId = 0;
	private static int threadId = 0;

	/**
	 * 
	 * 
	 * @param initThreadCount 최초 기본 생생 쓰레드 개수 
	 * @param maxThreadCount 최대 생성가능한 쓰레드 개수 
	 * @param minThreadCount 생성 후 재거할 필요 없는 최소 쓰레드 개수
	 * @param allowedIdleCount 쓰레드 생성 후 사용하지 않고 대기 가능한 개수 
	 */
	public ThreadPool(int initThreadCount, int maxThreadCount, int minThreadCount, int allowedIdleCount) {
		super(ThreadPool.class.getName()+Integer.toString(groupId++) );

		if (minThreadCount < 0) minThreadCount = 0;
		if (initThreadCount < minThreadCount) initThreadCount = minThreadCount;
		if (maxThreadCount < minThreadCount || maxThreadCount < initThreadCount) maxThreadCount = Integer.MAX_VALUE;

		if (allowedIdleCount < 0) allowedIdleCount = DEFAULT_ALLOWED_IDLE_COUNT;

		this.minThreadCount = minThreadCount;
		this.maxThreadCount = maxThreadCount;
		this.createdThreadCount = initThreadCount;
		this.idleThreadCount = initThreadCount;
		this.allowedIdleCount = allowedIdleCount;
		for (int i = 0 ; i < this.createdThreadCount ; i++ ) {
			new PooledThread().start();
		}

	}

	public ThreadPool(int initThreadCount, int maxThreadCount, int minThreadCount) {
		this(initThreadCount, maxThreadCount, minThreadCount, DEFAULT_ALLOWED_IDLE_COUNT);
	}

	public synchronized void execute(Runnable work) throws AlreadyClosedException {
		if (closed) throw new AlreadyClosedException();
		increasePooledThread();
		pool.enqueue( work );
	}

	public synchronized void close() throws AlreadyClosedException {
		if (closed) throw new AlreadyClosedException();
		closed = true;
		pool.close();
	}

	private void increasePooledThread() {
		synchronized(pool) {
			if (idleThreadCount == 0 && createdThreadCount < maxThreadCount) {
				new PooledThread().start();
				createdThreadCount ++;
				idleThreadCount ++;
			}
		}
	}

	private void beginRun() {
		synchronized(pool) {
			workThreadCount ++;
			idleThreadCount --;
		}
	}
	
	private void endRun(){
		synchronized(pool) {
			workThreadCount --;
			idleThreadCount ++;
		}
	}

	private boolean terminate() {
		synchronized(pool) {
			if (idleThreadCount > allowedIdleCount && createdThreadCount > minThreadCount) {
				synchronized(pool){
					createdThreadCount --;
					idleThreadCount --;
				}
				return true;
			}
			return false;
		}
	}

	private class PooledThread extends Thread {

		public PooledThread() {
			super(ThreadPool.this, "SP#"+threadId++);
		}

		public void run() {
			try {
				while( !closed ) {
					Runnable work = pool.dequeue();

					beginRun();
					try{
						work.run();
					}catch (RuntimeException e){
						e.printStackTrace();
						System.out.println("[SP#"+this.getId()+"]"+e.getMessage());
					}catch (Exception e){
						e.printStackTrace();
						System.out.println("[SP#"+this.getId()+"]"+e.getMessage());
					}
					endRun();
					if (terminate() ) {
						break;
					}
				}
			} catch(AlreadyClosedException ex) {
			} catch(InterruptedException ex) {
			} catch(Exception ex){
			}
		}
	} // end of PooledThread

	public String getStatus() {
		synchronized(pool) {
			return new StringBuffer("Total Thread: ").append(String.valueOf(createdThreadCount))
			.append("  Idle Thread: ").append(String.valueOf(idleThreadCount))
			.append("  tWork Thread: ").append(String.valueOf(workThreadCount)).toString();
		}
	}

	public int getAllowedIdleCount() {
		return allowedIdleCount;
	}

	public int getMinThreadCount() {
		return minThreadCount;
	}

	public int getMaxThreadCount() {
		return maxThreadCount;
	}

	public int getCreatedThreadCount() {
		return createdThreadCount;
	}

	public int getWorkThreadCount() {
		return workThreadCount;
	}

	public int getIdleThreadCount() {
		return idleThreadCount;
	}


}