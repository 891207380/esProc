package com.raqsoft.expression.fn.string;

import com.raqsoft.common.MessageManager;
import com.raqsoft.common.RQException;
import com.raqsoft.common.StringUtils;
import com.raqsoft.dm.Context;
import com.raqsoft.expression.Function;
import com.raqsoft.expression.IParam;
import com.raqsoft.resources.EngineMessage;

/**
 * like( stringExp, formatExp )
 * �ж��ַ����Ƿ�ƥ���ʽ������ʽ���е�*ƥ��0�������ַ���?ƥ�䵥���ַ���
 * ����ͨ��ת���ƥ��"*",���磺\*ת��Ϊ*��\\ת��Ϊ\
 * @author runqian
 *
 */
public class Like extends Function {
	public Object calculate(Context ctx) {
		if (param == null) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("like" + mm.getMessage("function.missingParam"));
		}

		if (param.getSubSize() != 2) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("like" + mm.getMessage("function.invalidParam"));
		}
		
		IParam sub1 = param.getSub(0);
		IParam sub2 = param.getSub(1);
		if (sub1 == null || sub2 == null) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("like" + mm.getMessage("function.invalidParam"));
		}

		Object o1 = sub1.getLeafExpression().calculate(ctx);
		if (o1 == null) {
			return Boolean.FALSE;
		} else if (!(o1 instanceof String)) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("like" + mm.getMessage("function.paramTypeError"));
		}

		Object o2 = sub2.getLeafExpression().calculate(ctx);
		if (o2 == null) {
			return Boolean.FALSE;
		} else if (!(o2 instanceof String)) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("like" + mm.getMessage("function.paramTypeError"));
		}

		boolean ignoreCase = option != null && option.indexOf('c') != -1;
		if (StringUtils.matches((String) o1, (String) o2, ignoreCase)) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}
}
