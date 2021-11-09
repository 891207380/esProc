package com.raqsoft.expression.mfn.xo;

import com.raqsoft.common.MessageManager;
import com.raqsoft.common.RQException;
import com.raqsoft.dm.Context;
import com.raqsoft.expression.XOFunction;
import com.raqsoft.resources.EngineMessage;

/**
 * ����xo.xlsclose()�� ��@r@w��ʽ�򿪵�Excel������Ҫ�ر�
 *
 */
public class XlsClose extends XOFunction {
	/**
	 * ����
	 */
	public Object calculate(Context ctx) {
		if (param != null) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("xlsclose"
					+ mm.getMessage("function.invalidParam"));
		}
		try {
			file.xlsclose();
			return null;
		} catch (RQException e) {
			throw e;
		} catch (Exception e) {
			throw new RQException(e.getMessage(), e);
		}
	}
}
