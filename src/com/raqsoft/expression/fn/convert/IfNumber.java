package com.raqsoft.expression.fn.convert;

import com.raqsoft.common.MessageManager;
import com.raqsoft.common.RQException;
import com.raqsoft.dm.Context;
import com.raqsoft.expression.Function;
import com.raqsoft.resources.EngineMessage;

/**
 * ifnumber(Exp) �ж�����Exp�Ƿ�Ϊ��ֵ����
 * @author runqian
 *
 */
public class IfNumber extends Function {
	public Object calculate(Context ctx) {
		if (param == null || !param.isLeaf()) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("ifnumber" + mm.getMessage("function.invalidParam"));
		}

		Object result1 = param.getLeafExpression().calculate(ctx);
		if (result1 instanceof Number) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}
}
