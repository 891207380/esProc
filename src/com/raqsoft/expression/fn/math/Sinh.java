package com.raqsoft.expression.fn.math;

import com.raqsoft.common.MessageManager;
import com.raqsoft.common.RQException;
import com.raqsoft.resources.EngineMessage;
import com.raqsoft.expression.Function;
import com.raqsoft.expression.Expression;
import com.raqsoft.dm.Context;
import com.raqsoft.util.Variant;

/**
 * ����ĳһ���ֵ�˫������ֵsinh(z)=(e^z-e^(-z))/2
 * @author yanjing
 *
 */
public class Sinh extends Function {

	public Object calculate(Context ctx) {
		if (param == null || !param.isLeaf()) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("sinh" + mm.getMessage("function.invalidParam"));
		}
		Expression param1 = param.getLeafExpression();
		Object result1 = param1.calculate(ctx);
		if (result1 == null) {
			return result1;
		}
		if (! (result1 instanceof Number)) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("sinh" + mm.getMessage("function.paramTypeError"));
		}
		double z=Variant.doubleValue(result1);
		return new Double(Math.sinh(z));
	}

}
