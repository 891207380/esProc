package com.raqsoft.expression.mfn.file;

import com.raqsoft.dm.Context;
import com.raqsoft.expression.FileGroupFunction;

/**
 * �򿪸����
 * fg.open()
 * @author RunQian
 *
 */
public class FileGroupOpen extends FileGroupFunction {
	public Object calculate(Context ctx) {
		return fg.open(option, ctx);
	}
}
