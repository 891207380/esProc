package com.raqsoft.dm.op;

import com.raqsoft.dm.Context;
import com.raqsoft.dm.Sequence;

/**
 * �ܵ��ӿ�
 * @author RunQian
 *
 */
public interface IPipe {
	/**
	 * ���ܵ���������
	 * @param seq ����
	 * @param ctx ����������
	 */
	void push(Sequence seq, Context ctx);
	
	/**
	 * �������ͽ���
	 * @param ctx
	 */
	void finish(Context ctx);
}