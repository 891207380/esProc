package com.raqsoft.expression.mfn.db;

import com.raqsoft.common.MessageManager;
import com.raqsoft.common.RQException;
import com.raqsoft.dm.Context;
import com.raqsoft.expression.DBFunction;
import com.raqsoft.resources.EngineMessage;

/**
 * �ع����ݿ⵽ָ���ع��㣬�޲����ǻع�ȫ�����µ�
 * db.rollback(spn)
 * @author RunQian
 *
 */
public class Rollback extends DBFunction {
	public Object calculate(Context ctx) {
		String name = null;
		if (param != null) {
			Object obj = param.getLeafExpression().calculate(ctx);
			if (!(obj instanceof String)) {
				MessageManager mm = EngineMessage.get();
				throw new RQException("rollback" + mm.getMessage("function.paramTypeError"));
			}
			
			name = (String)obj;
		}

		return db.rollback(name);
	}
}
