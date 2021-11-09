package com.raqsoft.expression.fn.datetime;

import java.util.Date;

import com.raqsoft.common.MessageManager;
import com.raqsoft.common.RQException;
import com.raqsoft.dm.Context;
import com.raqsoft.expression.Function;
import com.raqsoft.expression.IParam;
import com.raqsoft.expression.Node;
import com.raqsoft.resources.EngineMessage;
import com.raqsoft.util.Variant;

/**
 * elapse(dateExp, n) ������n��/ n��/ n����µ���������dateExp��
 * @author runqian
 *
 */
public class Elapse extends Function {
	public Node optimize(Context ctx) {
		if (param != null) param.optimize(ctx);
		return this;
	}

	public Object calculate(Context ctx) {
		if (param == null) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("elapse" + mm.getMessage("function.missingParam"));
		} else if (param.getSubSize() != 2) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("elapse" + mm.getMessage("function.invalidParam"));
		}

		IParam sub1 = param.getSub(0);
		IParam sub2 = param.getSub(1);
		if (sub2 == null) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("elapse" + mm.getMessage("function.invalidParam"));
		}

		Object result1 = null;
		if (sub1 != null) {
			result1 = sub1.getLeafExpression().calculate(ctx);
		}
		
		if (result1 == null) {
			result1 = new java.sql.Timestamp(System.currentTimeMillis());
		} else if (result1 instanceof String) {
			result1 = Variant.parseDate((String)result1);
		}

		Object result2 = sub2.getLeafExpression().calculate(ctx);
		if (!(result1 instanceof Date) || !(result2 instanceof Number)) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("elapse" + mm.getMessage("function.paramTypeError"));
		}

		return Variant.elapse((Date)result1, ((Number)result2).intValue(), option);
	}
}
