package com.raqsoft.expression.fn.parallel;

import com.raqsoft.common.*;
import com.raqsoft.resources.EngineMessage;
import com.raqsoft.expression.*;
import com.raqsoft.dm.*;
import com.raqsoft.parallel.PartitionUtil;

/**
 * syncfile(hs,p)	ͬ����ǰ����p·���µ��ļ����ֻ�����hs��p·�����ý��µ��滻�Ͼɵģ���ɾ������
 * ·��p����Ϊ���·���� ���·��ʱ�� �����·����p����ʡ��
 * @author Joancy
 *
 */
public class SyncFile extends Function {
	public Node optimize(Context ctx) {
		if (param != null) {
			param.optimize(ctx);
		}
		
		return this;
	}

	public Object calculate(Context ctx) {
		Machines mcHS = new Machines();
		String path = null;
		
		if (param == null) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("sync" + mm.getMessage("function.missingParam"));
		} else if (param.isLeaf()) {
			Object obj = param.getLeafExpression().calculate(ctx);
			mcHS.set(obj);
		} else if (param.getSubSize() == 2) {
			IParam hParam = param.getSub(0);
			if (hParam == null) {
				MessageManager mm = EngineMessage.get();
				throw new RQException("sync" + mm.getMessage("function.invalidParam"));
			} else {
				Object obj = hParam.getLeafExpression().calculate(ctx);
				mcHS.set(obj);
			}
			
			IParam sub1 = param.getSub(1);
			if (sub1 != null) {
				Object op = sub1.getLeafExpression().calculate(ctx);
				if(op instanceof String){
					path = (String)op;
				} else {
					MessageManager mm = EngineMessage.get();
					throw new RQException("sync" + mm.getMessage("function.paramTypeError"));
				}
			}
		} else {
			MessageManager mm = EngineMessage.get();
			throw new RQException("sync" + mm.getMessage("function.invalidParam"));
		}
		
		PartitionUtil.syncTo(mcHS, path);
		return Boolean.TRUE;
	}
}
