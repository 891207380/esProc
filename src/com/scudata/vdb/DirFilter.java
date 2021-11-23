package com.scudata.vdb;

import com.scudata.dm.Sequence;
import com.scudata.util.Variant;

/**
 * Ŀ¼������
 * @author RunQian
 *
 */
class DirFilter {
	private Object rightValue;
	private Sequence values; // ���б�ʾ������ϵ

	public DirFilter(Object rightValue) {
		this.rightValue = rightValue;
		if (rightValue instanceof Sequence) {
			values = (Sequence)rightValue;
		}
	}
	
	public boolean match(Object value) {
		if (values == null) {
			if (rightValue != null) {
				return Variant.isEquals(value, rightValue);
			} else {
				return true;
			}
		} else {
			return values.contains(value, false);
		}
	}
}