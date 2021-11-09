package com.raqsoft.expression.mfn.sequence;

import com.raqsoft.common.MessageManager;
import com.raqsoft.common.RQException;
import com.raqsoft.dm.Context;
import com.raqsoft.expression.SequenceFunction;
import com.raqsoft.resources.EngineMessage;

/**
 * �Էָ������������еĳ�Ա��Ϊ�ַ���������������
 * A.concat(d)
 * @author RunQian
 *
 */
public class Concat extends SequenceFunction {
	public Object calculate(Context ctx) {
		String sep = "";
		if (param != null) {
			Object obj = param.getLeafExpression().calculate(ctx);
			if (!(obj instanceof String)) {
				MessageManager mm = EngineMessage.get();
				throw new RQException("concat" + mm.getMessage("function.paramTypeError"));
			}

			sep = (String)obj;
		}

		return srcSequence.toString(sep, option);
	}
}
