package com.raqsoft.dw;

import com.raqsoft.common.MessageManager;
import com.raqsoft.common.RQException;
import com.raqsoft.dm.Context;
import com.raqsoft.dm.DataStruct;
import com.raqsoft.dm.Sequence;
import com.raqsoft.dm.Table;
import com.raqsoft.dm.cursor.ICursor;
import com.raqsoft.expression.Expression;
import com.raqsoft.expression.FieldRef;
import com.raqsoft.expression.Function;
import com.raqsoft.expression.IParam;
import com.raqsoft.expression.Moves;
import com.raqsoft.expression.Node;
import com.raqsoft.expression.ParamInfo2;
import com.raqsoft.expression.UnknownSymbol;
import com.raqsoft.expression.operator.DotOperator;
import com.raqsoft.resources.EngineMessage;

/**
 * ����ʵ���ֶα��ʽ��f()
 * @author runqian
 *
 */
class TableGather {
	private ICursor cs;//Դ�α�
	private int calcType;//��������
	private Sequence data;
	private TableMetaData table;
	private GroupTableRecord curRecord;//��ǰ��¼
	private int cur;//���
	private int len;//��ǰ����
	private long recSeq;//��ǰα��
	private Sequence temp;
	private static String []funName = {"field","sum","count","max","min","avg","top","iterate"};
	private String []subNames;//���ֶ���
	
	private boolean isRow;
	
	public TableGather(TableMetaData baseTable,Expression exp, Context ctx) {
		Node home = exp.getHome();
		if (!(home instanceof DotOperator) && !(home instanceof Moves)) {
			return;//Ŀǰֻ����T.C / T.f(C) / T{}
		}
		
		Object obj = home.getLeft();
		String tableName = ((UnknownSymbol)obj).getName();
		table = baseTable.getAnnexTable(tableName);
		if (table == null) {
			MessageManager mm = EngineMessage.get();
			throw new RQException(tableName + mm.getMessage("dw.tableNotExist"));
		}
		isRow = table instanceof RowTableMetaData;

		obj = exp.getHome().getRight();
		String field = null;
		IFilter[] filters = null;
		if (home instanceof Moves) {
			IParam fieldParam = ((Moves) home).getParam();
			ParamInfo2 pi = ParamInfo2.parse(fieldParam, "cursor", false, false);
			String []subFields = pi.getExpressionStrs1();
			String []subNames = pi.getExpressionStrs2();
			if (subFields == null) {
				subFields = table.getColNames();
				subNames = subFields;
			} else {
				int colCount = subNames.length;
				for (int i = 0; i < colCount; ++i) {
					if (subNames[i] == null || subNames[i].length() == 0) {
						if (subFields[i] == null) {
							MessageManager mm = EngineMessage.get();
							throw new RQException("cursor" + mm.getMessage("function.invalidParam"));
						}

						subNames[i] = subFields[i];
					}
				}
			}
			this.subNames = subNames;
			if (isRow) {
				cs = table.cursor(subFields);
				((RowCursor)cs).setFetchByBlock(true);
			} else {
				cs = new TableCursor((ColumnTableMetaData) table, subFields, filters, ctx);
			}
			temp = new Sequence(10);
			calcType = 9;
			return;
		}
		if (obj instanceof FieldRef) {
			field = ((FieldRef)obj).getName();
			calcType = 0;
		} else if (obj instanceof Function) {
			field = ((Function)obj).getParamString();
			String fname = ((Function)obj).getFunctionName();
			for (int i = 0, len = funName.length; i < len; i++) {
				if (funName[i].equals(fname)) {
					calcType = i;
					break;
				}
			}
		}
		if (isRow) {
			cs = table.cursor(new String[]{field});
		} else {
			cs = new TableCursor((ColumnTableMetaData) table, new String[]{field}, filters, ctx);
		}
		temp = new Sequence(10);
	}
	
	void setSegment(int startBlock, int endBlock) {
		((IDWCursor) cs).setSegment(startBlock, endBlock);
	}
	
	void loadData() {
		if (cs instanceof TableCursor) {
			data = ((TableCursor)cs).get(Integer.MAX_VALUE - 1);
		} else {
			data = cs.fetch();
		}
		if (data == null)
			return;
		if (data.hasRecord()) {
			curRecord = (GroupTableRecord) data.getMem(1);
			len = data.length();
			cur = 1;
			recSeq = curRecord.getRecordSeq();
		}
	}
	
	void skip() {
		cs.skip();
	}
	
	Object getNextBySeq(long seq) {
		long recSeq = this.recSeq;
		int cur = this.cur;
		int len = this.len;
		Sequence data = this.data;
		if (data == null) {
			if (calcType == 2) return 0;
			return null;
		}
		GroupTableRecord r = (GroupTableRecord) data.getMem(cur);
		
		//�ҵ���һ����ͬ��
		while (seq != recSeq) {
			cur++;
			if (cur > len) {
				if (calcType == 2) return 0;
				return null;
			}
			r = (GroupTableRecord) data.getMem(cur);
			recSeq = r.getRecordSeq();
		}
		
		//ȡ��������ͬ��
		Sequence temp = this.temp;
		temp.clear();
		while (seq == recSeq) {
			if (calcType == 9) {
				temp.add(r);
			} else {
				temp.add(r.getFieldValue(0));
			}
			cur++;
			if (cur > len) {
				break;
			}
			r = (GroupTableRecord) data.getMem(cur);
			recSeq = r.getRecordSeq();
		}
		
		//ָ��
		this.cur = cur;
		this.recSeq = recSeq;
		
		//����
		Object result = null;
		switch (calcType) {
		case 0 : 
			result = temp.getMem(1);
			break;
		case 1 : 
			//sum
			result = temp.sum();
			break;
		case 2 : 
			//count
			result = temp.length();
			break;
		case 3 : 
			//max
			result = temp.max();
			break;
		case 4 : 
			//min
			result = temp.min();
			break;
		case 5 : 
			//avg
			result = temp.average();
			break;
		case 9 : 
			//{}
			DataStruct ds = new DataStruct(subNames);
			Table t = new Table(ds);
			t.addAll(temp);
			result = t;
			break;
		default:
			break;
		}
		return result;
		
	}
}