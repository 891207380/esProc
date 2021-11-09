package com.raqsoft.expression.fn;

import com.raqsoft.common.MessageManager;
import com.raqsoft.common.RQException;
import com.raqsoft.dm.Context;
import com.raqsoft.dm.JobSpace;
import com.raqsoft.expression.Expression;
import com.raqsoft.expression.Function;
import com.raqsoft.expression.IParam;
import com.raqsoft.expression.Node;
import com.raqsoft.resources.EngineMessage;
import com.raqsoft.util.EnvUtil;

/**
 * ����ȫ�ֱ���ֵ
 * env@j(v,x) ��ȫ�ֱ���v��ֵΪx��xʡ����ɾ����ȫ�ֱ���������ʱ����ס����v�ټ���x����֤x���������v����ı䡣
 * @author runqian
 *
 */
public class EnvSet extends Function {
	public Node optimize(Context ctx) {
		return this;
	}

	public Object calculate(Context ctx) {
		if (param == null) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("env" + mm.getMessage("function.missingParam"));
		} else if (param.isLeaf()) {
			String name = param.getLeafExpression().getIdentifierName();
			EnvUtil.removeParam(name, ctx);
			return null;
		} else if (param.getSubSize() != 2) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("env" + mm.getMessage("function.invalidParam"));
		}
		
		IParam sub0 = param.getSub(0);
		if (sub0 == null || !sub0.isLeaf()) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("env" + mm.getMessage("function.invalidParam"));
		}
		
		String name = sub0.getLeafExpression().getIdentifierName();
		IParam sub1 = param.getSub(1);
		if (sub1 == null || !sub1.isLeaf()) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("env" + mm.getMessage("function.invalidParam"));
		}
		
		Expression x = sub1.getLeafExpression();
		if (option == null || option.indexOf('j') == -1) {
			return com.raqsoft.dm.Env.setParamValue(name, x, ctx);
		} else {
			JobSpace js = ctx.getJobSpace();
			return js.setParamValue(name, x, ctx);
		}
	}
}
