package com.raqsoft.expression.mfn.sequence;

import com.raqsoft.common.MessageManager;
import com.raqsoft.common.RQException;
import com.raqsoft.dm.Context;
import com.raqsoft.expression.Expression;
import com.raqsoft.expression.SequenceFunction;
import com.raqsoft.resources.EngineMessage;

/**
 * �������г�Ա�Ĳ���
 * A.union() A.union(x)��A�����е�����
 * @author RunQian
 *
 */
public class Union extends SequenceFunction {
	public Object calculate(Context ctx) {
		if (param == null) {
			return srcSequence.union(option);
		} else if (param.isLeaf()) {
			Expression exp = param.getLeafExpression();
			return srcSequence.calc(exp, ctx).union(option);
		} else {
			MessageManager mm = EngineMessage.get();
			throw new RQException("union" + mm.getMessage("function.invalidParam"));
		}
	}
}