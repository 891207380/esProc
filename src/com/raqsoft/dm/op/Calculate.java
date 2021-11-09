package com.raqsoft.dm.op;

import com.raqsoft.dm.Context;
import com.raqsoft.dm.Sequence;
import com.raqsoft.expression.Expression;
import com.raqsoft.expression.Function;

/**
 * �α��ܵ����ӳټ��㴦����
 * @author RunQian
 *
 */
public class Calculate extends Operation {
	private Expression exp; // ������ʽ

	/**
	 * ���������
	 * @param exp ������ʽ
	 */
	public Calculate(Expression exp) {
		this(null, exp);
	}
	
	/**
	 * ���������
	 * @param function ��ǰ������Ӧ�ı��ʽ��ĺ���
	 * @param exp ������ʽ
	 */
	public Calculate(Function function, Expression exp) {
		super(function);
		this.exp = exp;
	}
	
	/**
	 * �����������ڶ��̼߳��㣬��Ϊ���ʽ���ܶ��̼߳���
	 * @param ctx ����������
	 * @return Operation
	 */
	public Operation duplicate(Context ctx) {
		Expression dupExp = dupExpression(exp, ctx);
		return new Calculate(function, dupExp);
	}
	
	/**
	 * �����α��ܵ���ǰ���͵�����
	 * @param seq ����
	 * @param ctx ����������
	 * @return
	 */
	public Sequence process(Sequence seq, Context ctx) {
		return seq.calc(exp, ctx);
	}
}
