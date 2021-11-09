package com.raqsoft.expression.fn.math;

import com.raqsoft.common.MessageManager;
import com.raqsoft.common.RQException;
import com.raqsoft.resources.EngineMessage;
import com.raqsoft.expression.Function;
import com.raqsoft.dm.Context;
import com.raqsoft.util.Variant;

/**
 * ����ĳһ���ֵķ�˫������ֵasinh(z)=ln(z+sqrt(z*z+1))
 * @author yanjing
 *
 */
public class Arcsinh extends Function {

	public Object calculate(Context ctx) {
		if (param == null || !param.isLeaf()) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("asinh" + mm.getMessage("function.invalidParam"));
		}
		Object result1 = param.getLeafExpression().calculate(ctx);
		if (result1 == null) {
			return result1;
		}
		if (! (result1 instanceof Number)) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("asinh" + mm.getMessage("function.paramTypeError"));
		}
		double z=Variant.doubleValue(result1);
		return new Double(Math.log(z+Math.sqrt(z*z+1)));
	}

}
