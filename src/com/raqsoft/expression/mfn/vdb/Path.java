package com.raqsoft.expression.mfn.vdb;

import com.raqsoft.dm.Context;
import com.raqsoft.expression.VSFunction;

/**
 * ���ص�ǰ·���Ľ�ֵ
 * h.path()
 * @author RunQian
 *
 */
public class Path extends VSFunction {
	public Object calculate(Context ctx) {
		return vs.path(option);
	}
}
