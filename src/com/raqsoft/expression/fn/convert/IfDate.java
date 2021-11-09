package com.raqsoft.expression.fn.convert;

import com.raqsoft.common.MessageManager;
import com.raqsoft.common.RQException;
import com.raqsoft.dm.Context;
import com.raqsoft.expression.Function;
import com.raqsoft.resources.EngineMessage;

/**
 * ifdate(exp) �ж�����exp�Ƿ�Ϊ�����ͻ�����ʱ������
 * @author runqian
 *
 */
public class IfDate extends Function {
	public Object calculate(Context ctx) {
		if (param == null || !param.isLeaf()) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("ifdate" + mm.getMessage("function.invalidParam"));
		}

		Object result = param.getLeafExpression().calculate(ctx);
		if (result instanceof java.sql.Time) {
			return Boolean.FALSE;
		} else if (result instanceof java.util.Date) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}
}
