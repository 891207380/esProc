package com.raqsoft.expression;

import com.raqsoft.dm.cursor.ICursor;

/**
 * �α��Ա��������
 * cs.f()
 * @author RunQian
 *
 */
public abstract class CursorFunction extends MemberFunction {
	protected ICursor cursor;

	public boolean isLeftTypeMatch(Object obj) {
		return obj instanceof ICursor;
	}
	
	public void setDotLeftObject(Object obj) {
		cursor = (ICursor)obj;
	}
}