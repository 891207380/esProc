package com.raqsoft.expression.mfn.file;

import com.raqsoft.dm.Context;
import com.raqsoft.expression.FileFunction;

/**
 * ȡ�ļ�����޸�����ʱ��
 * f.date()
 * @author RunQian
 *
 */
public class Date extends FileFunction {
	public Object calculate(Context ctx) {
		return file.lastModified();
	}
}
