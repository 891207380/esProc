package com.raqsoft.expression.mfn.file;

import com.raqsoft.common.MessageManager;
import com.raqsoft.common.RQException;
import com.raqsoft.dm.Context;
import com.raqsoft.expression.FileFunction;
import com.raqsoft.resources.EngineMessage;

/**
 * �������ļ���ȡ��ָ�����Է��أ�ʡ���򷵻����������γ����
 * f.property(p)
 * @author RunQian
 *
 */
public class Property extends FileFunction {
	public Object calculate(Context ctx) {
		if (param == null) {
			try {
				return file.getProperties(option);
			} catch( Exception e) {
				throw new RQException(e.getMessage(), e);
			}
		} else if (param.isLeaf()) {
			Object obj = param.getLeafExpression().calculate(ctx);
			if (!(obj instanceof String)) {
				MessageManager mm = EngineMessage.get();
				throw new RQException("property" + mm.getMessage("function.paramTypeError"));
			}

			try {
				return file.getProperty((String)obj, option);
			} catch( Exception e) {
				throw new RQException(e.getMessage(), e);
			}
		} else {
			MessageManager mm = EngineMessage.get();
			throw new RQException("property" + mm.getMessage("function.invalidParam"));
		}
	}
}
