package com.raqsoft.expression;

import com.raqsoft.dw.MemoryTable;

/**
 * �ڱ��Ա��������
 * T.f()
 * @author RunQian
 *
 */
public abstract class MemoryTableFunction extends MemberFunction {
	protected MemoryTable table;
	
	public boolean isLeftTypeMatch(Object obj) {
		return obj instanceof MemoryTable;
	}

	public void setDotLeftObject(Object obj) {
		table = (MemoryTable)obj;
	}
}