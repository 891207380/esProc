package com.raqsoft.expression.mfn.sequence;

import com.raqsoft.common.MessageManager;
import com.raqsoft.common.RQException;
import com.raqsoft.dm.Context;
import com.raqsoft.expression.Expression;
import com.raqsoft.expression.SequenceFunction;
import com.raqsoft.resources.EngineMessage;

/**
 * ��������ƽ��ֵ
 * A.avg(), A.avg(x)
 * @author RunQian
 *
 */
public class Avg extends SequenceFunction {
	public Object calculate(Context ctx) {
		if (param == null) {
			return srcSequence.average();
		} else if (param.isLeaf()) {
			Expression exp = param.getLeafExpression();
			return srcSequence.calc(exp, ctx).average();
		} else {
			MessageManager mm = EngineMessage.get();
			throw new RQException("avg" + mm.getMessage("function.invalidParam"));
		}
	}
}
