package com.raqsoft.expression.mfn.sequence;

import com.raqsoft.dm.Context;
import com.raqsoft.expression.SequenceFunction;

/**
 * ������е�Ԫ��
 * A.reset()
 * @author RunQian
 *
 */
public class Reset extends SequenceFunction {
	public Object calculate(Context ctx) {
		srcSequence.reset();
		return srcSequence;
	}
}
