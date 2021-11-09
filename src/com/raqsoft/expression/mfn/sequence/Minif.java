package com.raqsoft.expression.mfn.sequence;

import com.raqsoft.dm.Context;
import com.raqsoft.expression.SequenceFunction;

/**
 * �����������������������Ԫ�ص���Сֵ
 * A.minif(Ai:xi,��)
 * @author RunQian
 *
 */
public class Minif extends SequenceFunction {
	public Object calculate(Context ctx) {
		return Sumif.posSelect("minif", srcSequence, param, option, ctx).min();
	}
}
