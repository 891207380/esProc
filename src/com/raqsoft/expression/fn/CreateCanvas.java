package com.raqsoft.expression.fn;

import com.raqsoft.common.*;
import com.raqsoft.resources.EngineMessage;
import com.raqsoft.expression.*;
import com.raqsoft.dm.*;

/**
 * ���ɻ������� canvas()
 * �������ж��廭����ֱ���ڵ�Ԫ����ʹ��canvas()������
 * �����Ļ�ͼ�����п���ֱ���õ�Ԫ�����Ƶ��û��������趨��ͼ�������߻�ͼ��
 * @author runqian
 *
 */
public class CreateCanvas extends Function {

	public Object calculate(Context ctx) {
		if (param != null) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("canvas" + mm.getMessage("function.invalidParam"));
		}

		return new Canvas();
	}

	public Node optimize(Context ctx) {
		return this;
	}
}
