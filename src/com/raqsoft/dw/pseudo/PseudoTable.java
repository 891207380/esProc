package com.raqsoft.dw.pseudo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.raqsoft.common.RQException;
import com.raqsoft.dm.Context;
import com.raqsoft.dm.DataStruct;
import com.raqsoft.dm.Machines;
import com.raqsoft.dm.Record;
import com.raqsoft.dm.Sequence;
import com.raqsoft.dm.cursor.ConjxCursor;
import com.raqsoft.dm.cursor.ICursor;
import com.raqsoft.dm.cursor.MergeCursor;
import com.raqsoft.dm.cursor.MultipathCursors;
import com.raqsoft.dm.op.Derive;
import com.raqsoft.dm.op.Join;
import com.raqsoft.dm.op.New;
import com.raqsoft.dm.op.Operable;
import com.raqsoft.dm.op.Operation;
import com.raqsoft.dm.op.Switch;
import com.raqsoft.dw.ITableMetaData;
import com.raqsoft.expression.Constant;
import com.raqsoft.expression.Expression;
import com.raqsoft.expression.IParam;
import com.raqsoft.expression.Node;
import com.raqsoft.expression.ParamParser;
import com.raqsoft.expression.UnknownSymbol;
import com.raqsoft.expression.mfn.sequence.Contain;
import com.raqsoft.expression.operator.And;
import com.raqsoft.expression.operator.DotOperator;
import com.raqsoft.expression.operator.Equals;
import com.raqsoft.expression.operator.NotEquals;
import com.raqsoft.expression.operator.Or;

public class PseudoTable extends Pseudo implements Operable, IPseudo {	
	//�����α���Ҫ�Ĳ���
	private String []fkNames;
	private Sequence []codes;
	private int pathCount;
	
	private ArrayList<Operation> extraOpList = new ArrayList<Operation>();//��������������ӳټ��㣨������������select��ӣ�

	private PseudoTable mcsTable;
	
	protected boolean hasPseudoColumns = false;//�Ƿ���Ҫ����α�ֶ�ת����ö�١���ֵ�����ʽ
	
	public PseudoTable() {
	}
	
	/**
	 * ����������
	 * @param rec �����¼
	 * @param hs �ֻ�����
	 * @param n ������
	 * @param ctx
	 */
	public PseudoTable(Record rec, Machines hs, int n, Context ctx) {
		pd = new PseudoDefination(rec, ctx);
		pathCount = n;
		this.ctx = ctx;
		extraNameList = new ArrayList<String>();
		init();
	}
	
//	public PseudoTable(ITableMetaData table, Context ctx) {
//		this.table = table;
//		this.ctx = ctx;
//		extraNameList = new ArrayList<String>();
//		init();
//	}
//	
//	public PseudoTable(ITableMetaData table, int n, Context ctx) {
//		this.table = table;
//		this.ctx = ctx;
//		extraNameList = new ArrayList<String>();
//		pathCount = n;
//		init();
//	}
//	
//	public PseudoTable(ITableMetaData table, PseudoTable ptable, Context ctx) {
//		this.table = table;
//		this.ctx = ctx;
//		extraNameList = new ArrayList<String>();
//		mcsTable = ptable;
//		init();
//	}
	
	private void init() {
		if (getPd() != null) {
			allNameList = new ArrayList<String>();
			String []names = getPd().getAllColNames();
			for (String name : names) {
				allNameList.add(name);
			}
			
			if (getPd().getColumns() != null) {
				List<PseudoColumn> columns = getPd().getColumns();
				for (PseudoColumn column : columns) {
					//�������ö��α�ֶκͶ�ֵα�ֶΣ�Ҫ��¼�������ڽ������Ĵ����л��õ�
					if (column.getPseudo() != null && 
							(column.getBits() != null || column.get_enum() != null)) {
						hasPseudoColumns = true;
					}
					if (column.getDim() != null) {
						addColName(column.getName());
					}
				}
			}
		}
	}

	public void addPKeyNames() {
		addColNames(getPd().getAllSortedColNames());
	}
	
	public void addColNames(String []nameArray) {
		for (String name : nameArray) {
			addColName(name);
		}
	}
	
	public void addColName(String name) {
		if (name == null) return; 
		if (allNameList.contains(name) && !extraNameList.contains(name)) {
			extraNameList.add(name);
		}
	}
	
