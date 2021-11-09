package com.raqsoft.expression.mfn.sequence;

import com.raqsoft.dm.Context;
import com.raqsoft.expression.SequenceFunction;

/**
 * �����������������Ԫ�ص����ֵ
 * A.maxif(Ai:xi,��)
 * @author RunQian
 *
 */
public class Maxif extends SequenceFunction {
	public Object calculate(Context ctx) {
		return Sumif.posSelect("maxif", srcSequence, param, option, ctx).max();
	}
}
