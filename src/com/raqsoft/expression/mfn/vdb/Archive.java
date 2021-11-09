package com.raqsoft.expression.mfn.vdb;

import com.raqsoft.common.MessageManager;
import com.raqsoft.common.RQException;
import com.raqsoft.dm.Context;
import com.raqsoft.expression.VSFunction;
import com.raqsoft.resources.EngineMessage;

/**
 * �鵵ָ��·�����鵵��·��������д��ռ�õĿռ���С����ѯ�ٶȻ���
 * v.archive(p)
 * @author RunQian
 *
 */
public class Archive extends VSFunction {
	public Object calculate(Context ctx) {
		if (param == null) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("archive" + mm.getMessage("function.missingParam"));
		} else if (param.isLeaf()) {
			Object path = param.getLeafExpression().calculate(ctx);
			return vs.archive(path);
		} else {
			MessageManager mm = EngineMessage.get();
			throw new RQException("archive" + mm.getMessage("function.invalidParam"));
		}
	}
}