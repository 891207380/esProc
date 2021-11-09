package com.raqsoft.expression.fn;

import com.raqsoft.cellset.ICellSet;
import com.raqsoft.cellset.INormalCell;
import com.raqsoft.cellset.datamodel.PgmCellSet;
import com.raqsoft.common.MessageManager;
import com.raqsoft.common.RQException;
import com.raqsoft.dm.Context;
import com.raqsoft.expression.Function;
import com.raqsoft.expression.IParam;
import com.raqsoft.expression.Node;
import com.raqsoft.resources.EngineMessage;

/**
 * �����ӳ���
 * func(a,arg)�ڲ�ѯ�ж�������ӳ���󣬾Ϳ��������ⵥԪ���н����ӳ���ĵ��á�  
 * @author runqian
 *
 */
public class Func extends Function {
	public class CallInfo {
		private INormalCell cell;
		private Object []args;
		
		public CallInfo(INormalCell cell, Object []args) {
			this.cell = cell;
			this.args = args;
		}
		
		public PgmCellSet getPgmCellSet() {
			return (PgmCellSet)cs;
		}
		
		public int getRow(){
			return cell.getRow();
		}
		
		public int getCol() {
			return cell.getCol();
		}
		
		public Object[] getArgs() {
			return args;
		}
	}

	public Node optimize(Context ctx) {
		if (param != null) param.optimize(ctx);
		return this;
	}
	
	public Object calculate(Context ctx) {
		IParam param = this.param;
		if (param == null) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("func" + mm.getMessage("function.missingParam"));
		}

		INormalCell cell;
		Object []args = null;
		if (param.isLeaf()) {
			cell = param.getLeafExpression().calculateCell(ctx);
			if (cell == null) {
				MessageManager mm = EngineMessage.get();
				throw new RQException("func" + mm.getMessage("function.invalidParam"));
			}
		} else {
			IParam sub0 = param.getSub(0);
			if (sub0 == null) {
				MessageManager mm = EngineMessage.get();
				throw new RQException("func" + mm.getMessage("function.invalidParam"));
			}

			cell = sub0.getLeafExpression().calculateCell(ctx);
			if (cell == null) {
				MessageManager mm = EngineMessage.get();
				throw new RQException("func" + mm.getMessage("function.invalidParam"));
			}
			
			int size = param.getSubSize();
			args = new Object[size - 1];
			for (int i = 1; i < size; ++i) {
				IParam sub = param.getSub(i);
				if (sub != null) {
					args[i - 1] = sub.getLeafExpression().calculate(ctx);
				}
			}
		}

		ICellSet cs = cell.getCellSet();
		if (!(cs instanceof PgmCellSet)) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("func" + mm.getMessage("function.invalidParam"));
		}

		PgmCellSet pcs = (PgmCellSet)cs;
		return pcs.executeFunc(cell.getRow(), cell.getCol(), args);
	}
	
	// ide����ȡ������Ϣ���е�������
	public CallInfo getCallInfo(Context ctx) {
		if (param == null) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("func" + mm.getMessage("function.missingParam"));
		}

		INormalCell cell;
		Object []args = null;
		if (param.isLeaf()) {
			cell = param.getLeafExpression().calculateCell(ctx);
			if (cell == null) {
				MessageManager mm = EngineMessage.get();
				throw new RQException("func" + mm.getMessage("function.invalidParam"));
			}
		} else {
			IParam sub0 = param.getSub(0);
			if (sub0 == null) {
				MessageManager mm = EngineMessage.get();
				throw new RQException("func" + mm.getMessage("function.invalidParam"));
			}

			cell = sub0.getLeafExpression().calculateCell(ctx);
			if (cell == null) {
				MessageManager mm = EngineMessage.get();
				throw new RQException("func" + mm.getMessage("function.invalidParam"));
			}
			
			int size = param.getSubSize();
			args = new Object[size - 1];
			for (int i = 1; i < size; ++i) {
				IParam sub = param.getSub(i);
				if (sub != null) {
					args[i - 1] = sub.getLeafExpression().calculate(ctx);
				}
			}
		}

		ICellSet cs = cell.getCellSet();
		if (!(cs instanceof PgmCellSet)) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("func" + mm.getMessage("function.invalidParam"));
		}
		
		return new CallInfo(cell, args);
	}
}
