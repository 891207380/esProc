package com.raqsoft.expression;

import com.raqsoft.dm.Record;

/**
 * ��¼��Ա��������
 * r.f()
 * @author RunQian
 *
 */
public abstract class RecordFunction extends MemberFunction {
	protected Record srcRecord; // Դ��¼
	
	public boolean isLeftTypeMatch(Object obj) {
		return obj instanceof Record;
	}
	
	public void setDotLeftObject(Object obj) {
		srcRecord = (Record)obj;
	}
}
