package com.raqsoft.expression.mfn.file;

import com.raqsoft.dm.Context;
import com.raqsoft.expression.FileFunction;

/**
 * �ж��ļ��Ƿ����
 * f.exists()
 * @author RunQian
 *
 */
public class Exists extends FileFunction {
	public Object calculate(Context ctx) {
		return Boolean.valueOf(file.isExists());
	}
}
