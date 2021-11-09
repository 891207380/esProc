package com.raqsoft.expression.mfn.sequence;

import com.raqsoft.common.MessageManager;
import com.raqsoft.common.RQException;
import com.raqsoft.dm.Context;
import com.raqsoft.dm.Sequence;
import com.raqsoft.expression.SequenceFunction;
import com.raqsoft.resources.EngineMessage;

/**
 * ɾ��������ָ��λ�ã��������λ�����У���Ԫ��
 * A.delete(k) A.delete(p)
 * @author RunQian
 *
 */
public class Delete extends SequenceFunction {
	public Object calculate(Context ctx) {
		if (param == null || !param.isLeaf()) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("delete" + mm.getMessage("function.invalidParam"));
		}

		Object obj = param.getLeafExpression().calculate(ctx);
		if (obj instanceof Number) {
			if (option == null || option.indexOf('n') == -1) {
				srcSequence.delete(((Number)obj).intValue());
				return srcSequence;
			} else {
				return srcSequence.delete(((Number)obj).intValue());
			}
		} else if (obj instanceof Sequence || obj == null) {
			return srcSequence.delete((Sequence)obj, option);
		} else {
			MessageManager mm = EngineMessage.get();
			throw new RQException("delete" + mm.getMessage("function.paramTypeError"));
		}
	}
}
