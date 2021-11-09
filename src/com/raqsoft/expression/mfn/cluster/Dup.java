package com.raqsoft.expression.mfn.cluster;

import com.raqsoft.common.MessageManager;
import com.raqsoft.common.RQException;
import com.raqsoft.dm.Context;
import com.raqsoft.expression.ClusterMemoryTableFunction;
import com.raqsoft.resources.EngineMessage;

/**
 * �Ѽ�Ⱥ�ڱ�ƴ�ɱ����ڱ�
 * T.dup()
 * @author RunQian
 *
 */
public class Dup extends ClusterMemoryTableFunction {
	public Object calculate(Context ctx) {
		if (param != null) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("dup" + mm.getMessage("function.invalidParam"));
		}
		
		return table.dup();
	}
}
