package com.raqsoft.expression.mfn.table;

import com.raqsoft.dm.Context;
import com.raqsoft.expression.ParamInfo2;
import com.raqsoft.expression.TableFunction;

/**
 * �������ֶν���������
 * T.rename(F:F',��)
 * @author RunQian
 *
 */
public class Rename extends TableFunction {
	public Object calculate(Context ctx) {
		ParamInfo2 pi = ParamInfo2.parse(param, "rename", true, false);
		String []srcFields = pi.getExpressionStrs1();
		String []newFields = pi.getExpressionStrs2();
		srcTable.rename(srcFields, newFields);
		return srcTable;
	}
}
