package com.raqsoft.expression.mfn.file;

import com.raqsoft.dm.Context;
import com.raqsoft.expression.FileFunction;

/**
 * ȡ�ļ���С
 * f.size()
 * @author RunQian
 *
 */
public class Size extends FileFunction {
	public Object calculate(Context ctx) {
		return new Long(file.size());
	}
}
