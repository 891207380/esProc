package com.raqsoft.expression.fn.datetime;

import java.util.Date;

import com.raqsoft.common.DateFactory;
import com.raqsoft.common.MessageManager;
import com.raqsoft.common.RQException;
import com.raqsoft.dm.Context;
import com.raqsoft.expression.Function;
import com.raqsoft.resources.EngineMessage;
import com.raqsoft.util.Variant;

/**
 * minute(datetimeExp) ������ʱ��������datetimeExp�У���÷��ӵ���Ϣ
 * @author runqian
 *
 */
public class Minute extends Function {
	public Object calculate(Context ctx) {
		if (param == null || !param.isLeaf()) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("minute" + mm.getMessage("function.invalidParam"));
		}
		

		Object result = param.getLeafExpression().calculate(ctx);
		if (result == null) {
			return null;
		}
		
		if (result instanceof String) {
			result = Variant.parseDate((String)result);
		}
		
		if (result instanceof Date) {
			return DateFactory.get().minute((Date)result);
		} else {
			MessageManager mm = EngineMessage.get();
			throw new RQException("minute" + mm.getMessage("function.paramTypeError"));
		}
	}
}
