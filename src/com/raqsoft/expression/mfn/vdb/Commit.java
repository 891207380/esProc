package com.raqsoft.expression.mfn.vdb;

import com.raqsoft.dm.Context;
import com.raqsoft.expression.VDBFunction;

/**
 * �ύ����������
 * v.commit()
 * @author RunQian
 *
 */
public class Commit extends VDBFunction {
	public Object calculate(Context ctx) {
		return vdb.commit();
	}
}