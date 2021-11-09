package com.raqsoft.expression.mfn.vdb;

import com.raqsoft.common.MessageManager;
import com.raqsoft.common.RQException;
import com.raqsoft.dm.Context;
import com.raqsoft.expression.VSFunction;
import com.raqsoft.resources.EngineMessage;

/**
 * ���õ�ǰĿ¼��������д����������ڴ�·��
 * h.home(p)
 * @author RunQian
 *
 */
public class Home extends VSFunction {
	public Object calculate(Context ctx) {
		if (param == null) {
			return vs;
		} else if (param.isLeaf()) {
			Object path = param.getLeafExpression().calculate(ctx);
			return vs.home(path);
		} else {
			MessageManager mm = EngineMessage.get();
			throw new RQException("home" + mm.getMessage("function.invalidParam"));
		}
	}
}
