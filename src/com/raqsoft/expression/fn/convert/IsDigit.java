package com.raqsoft.expression.fn.convert;

import com.raqsoft.common.MessageManager;
import com.raqsoft.common.RQException;
import com.raqsoft.dm.Context;
import com.raqsoft.expression.Function;
import com.raqsoft.resources.EngineMessage;

/**
 * isdigit(string) �ж��ַ���string�Ƿ�ȫ�����ֹ��ɡ����stringΪ����������Ϊascii�룬�ж϶�Ӧ���ַ��Ƿ�Ϊ���֡�
 * @author runqian
 *
 */
public class IsDigit extends Function {
	public Object calculate(Context ctx) {
		if (param == null || !param.isLeaf()) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("isdigit" + mm.getMessage("function.invalidParam"));
		}

		Object result1 = param.getLeafExpression().calculate(ctx);
		if (result1 instanceof String) {
			String str = (String)result1;
			if (str.length() == 0) return Boolean.FALSE;

			for (int i = 0, len = str.length(); i < len; ++i) {
				char c = str.charAt(i);
				if (c < '0' || c > '9') {
					return Boolean.FALSE;
				}
			}

			return Boolean.TRUE;
		} else if (result1 instanceof Number) {
			int c = ((Number)result1).intValue();
			if (c >= '0' && c <= '9') {
				return Boolean.TRUE;
			} else {
				return Boolean.FALSE;
			}
		} else {
			return Boolean.FALSE;
		}
	}
}
