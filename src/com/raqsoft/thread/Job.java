package com.raqsoft.thread;

import com.raqsoft.common.RQException;

/**
 * ������󣬿����ύ��ThreadPool����JobThreadִ��
 * @author WangXiaoJun
 *
 */
public abstract class Job implements Runnable {
	private boolean isFinished; // �����Ƿ������
	private Throwable error; // ����ִ�й����е��쳣��Ϣ��û������Ϊ��
	
	/**
	 * �ȴ�����ִ����
	 */
	public final synchronized void join() {
		if (!isFinished) {
			try {
				wait();
			} catch (InterruptedException e) {
				throw new RQException(e);
			}
		}
		
		if (error != null) {
			if (error instanceof RQException) {
				throw (RQException)error;
			} else {
				throw new RQException(error);
			}
		}
	}

	void reset() {
		isFinished = false;
		error = null;
	}

	synchronized void finish() {
		isFinished = true;
		notify();
	}
	
	void setError(Throwable error) {
		this.error = error;
	}
}
