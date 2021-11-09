package com.raqsoft.expression.mfn.record;

import com.raqsoft.common.MessageManager;
import com.raqsoft.common.RQException;
import com.raqsoft.dm.Context;
import com.raqsoft.dm.DataStruct;
import com.raqsoft.expression.IParam;
import com.raqsoft.expression.RecordFunction;
import com.raqsoft.resources.EngineMessage;

/**
 * ȡ��¼ָ���ֶε�ֵ������ָ���ֶε�ֵ
 * r.field(F) r.field(F, v)
 * @author RunQian
 *
 */
public class FieldValue extends RecordFunction {
	private String prevName; // ��һ�μ�����ֶ���
	private DataStruct prevDs; // ��һ����¼�����ݽṹ
	private int prevCol; // ��һ����¼�ֶε����
	
	public Object calculate(Context ctx) {
		if (param == null) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("field" + mm.getMessage("function.missingParam"));
		} else if (param.isLeaf()) {
			Object obj = param.getLeafExpression().calculate(ctx);
			if (obj instanceof Number) {
				int findex = ((Number)obj).intValue();
				if (findex > 0) {
					// �ֶδ�0��ʼ����
					findex--;
				} else if (findex == 0) {
					return null;
				} // С��0�Ӻ���
				
				return srcRecord.getFieldValue2(findex);
			} else if (obj instanceof String) {
				if (obj == prevName && srcRecord.dataStruct() == prevDs) {
					if (prevCol >= 0) {
						return srcRecord.getNormalFieldValue(prevCol);
					} else {
						return null;
					}
				}
				
				prevName = (String)obj;
				prevDs = srcRecord.dataStruct();
				prevCol = prevDs.getFieldIndex(prevName);
				if (prevCol >= 0) {
					return srcRecord.getNormalFieldValue(prevCol);
				} else {
					return null;
				}
			} else {
				MessageManager mm = EngineMessage.get();
				throw new RQException("field" + mm.getMessage("function.paramTypeError"));
			}
		} else {
			if (param.getSubSize() != 2) {
				MessageManager mm = EngineMessage.get();
				throw new RQException("field" + mm.getMessage("function.invalidParam"));
			}

			IParam sub0 = param.getSub(0);
			IParam sub1 = param.getSub(1);
			if (sub0 == null || sub1 == null) {
				MessageManager mm = EngineMessage.get();
				throw new RQException("field" + mm.getMessage("function.invalidParam"));
			}

			Object obj = sub0.getLeafExpression().calculate(ctx);
			Object value = sub1.getLeafExpression().calculate(ctx);
			if (obj instanceof Number) {
				int findex = ((Number)obj).intValue();
				if (findex > 0) {
					// �ֶδ�0��ʼ����
					findex--;
				} else if (findex == 0) {
					return null;
				} // С��0�Ӻ���
				
				srcRecord.set2(findex, value);
			} else if (obj instanceof String) {				
				int findex = srcRecord.getFieldIndex((String)obj);
				if (findex >= 0) {
					srcRecord.set2(findex, value);
				}
			} else {
				MessageManager mm = EngineMessage.get();
				throw new RQException("field" + mm.getMessage("function.paramTypeError"));
			}

			return null;
		}
	}
}
