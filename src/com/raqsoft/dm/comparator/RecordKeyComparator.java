package com.raqsoft.dm.comparator;

import java.util.Comparator;
import com.raqsoft.util.Variant;
import com.raqsoft.dm.Record;

/**
 * ���ռ�¼�������бȽϵıȽ���
 * @author WangXiaoJun
 *
 */
public class RecordKeyComparator implements Comparator<Object> {
	public int compare(Object o1, Object o2) {
		if (o1 == null) {
			return (o2 == null) ? 0 : -1;
		}

		return Variant.compare(((Record)o1).value(), ((Record)o2).value());
	}
}
