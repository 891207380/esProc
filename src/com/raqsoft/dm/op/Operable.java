package com.raqsoft.dm.op;

import com.raqsoft.dm.Context;

/**
 * ���Ը�������Ľӿ�
 * @author WangXiaoJun
 *
 */
public interface Operable {
	/**
	 * ��������
	 * @param op ����
	 * @param ctx ����������
	 * @return Operable
	 */
	Operable addOperation(Operation op, Context ctx);
}