	/**
	 * ����ȡ���ֶ�
	 * @param exps ȡ�����ʽ
	 * @param fields ȡ������
	 */
	private void setFetchInfo(Expression []exps, String []fields) {
		this.exps = null;
		this.names = null;
		
		extraOpList.clear();
		
		//set FK codes info
		if (fkNameList != null) {
			int size = fkNameList.size();
			fkNames = new String[size];
			fkNameList.toArray(fkNames);
			
			codes = new Sequence[size];
			codeList.toArray(codes);
		}
		
		if (exps == null) {
			if (fields == null) {
				return;
			} else {
				this.names = getFetchColNames(fields);//��ȡ���ֶ�
			}
		} else {
			//��ȡ�����ʽҲ��ȡ���ֶ�
			//���extraNameList���Ƿ����exps����ֶ�
			//����У���ȥ��
			ArrayList<String> tempList = new ArrayList<String>();
			for (String name : extraNameList) {
				if (!tempList.contains(name)) {
					tempList.add(name);
				}
			}
			for (Expression exp : exps) {
				String expName = exp.getIdentifierName();
				if (tempList.contains(expName)) {
					tempList.remove(expName);
				}
			}
			
			ArrayList<String> tempNameList = new ArrayList<String>();
			ArrayList<Expression> tempExpList = new ArrayList<Expression>();
			int size = exps.length;
			for (int i = 0; i < size; i++) {
				Expression exp = exps[i];
				String name = fields[i];
				Node node = exp.getHome();
				if (node instanceof UnknownSymbol) {
					tempExpList.add(exp);
					tempNameList.add(name);
				} else if (node instanceof DotOperator) {
					Node left = node.getLeft();
					if (left != null && left instanceof UnknownSymbol) {
						PseudoColumn col = getPd().findColumnByName( ((UnknownSymbol)left).getName());
						if (col != null) {
							Derive derive = new Derive(new Expression[] {exp}, new String[] {name}, null);
							extraOpList.add(derive);
						}
					}
				} else {
					
				}
			}
			
			for (String name : tempList) {
				tempExpList.add(new Expression(name));
				tempNameList.add(name);
			}
			
			size = tempExpList.size();
			this.exps = new Expression[size];
			tempExpList.toArray(this.exps);
			
			this.names = new String[size];
			tempNameList.toArray(this.names);
		}
	}
	
	private String[] getFetchColNames(String []fields) {
		//if (fields == null) return null;
		ArrayList<String> tempList = new ArrayList<String>();
		if (fields != null) {
			for (String name : fields) {
				tempList.add(name);
			}
		}
		for (String name : extraNameList) {
			if (!tempList.contains(name)) {
				tempList.add(name);
			}
		}
		
		int size = tempList.size();
		if (size == 0) {
			return null;
		}
		String []newFields = new String[size];
		tempList.toArray(newFields);
		return newFields;
	}
	
	/**
	 * �õ�����ÿ��ʵ�����α깹�ɵ�����
	 * @return
	 */
	public ICursor[] getCursors() {
		List<ITableMetaData> tables = getPd().getTables();
		int size = tables.size();
		ICursor cursors[] = new ICursor[size];
		
		for (int i = 0; i < size; i++) {
			cursors[i] = getCursor(tables.get(i), null);
		}
		return cursors;
	}
	
