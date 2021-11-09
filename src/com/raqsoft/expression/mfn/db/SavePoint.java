package com.raqsoft.expression.mfn.db;

import com.raqsoft.common.MessageManager;
import com.raqsoft.common.RQException;
import com.raqsoft.dm.Context;
import com.raqsoft.expression.DBFunction;
import com.raqsoft.resources.EngineMessage;

/**
 * ������Ϊspn�Ļع��㣬���ֲ���ʡ���Ҳ����ظ�
 * db.savepoint(spn)
 * @author RunQian
 *
 */
public class SavePoint extends DBFunction {
	public Object calculate(Context ctx) {
		if (param == null) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("savepoint" + mm.getMessage("function.missingParam"));
		}
		
		Object obj = param.getLeafExpression().calculate(ctx);
		if (!(obj instanceof String)) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("savepoint" + mm.getMessage("function.paramTypeError"));
		}
		
		return db.savepoint((String)obj);
	}
}
