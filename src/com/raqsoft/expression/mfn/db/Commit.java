package com.raqsoft.expression.mfn.db;

import com.raqsoft.dm.Context;
import com.raqsoft.expression.DBFunction;

/**
 * �ύ���ݿ����
 * db.commit()
 * @author RunQian
 *
 */
public class Commit extends DBFunction {
	public Object calculate(Context ctx) {
		db.commit();
		return null;
	}
}