	/**
	 * �õ�table���α�
	 * @param table
	 * @param mcs
	 * @return
	 */
	private ICursor getCursor(ITableMetaData table, ICursor mcs) {
		ICursor cursor = null;
		if (fkNames != null) {
			if (mcs != null ) {
				if (mcs instanceof MultipathCursors) {
					cursor = table.cursor(null, this.names, filter, fkNames, codes, null, (MultipathCursors)mcs, null, ctx);
				} else {
					if (exps == null) {
						cursor = table.cursor(null, this.names, filter, fkNames, codes, null, ctx);
					} else {
						cursor = table.cursor(this.exps, this.names, filter, fkNames, codes, null, ctx);
					}
				}
			} else if (pathCount > 1) {
				if (exps == null) {
					cursor = table.cursor(null, this.names, filter, fkNames, codes, null, pathCount, ctx);
				} else {
					cursor = table.cursor(this.exps, this.names, filter, fkNames, codes, null, pathCount, ctx);
				}
			} else {
				if (exps == null) {
					cursor = table.cursor(null, this.names, filter, fkNames, codes, null, ctx);
				} else {
					cursor = table.cursor(this.exps, this.names, filter, fkNames, codes, null, ctx);
				}
			}
		} else {
			if (mcs != null ) {
				if (mcs instanceof MultipathCursors) {
					cursor = table.cursor(null, this.names, filter, null, null, null, (MultipathCursors)mcs, null, ctx);
				} else {
					if (exps == null) {
						cursor = table.cursor(this.names, filter, ctx);
					} else {
						cursor = table.cursor(this.exps, this.names, filter, null, null, null, ctx);
					}
				}
			} else if (pathCount > 1) {
				if (exps == null) {
					cursor = table.cursor(null, this.names, filter, null, null, null, pathCount, ctx);
				} else {
					cursor = table.cursor(this.exps, this.names, filter, null, null, null, pathCount, ctx);
				}
			} else {
				if (exps == null) {
					cursor = table.cursor(this.names, filter, ctx);
				} else {
					cursor = table.cursor(this.exps, this.names, filter, null, null, null, ctx);
				}
			}
		}
		
		if (getPd() != null && getPd().getColumns() != null) {
			for (PseudoColumn column : getPd().getColumns()) {
				if (column.getDim() != null) {//�����������������һ��switch���ӳټ���
					Sequence dim;
					if (column.getDim() instanceof Sequence) {
						dim = (Sequence) column.getDim();
					} else {
						dim = ((IPseudo) column.getDim()).cursor(null, null).fetch();
					}
					
					String fkey[] = column.getFkey();
					if (fkey == null) {
						String[] fkNames = new String[] {column.getName()};//��ʱname��������ֶ�
						Sequence[] codes = new Sequence[] {dim};
						Switch s = new Switch(fkNames, codes, null, null);
						cursor.addOperation(s, ctx);
//					} else if (fkey.length == 1) {
//						Sequence[] codes = new Sequence[] {dim};
//						Switch s = new Switch(fkey, codes, null, null);
//						cursor.addOperation(s, ctx);
					} else {
						int size = fkey.length;
						Expression[][] exps = new Expression[1][];
						exps[0] = new Expression[size];
						for (int i = 0; i < size; i++) {
							exps[0][i] = new Expression(fkey[i]);
						}
						Expression[][] newExps = new Expression[1][];
						newExps[0] = new Expression[] {new Expression("~")};
						String[][] newNames = new String[1][];
						newNames[0] = new String[] {column.getName()};
						Join join = new Join(null, null, exps, codes, null, newExps, newNames, null);
						cursor.addOperation(join, ctx);
					}
				}
			}
		}
	
		if (extraOpList != null) {
			for (Operation op : extraOpList) {
				cursor.addOperation(op, ctx);
			}
		}
		if (opList != null) {
			for (Operation op : opList) {
				cursor.addOperation(op, ctx);
			}
		}
		
		return cursor;
	
	}
	
	/**
	 * �鲢���������α�
	 * @param cursors
	 * @return
	 */
	static ICursor mergeCursor(ICursor cursors[], Context ctx) {
		int[] sortFields = cursors[0].getDataStruct().getPKIndex();
		if (sortFields != null) {
			return new MergeCursor(cursors, sortFields, null, ctx);//������鲢
		} else {
			return new ConjxCursor(cursors);//����������
		}
	}
	
	//���������α�
	public ICursor cursor(Expression []exps, String []names) {
		setFetchInfo(exps, names);//��ȡ���ֶ���ӽ�ȥ��������ܻ��extraOpList��ֵ
		
		//ÿ��ʵ���ļ�����һ���α�
		List<ITableMetaData> tables = getPd().getTables();
		int size = tables.size();
		ICursor cursors[] = new ICursor[size];
		
		/**
		 * �Եõ��α���й鲢����Ϊ���
		 * 1 ֻ��һ���α��򷵻أ�
		 * 2 �ж���α��Ҳ�����ʱ�����й鲢
		 * 3 �ж���α��Ҳ���ʱ���ȶԵ�һ���α�ֶΣ�Ȼ�������α갴��һ��ͬ���ֶΣ�����ÿ���α��ÿ���ν��й鲢
		 */
		if (size == 1) {//ֻ��һ���α�ֱ�ӷ���
			return getCursor(tables.get(0), null);
		} else {
			if (pathCount > 1) {//ָ���˲���������ʱ����mcsTable
				cursors[0] = getCursor(tables.get(0), null);
				for (int i = 1; i < size; i++) {
					cursors[i] = getCursor(tables.get(i), cursors[0]);
				}
			} else {//û��ָ��������
				if (mcsTable == null) {//û��ָ���ֶβο����mcsTable
					for (int i = 0; i < size; i++) {
						cursors[i] = getCursor(tables.get(i), null);
					}
					return mergeCursor(cursors, ctx);
				} else {//ָ���˷ֶβο����mcsTable
					ICursor mcs = null;
					if (mcsTable != null) {
						mcs = mcsTable.cursor();
					}
					for (int i = 0; i < size; i++) {
						cursors[i] = getCursor(tables.get(i), mcs);
					}
					mcs.close();
				}
			}
			
			//��cursors���ι鲢������:�������α�ĵ�N·�鲢,�õ�N���α�,�ٰ���N���α����ɶ�·�α귵��
			int mcount = ((MultipathCursors)cursors[0]).getPathCount();//�ֶ���
			ICursor mcursors[] = new ICursor[mcount];//����α�
			for (int m = 0; m < mcount; m++) {
				ICursor cursorArray[] = new ICursor[size];
				for (int i = 0; i < size; i++) {
					cursorArray[i] = ((MultipathCursors)cursors[i]).getCursors()[m];
				}
				mcursors[m] = mergeCursor(cursorArray, ctx);
			}
			return new MultipathCursors(mcursors, ctx);
		}
	}
	
