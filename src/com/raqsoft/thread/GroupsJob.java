package com.raqsoft.thread;

import com.raqsoft.dm.Context;
import com.raqsoft.dm.cursor.ICursor;
import com.raqsoft.dm.op.IGroupsResult;
import com.raqsoft.dm.Sequence;
import com.raqsoft.dm.Table;
import com.raqsoft.expression.Expression;

/**
 * ���α������ִ�з������������
 * @author WangXiaoJun
 *
 */
public class GroupsJob extends Job {
	private ICursor cursor; // �����α�
	private Sequence seq; // ��������
	
	private Expression[] exps; // �����ֶα��ʽ����
	private String[] names; // �����ֶ�������
	private Expression[] calcExps; // �����ֶα��ʽ����
	private String[] calcNames; // �����ֶ�������
	private String opt; // ѡ��
	private Context ctx; // ����������
	
	private Table table; // �״η���ķ�����
	private int groupCount = -1; // @n��ʽ����ʱ�û����õĽ��������
	
	public GroupsJob(ICursor cursor, Expression[] exps, String[] names,
			Expression[] calcExps, String[] calcNames, String opt, Context ctx) {
		this.cursor = cursor;
		this.exps = exps;
		this.names = names;
		this.calcExps = calcExps;
		this.calcNames = calcNames;
		this.opt = opt;
		this.ctx = ctx;
	}
	
	public GroupsJob(Sequence seq, Expression[] exps, String[] names,
			Expression[] calcExps, String[] calcNames, String opt, Context ctx) {
		this.seq = seq;
		this.exps = exps;
		this.names = names;
		this.calcExps = calcExps;
		this.calcNames = calcNames;
		this.opt = opt;
		this.ctx = ctx;
	}
	
	/**
	 * ȡ�״η���ķ�����
	 * @return Table
	 */
	public Table getResult() {
		return table;
	}
	
	/**
	 * @nѡ��ʹ�ã����õĽ��������
	 * @param groupCount
	 */
	public void setGroupCount(int groupCount) {
		this.groupCount = groupCount;
	}

	public void run() {
		IGroupsResult groups = IGroupsResult.instance(exps, names, calcExps, calcNames, opt, ctx);
		if (groupCount > 1) {
			groups.setGroupCount(groupCount);
		}
		
		if (seq == null) {
			groups.push(cursor);
		} else {
			groups.push(seq, ctx);
		}
		
		table = groups.getTempResult();
	}
}
