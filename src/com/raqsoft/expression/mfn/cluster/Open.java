package com.raqsoft.expression.mfn.cluster;

import com.raqsoft.dm.Context;
import com.raqsoft.expression.ClusterFileFunction;

/**
 * �򿪼�Ⱥ���
 * f.open()
 * @author RunQian
 *
 */
public class Open extends ClusterFileFunction {
	public Object calculate(Context ctx) {
		return file.openGroupTable(ctx);
	}
}
