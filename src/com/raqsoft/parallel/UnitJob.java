package com.raqsoft.parallel;

import com.raqsoft.thread.Job;

/**
 * ���ڶ��̸߳��ڵ����������
 * @author WangXiaoJun
 *
 */
class UnitJob extends Job {
	private UnitClient client;
	private UnitCommand command;
	private Response response;
	
	public UnitJob(UnitClient client, UnitCommand command) {
		this.client = client;
		this.command = command;
	}

	public void run() {
		try {
			response = client.send(command);
		} finally {
			client.close();
		}
	}
	
	public Object getResult() {
		return response.checkResult();
	}
}