	//���ڻ�ȡ��·�α�
	private ICursor cursor() {
		List<ITableMetaData> tables = getPd().getTables();
		return tables.get(0).cursor(null, null, null, null, null, null, pathCount, ctx);
	}

	public Object clone(Context ctx) throws CloneNotSupportedException {
		PseudoTable obj = new PseudoTable();
		obj.hasPseudoColumns = hasPseudoColumns;
		obj.pathCount = pathCount;
		obj.mcsTable = mcsTable;
		obj.fkNames = fkNames == null ? null : fkNames.clone();
		obj.codes = codes == null ? null : codes.clone();
		cloneField(obj);
		obj.ctx = ctx;
		return obj;
	}

	public void setPathCount(int pathCount) {
		this.pathCount = pathCount;
	}

	public void setMcsTable(PseudoTable mcsTable) {
		this.mcsTable = mcsTable;
	}
	
	/**
	 * �ѱ��ʽ���漰α�ֶε�ö�١���ֵ�������ת��
	 * @param node
	 */
	private void parseFilter(Node node) {
		if (node instanceof And || node instanceof Or) {
			parseFilter(node.getLeft());
			parseFilter(node.getRight());
		} else if (node instanceof Equals || node instanceof NotEquals) {
			//��α�ֶε�==��!=���д���
			if (node.getLeft() instanceof UnknownSymbol) {
				//�ж��Ƿ���α�ֶ�
				String pname = ((UnknownSymbol) node.getLeft()).getName();
				PseudoColumn col = getPd().findColumnByPseudoName(pname);
				if (col != null) {
					Sequence seq;
					//�ж��Ƿ��Ƕ�ö��α�ֶν�������
					seq = col.get_enum();
					if (seq != null) {
						node.setLeft(new UnknownSymbol(col.getName()));//��Ϊ���ֶ�
						Integer obj = seq.firstIndexOf(node.getRight().calculate(ctx));
						node.setRight(new Constant(obj));//��ö��ֵ��Ϊ��Ӧ�����ֵ
					}
					
					//�ж��Ƿ��ǶԶ�ֵα�ֶν�������
					seq = col.getBits();
					if (seq != null) {
						int idx = seq.firstIndexOf(pname) - 1;
						int bit = 1 << idx;
						String str = "and(" + col.getName() + "," + bit + ")";
						node.setLeft(new Expression(str).getHome());//��Ϊ���ֶε�λ����
						if ((Boolean) node.getRight().calculate(ctx)) {
							node.setRight(new Constant(bit));
						} else {
							node.setRight(new Constant(0));
						}
					}
				}
			} else if (node.getRight() instanceof UnknownSymbol) {
				//�����ֶ������ұߵ���������ҽ���һ���ٴ����߼�������һ��
				Node right = node.getRight();
				node.setRight(node.getLeft());
				node.setLeft(right);
				parseFilter(node);
			}
		} else if (node instanceof DotOperator) {
			//����ö���б��α�ֶε�contain���д���
			if (node.getRight() instanceof Contain) {
				Contain contain = (Contain)node.getRight();
				IParam param = contain.getParam();
				if (param == null || !param.isLeaf()) {
					return;
				}
				
				//�ж��Ƿ��Ƕ�α�ֶν���contain����
				UnknownSymbol un = (UnknownSymbol) param.getLeafExpression().getHome();
				PseudoColumn col = getPd().findColumnByPseudoName(un.getName());
				if (col != null && col.get_enum() != null) {
					Object val = node.getLeft().calculate(ctx);
					if (val instanceof Sequence) {
						//��contain�ұߵ��ֶ�����Ϊ���ֶ�
						IParam newParam = ParamParser.parse(col.getName(), null, ctx);
						contain.setParam(newParam);
						
						//��contain��ߵ�ö��ֵ���и�Ϊ��Ӧ�����ֵ������
						Sequence value = (Sequence) val;
						Sequence newValue = new Sequence();
						int size = value.length();
						for (int i = 1; i <= size; i++) {
							Integer obj = col.get_enum().firstIndexOf(value.get(i));
							newValue.add(obj);
						}
						node.setLeft(new Constant(newValue));
					}
				}
			}
		}
	}
	
