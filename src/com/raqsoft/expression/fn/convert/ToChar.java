package com.raqsoft.expression.fn.convert;

import com.raqsoft.common.MessageManager;
import com.raqsoft.common.RQException;
import com.raqsoft.dm.Context;
import com.raqsoft.expression.Function;
import com.raqsoft.resources.EngineMessage;

/**
 * ���ݸ�����Unicode�������ASCII���ȡ��Ӧ���ַ�
 * @author runqian
 *
 */
public class ToChar extends Function {
	public Object calculate(Context ctx) {
		if (param == null || !param.isLeaf()) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("char" + mm.getMessage("function.invalidParam"));
		}

		Object result = param.getLeafExpression().calculate(ctx);
		if (result instanceof Number) {
			char[] c = new char[] {(char)((Number)result).intValue()};
			return new String(c);
		} else if (result == null) {
			return null;
		} else {
			MessageManager mm = EngineMessage.get();
			throw new RQException("char" + mm.getMessage("function.paramTypeError"));
		}
	}
}
