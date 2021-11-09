package com.raqsoft.expression.mfn.file;

import java.io.File;

import com.raqsoft.dm.Context;
import com.raqsoft.dm.Sequence;
import com.raqsoft.expression.FileGroupFunction;

/**
 * �����������ļ�ʧ�ܣ����ô˺����ָ�����
 * f.rollback()
 * @author RunQian
 *
 */
public class FileGroupRollback extends FileGroupFunction {
	public Object calculate(Context ctx) {
		int pcount = fg.getPartitionCount();
		Sequence result = new Sequence(pcount);
		
		for (int i = 0; i < pcount; ++i) {
			File file = fg.getPartitionFile(i);
			if (Rollback.groupTableRollBack(file, null, ctx)) {
				result.add(Boolean.TRUE);
			} else {
				result.add(Boolean.FALSE);
			}
		}
		
		return result;
	}
}
