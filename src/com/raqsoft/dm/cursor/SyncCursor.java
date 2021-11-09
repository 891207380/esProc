package com.raqsoft.dm.cursor;

import com.raqsoft.dm.Context;
import com.raqsoft.dm.Sequence;

/**
 * ���ڶ��α���ͬ��ȡ���������α�
 * @author RunQian
 *
 */
public class SyncCursor extends ICursor {
	private ICursor cursor;
	
	/**
	 * ����ͬ��ȡ���α�
	 * @param cursor
	 */
	public SyncCursor(ICursor cursor) {
		this.cursor = cursor;
		setDataStruct(cursor.getDataStruct());
	}
	
	// ���м���ʱ��Ҫ�ı�������
	// �̳�������õ��˱��ʽ����Ҫ�������������½������ʽ
	protected void resetContext(Context ctx) {
		if (this.ctx != ctx) {
			cursor.resetContext(ctx);
			super.resetContext(ctx);
		}
	}
	
	/**
	 * ��ȡָ�����������ݷ���
	 * @param n ����
	 * @return Sequence
	 */
	protected Sequence get(int n) {
		return cursor.fetch(n);
	}
	
	/**
	 * ����ָ������������
	 * @param n ����
	 * @return long ʵ������������
	 */
	protected long skipOver(long n) {
		return cursor.skip(n);
	}
	
	/**
	 * �����α�
	 * @return �����Ƿ�ɹ���true���α���Դ�ͷ����ȡ����false�������Դ�ͷ����ȡ��
	 */
	public boolean reset() {
		return cursor.reset();
	}

	/**
	 * �ر��α�
	 */
	public synchronized void close() {
		super.close();
		cursor.close();
	}
}