package com.raqsoft.expression.mfn.sequence;

import com.raqsoft.common.MessageManager;
import com.raqsoft.common.RQException;
import com.raqsoft.dm.Context;
import com.raqsoft.dm.Sequence;
import com.raqsoft.dm.Table;
import com.raqsoft.expression.Expression;
import com.raqsoft.expression.IParam;
import com.raqsoft.expression.SequenceFunction;
import com.raqsoft.resources.EngineMessage;

/**
 * ��ָ��������������ֶΣ�����ֶ������ֶ�ֵ�������ת�����
 * T.record(A,k)
 * A.record() A���ֶ�ֵ������ɵ�����
 * A.record(n) A���ֶ������ֶ�ֵ�������
 * @author RunQian
 *
 */
public class RecordValue extends SequenceFunction {
	public Object calculate(Context ctx) {
		if (srcSequence instanceof Table) {
			return record((Table)srcSequence, ctx);
		} else {
			if (param == null) {
				return srcSequence.toTable();
			} else if (param.isLeaf()) {
				Object obj = param.getLeafExpression().calculate(ctx);
				if (!(obj instanceof Number)) {
					MessageManager mm = EngineMessage.get();
					throw new RQException("record" + mm.getMessage("function.paramTypeError"));
				}
				
				int fcount = ((Number)obj).intValue();
				return record(srcSequence, fcount);
			} else {
				MessageManager mm = EngineMessage.get();
				throw new RQException("record" + mm.getMessage("function.invalidParam"));
			}
		}
	}

	private static Table record(Sequence seq, int fcount) {
		int len = seq.length();
		if (len == 0) {
			return null;
		} else if (fcount < 1 || fcount > len) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("record" + mm.getMessage("function.invalidParam"));
		}
		
		String []fnames = new String[fcount];
		for (int i = 1; i <= fcount; ++i) {
			Object obj = seq.getMem(i);
			if (obj instanceof String) {
				fnames[i - 1] = (String)obj;
			} else if (obj != null) {
				fnames[i - 1] = obj.toString();
			}
		}
		
		Table table = new Table(fnames, len / fcount);
		Sequence tmp = seq.get(fcount + 1, len + 1);
		table.record(1, tmp, null);
		return table;
	}
	
	private Sequence record(Table srcTable, Context ctx) {
		if (param == null) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("record" + mm.getMessage("function.missingParam"));
		}

		Expression srcExp;
		int pos = 0;
		
		if (param.isLeaf()) {
			srcExp = param.getLeafExpression();
		} else {
			if (param.getSubSize() != 2) {
				MessageManager mm = EngineMessage.get();
				throw new RQException("record" + mm.getMessage("function.invalidParam"));
			}
			
			IParam sub1 = param.getSub(0);
			IParam sub2 = param.getSub(1);
			if (sub1 == null || sub2 == null) {
				MessageManager mm = EngineMessage.get();
				throw new RQException("record" + mm.getMessage("function.invalidParam"));
			}
			
			srcExp = sub1.getLeafExpression();
			Object posObj = sub2.getLeafExpression().calculate(ctx);
			if (!(posObj instanceof Number)) {
				MessageManager mm = EngineMessage.get();
				throw new RQException("record" + mm.getMessage("function.paramTypeError"));
			}

			pos = ((Number)posObj).intValue();
		}

		Object src = srcExp.calculate(ctx);
		if (src instanceof Sequence) {
			return srcTable.record(pos, (Sequence)src, option);
		} else if (src == null) {
			return srcTable;
		} else {
			MessageManager mm = EngineMessage.get();
			throw new RQException("record" + mm.getMessage("function.paramTypeError"));
		}
	}
}
