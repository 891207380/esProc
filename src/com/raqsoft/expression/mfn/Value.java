package com.raqsoft.expression.mfn;

import com.raqsoft.dm.Context;
import com.raqsoft.dm.Record;
import com.raqsoft.expression.MemberFunction;

/**
 * ���ؼ�¼�ļ������û�������򷵻������ֶ���ɵ����У�������Ǽ�¼�򷵻ر���
 * v.v()
 * @author RunQian
 *
 */
public class Value extends MemberFunction {
	protected Object src;
	
	public boolean isLeftTypeMatch(Object obj) {
		return true;
	}

	public void setDotLeftObject(Object obj) {
		src = obj;
	}

	public Object calculate(Context ctx) {
		if (src instanceof Record) {
			return ((Record)src).value();
		} else {
			return src;
		}
	}
}
