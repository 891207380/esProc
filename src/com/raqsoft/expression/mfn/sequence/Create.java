package com.raqsoft.expression.mfn.sequence;

import com.raqsoft.dm.Context;
import com.raqsoft.expression.SequenceFunction;

/**
 * ʹ���������е�һ����¼�����ݽṹ�����������
 * T.create() P.create()
 * @author RunQian
 *
 */
public class Create extends SequenceFunction {
	public Object calculate(Context ctx) {
		return srcSequence.create();
	}
}
