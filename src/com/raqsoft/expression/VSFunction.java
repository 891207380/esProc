package com.raqsoft.expression;

import com.raqsoft.vdb.IVS;

/**
 * ���׿��Ա��������
 * h.f()
 * @author RunQian
 *
 */
public abstract class VSFunction extends MemberFunction {
	protected IVS vs;

	public boolean isLeftTypeMatch(Object obj) {
		return obj instanceof IVS;
	}
	
	public void setDotLeftObject(Object obj) {
		vs = (IVS)obj;
	}
}
