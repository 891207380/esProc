package com.raqsoft.expression.fn.convert;

import com.raqsoft.common.MessageManager;
import com.raqsoft.common.RQException;
import com.raqsoft.dm.Context;
import com.raqsoft.expression.Function;
import com.raqsoft.resources.EngineMessage;

/**
 * bool(expression) �����ʽexpression����������ת��Ϊ�����͡�
 * ת�����򣺵�����ֵΪnull���ַ���"false"(��С����)������ֵfalseʱ����false�����򷵻�true��
 * @author runqian
 *
 */
public class ToBool extends Function {
	public Object calculate(Context ctx) {
		if (param == null || !param.isLeaf()) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("bool" + mm.getMessage("function.invalidParam"));
		}

		Object obj = param.getLeafExpression().calculate(ctx);
		if (obj instanceof Boolean) {
			return obj;
		} else if (obj instanceof String) {
			if (((String)obj).equals("false")) {
				return Boolean.FALSE;
			} else {
				return Boolean.TRUE;
			}
		} else if (obj == null)  {
			return Boolean.FALSE;
		} else {
			return Boolean.TRUE;
		}
	}
}
