package com.raqsoft.expression.mfn.cursor;

import com.raqsoft.common.MessageManager;
import com.raqsoft.common.RQException;
import com.raqsoft.dm.Context;
import com.raqsoft.expression.CursorFunction;
import com.raqsoft.expression.Expression;
import com.raqsoft.resources.EngineMessage;

/**
 * ����α����������ܣ�����ֵ����
 * cs.total(y,��) ֻ��һ��yʱ���ص�ֵ
 * @author RunQian
 *
 */
public class Total extends CursorFunction {
	public Object calculate(Context ctx) {
		if (param == null) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("total" + mm.getMessage("function.missingParam"));
		}
		
		Expression []exps = param.toArray("total", false);
		return cursor.total(exps, ctx);
	}
}
