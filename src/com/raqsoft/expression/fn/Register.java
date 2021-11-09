package com.raqsoft.expression.fn;

import com.raqsoft.common.MessageManager;
import com.raqsoft.common.RQException;
import com.raqsoft.dm.Context;
import com.raqsoft.expression.Function;
import com.raqsoft.expression.FunctionLib;
import com.raqsoft.expression.IParam;
import com.raqsoft.expression.Node;
import com.raqsoft.resources.EngineMessage;

/**
 * ��dfx�ļ��Ǽ�Ϊ����
 * register(f,dfx) 
 * �Ǽ�dfx�ļ�Ϊ����f��֮��ú��������������ű���ʹ�ã��������ʽд��Ϊ��f(xi,...)������xi,...Ϊdfx�ļ��еĲ�����
 * ����������ö��ŷָ���
 * @author runqian
 *
 */
public class Register extends Function {
	public Node optimize(Context ctx) {
		return this;
	}

	public Object calculate(Context ctx) {
		if (param == null) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("register" + mm.getMessage("function.missingParam"));
		} else if (param.isLeaf()) {
			Object name = param.getLeafExpression().calculate(ctx);
			if (!(name instanceof String)) {
				MessageManager mm = EngineMessage.get();
				throw new RQException("register" + mm.getMessage("function.paramTypeError"));
			}
			
			FunctionLib.removeDFXFunction((String)name);
			return name;
		} else if (param.getSubSize() != 2) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("register" + mm.getMessage("function.invalidParam"));
		}
		
		IParam sub0 = param.getSub(0);
		IParam sub1 = param.getSub(1);
		if (sub0 == null || sub1 == null) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("register" + mm.getMessage("function.invalidParam"));
		}
		
		Object name = sub0.getLeafExpression().calculate(ctx);
		if (!(name instanceof String)) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("register" + mm.getMessage("function.paramTypeError"));
		}
		
		Object dfx = sub1.getLeafExpression().calculate(ctx);
		if (!(dfx instanceof String)) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("register" + mm.getMessage("function.paramTypeError"));
		}
		
		FunctionLib.addDFXFunction((String)name, (String)dfx);
		return name;
	}
}
