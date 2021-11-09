package com.raqsoft.expression.fn;

import com.raqsoft.common.MessageManager;
import com.raqsoft.common.RQAgent;
import com.raqsoft.common.RQException;
import com.raqsoft.dm.Context;
import com.raqsoft.expression.Function;
import com.raqsoft.expression.Node;
import com.raqsoft.resources.EngineMessage;

/**
 * ���������ռ�ڴ�ռ�
 * sizeof(x) �������x��ռ�ڴ�ռ䣬����ֵ��λΪ�ֽڣ�B��
 * JVM����ʱ��Ҫ���ش����� -javaagent.RQAgent.jar
 * @author runqian
 *
 */
public class Sizeof extends Function {
	public Node optimize(Context ctx) {
		if (param != null) param.optimize(ctx);
		return this;
	}

	public Object calculate(Context ctx) {
		if (param == null) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("sizeof" + mm.getMessage("function.missingParam"));
		} else if (param.isLeaf()) {
			Object obj = param.getLeafExpression().calculate(ctx);
			return RQAgent.sizeof(obj, true);
		} else {
			MessageManager mm = EngineMessage.get();
			throw new RQException("sizeof" + mm.getMessage("function.invalidParam"));
		}
	}
}
