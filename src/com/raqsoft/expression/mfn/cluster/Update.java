package com.raqsoft.expression.mfn.cluster;

import com.raqsoft.common.MessageManager;
import com.raqsoft.common.RQException;
import com.raqsoft.dm.Context;
import com.raqsoft.dm.Sequence;
import com.raqsoft.expression.ClusterTableMetaDataFunction;
import com.raqsoft.resources.EngineMessage;

/**
 * ���¼�Ⱥ������ݣ��¼�ֵ����룬���ּ����������������
 * T.update(P)
 * @author RunQian
 *
 */
public class Update extends ClusterTableMetaDataFunction {
	public Object calculate(Context ctx) {
		String opt = option;
		if (param == null) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("update" + mm.getMessage("function.missingParam"));
		}
		
		boolean hasN = opt != null && opt.indexOf('n') != -1;
		Object obj = param.getLeafExpression().calculate(ctx);
		if (obj instanceof Sequence) {
			if (!hasN) {
				table.update((Sequence)obj, opt);
				return table;
			}
			return table.update((Sequence)obj, opt);
		} else if (obj != null) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("update" + mm.getMessage("function.paramTypeError"));
		} else {
			if (!hasN) {
				return table;
			} else {
				return null;
			}
		}
	}
}