	public Operable addOperation(Operation op, Context ctx) {
		if (hasPseudoColumns) {
			Expression exp = op.getFunction().getParam().getLeafExpression();
			Node node = exp.getHome();
			parseFilter(node);
		}
		return super.addOperation(op, ctx);
	}
	
	public void append(ICursor cursor, String option) {
		//������׷�ӵ�file������ж��file��ȡ���һ��
		List<ITableMetaData> tables = getPd().getTables();
		int size = tables.size();
		if (size == 0) {
			return;
		}
		ITableMetaData table = tables.get(size - 1);

		List<PseudoColumn> columns = pd.getColumns();
		if (columns != null) {
			//�ȰѲ���α�ֶεĸ�ֵ����
			String fields[] = table.getAllColNames();
			DataStruct ds = new DataStruct(fields);
			size = ds.getFieldCount();
			Expression []exps = new Expression[size];
			String []names = new String[size];
			for (int c = 0; c < size; c++) {
				exps[c] = new Expression(fields[c]);
				names[c] = fields[c];
			}
			
			//ת���α����α�ֶ�
			size = columns.size();
			for (int c = 0; c < size; c++) {
				PseudoColumn column = columns.get(c);
				String pseudoName = column.getPseudo();
				Sequence bitNames = column.getBits();
				int idx = ds.getFieldIndex(column.getName());
				
				if (column.getExp() != null) {
					//�б��ʽ��α��
					exps[idx] = new Expression(column.getExp());
					names[idx] = column.getName();
				} else if (pseudoName != null && column.get_enum() != null) {
					//ö��α��
					String var = "pseudo_enum_value_" + c;
					Context context = cursor.getContext();
					if (context == null) {
						context = new Context();
						cursor.setContext(context);
						context.setParamValue(var, column.get_enum());
					} else {
						context.setParamValue(var, column.get_enum());
					}
					exps[idx] = new Expression(var + ".pos(" + pseudoName + ")");
					names[idx] = column.getName();
				} else if (bitNames != null) {
					//�����ֵα�ֶ�(���α�ֶΰ�λת��Ϊһ�����ֶ�)
					String exp = "0";
					int len = bitNames.length();
					for (int i = 1; i <= len; i++) {
						String field = (String) bitNames.get(i);
						//ת��Ϊbitֵ,���ۼ�
						exp += "+ if(" + field + ",0,shift(1,-" + (i - 1) + "))";
					}
					exps[idx] = new Expression(exp);
					names[idx] = column.getName();
				}
			}
			
			New _new = new New(exps, names, null);
			cursor.addOperation(_new, null);
		}
		
		try {
			table.append(cursor, option);
		} catch (IOException e) {
			throw new RQException(e.getMessage(), e);
		}
	}
	
	// �ж��Ƿ�������ָ�����Ƶ����
	public boolean hasForeignKey(String fkName) {
		PseudoColumn column = pd.findColumnByName(fkName);
		if (column.getDim() != null)
			return true;
		else
			return false;
	}
	
	/**
	 * ������
	 * @param fkName	�����
	 * @param fieldNames ����ֶ�
	 * @param code	���
	 * @return
	 */
	public PseudoTable addForeignKeys(String fkName, String []fieldNames, PseudoTable code) {
		PseudoTable table = null;
		try {
			table = (PseudoTable) clone(ctx);
			table.getPd().addPseudoColumn(new PseudoColumn(fkName, fieldNames, code));
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return table;
	}
	
	// ���ӱ�������������������
	public static ICursor join(PseudoTable masterTable, PseudoTable subTable) {
		return null;
	}
	
	// ȡ�������
	public String[] getPrimaryKey() {
		return getPd().getAllSortedColNames();
	}
	
	// ȡ�ֶ���switchָ���������û���򷵻ؿ�
	PseudoTable getFieldSwitchTable(String fieldName) {
		List<PseudoColumn> columns = pd.getColumns();
		for (PseudoColumn column : columns) {
			if (column.getDim() != null) {
				if (column.getFkey() == null && column.getName().equals(fieldName)) {
					return (PseudoTable) column.getDim();
				} else if (column.getFkey() != null 
						&& column.getFkey().length == 1
						&& column.getFkey()[0].equals(fieldName)) {
					return (PseudoTable) column.getDim();
				}
			}
		}
		return null;
	}
}

