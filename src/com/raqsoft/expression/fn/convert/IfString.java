package com.raqsoft.expression.fn.convert;

import com.raqsoft.common.MessageManager;
import com.raqsoft.common.RQException;
import com.raqsoft.dm.Context;
import com.raqsoft.expression.Function;
import com.raqsoft.resources.EngineMessage;

/**
 * ifstring(x) �ж�����x�Ƿ�Ϊ�ַ�������
 * @author runqian
 *
 */
public class IfString extends Function {
	public Object calculate(Context ctx) {
		if (param == null || !param.isLeaf()) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("ifstring" + mm.getMessage("function.invalidParam"));
		}

		Object obj = param.getLeafExpression().calculate(ctx);
		return (obj instanceof String) ? Boolean.TRUE : Boolean.FALSE;
	}
}
