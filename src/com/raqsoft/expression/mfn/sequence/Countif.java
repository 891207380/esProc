package com.raqsoft.expression.mfn.sequence;

import com.raqsoft.dm.Context;
import com.raqsoft.expression.SequenceFunction;

/**
 * �����������������Ԫ��ȡֵΪ�棨�ǿղ��Ҳ���false���ĸ���
 * A.countif(Ai:xi,��)
 * @author RunQian
 *
 */
public class Countif extends SequenceFunction {
	public Object calculate(Context ctx) {
		return Sumif.posSelect("countif", srcSequence, param, option, ctx).count(option);
	}
}
