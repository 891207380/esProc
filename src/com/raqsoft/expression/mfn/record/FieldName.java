package com.raqsoft.expression.mfn.record;

import com.raqsoft.common.MessageManager;
import com.raqsoft.common.RQException;
import com.raqsoft.dm.Context;
import com.raqsoft.dm.Sequence;
import com.raqsoft.expression.RecordFunction;
import com.raqsoft.resources.EngineMessage;

/**
 * ���ؼ�¼ָ���ֶλ������ֶε��ֶ���
 * r.fname(i) r.fname()
 * @author RunQian
 *
 */
public class FieldName extends RecordFunction {
	public Object calculate(Context ctx) {
		if (param == null) {
			String []names = srcRecord.dataStruct().getFieldNames();
			return new Sequence(names);
		} else if (param.isLeaf()) {
			Object indexObj = param.getLeafExpression().calculate(ctx);
			if (!(indexObj instanceof Number)) {
				MessageManager mm = EngineMessage.get();
				throw new RQException("fname" + mm.getMessage("function.paramTypeError"));
			}

			// �ֶδ�0��ʼ����
			int findex = ((Number)indexObj).intValue() - 1;
			if (findex < 0) {
				MessageManager mm = EngineMessage.get();
				throw new RQException(findex + mm.getMessage("ds.fieldNotExist"));
			}

			return srcRecord.dataStruct().getFieldName(findex);
		} else {
			MessageManager mm = EngineMessage.get();
			throw new RQException("fname" + mm.getMessage("function.invalidParam"));
		}
	}
}
