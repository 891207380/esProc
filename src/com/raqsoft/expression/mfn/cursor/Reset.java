package com.raqsoft.expression.mfn.cursor;

import com.raqsoft.dm.Context;
import com.raqsoft.expression.CursorFunction;

/**
 * �����α굽����ͷ��
 * cs.reset()
 * @author RunQian
 *
 */
public class Reset extends CursorFunction {
	public Object calculate(Context ctx) {
		cursor.reset();
		return cursor;
	}
}
