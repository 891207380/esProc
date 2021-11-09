package com.raqsoft.expression.mfn;

import com.raqsoft.common.ObjectCache;
import com.raqsoft.dm.Context;
import com.raqsoft.dm.SerialBytes;
import com.raqsoft.expression.MemberFunction;

/**
 * ȡ�źŵĳ���
 * k.len()
 * @author RunQian
 *
 */
public class Len extends MemberFunction {
	protected SerialBytes sb; // �ź�

	public boolean isLeftTypeMatch(Object obj) {
		return obj instanceof SerialBytes;
	}
	
	public void setDotLeftObject(Object obj) {
		sb = (SerialBytes)obj;
	}

	public Object calculate(Context ctx) {
		return ObjectCache.getInteger(sb.length());
	}
}
