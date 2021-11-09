package com.raqsoft.cellset.datamodel;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import com.raqsoft.cellset.ICellSet;
import com.raqsoft.cellset.INormalCell;
import com.raqsoft.common.ByteArrayInputRecord;
import com.raqsoft.common.ByteArrayOutputRecord;
import com.raqsoft.common.ByteMap;
import com.raqsoft.common.CellLocation;
import com.raqsoft.common.MessageManager;
import com.raqsoft.common.RQException;
import com.raqsoft.resources.EngineMessage;

abstract public class NormalCell implements INormalCell {
	private static final long serialVersionUID = 0x02010014;

	// ��Ԫ������
	public final static int TYPE_CALCULABLE_CELL  = 0x00000001; // ����� =
	public final static int TYPE_CALCULABLE_BLOCK = 0x00000002; // ����� ==
	public final static int TYPE_EXECUTABLE_CELL  = 0x00000004; // ִ�и� >
	public final static int TYPE_EXECUTABLE_BLOCK = 0x00000008; // ִ�п� >>
	public final static int TYPE_COMMAND_CELL     = 0x00000010; // ����
	public final static int TYPE_CONST_CELL       = 0x00000020; // ������
	public final static int TYPE_NOTE_CELL        = 0x00000040; // ע�͸� /
	public final static int TYPE_NOTE_BLOCK       = 0x00000080; // ע�Ϳ� //
	public final static int TYPE_BLANK_CELL       = 0x00000100; // �հ׸�

	protected CellSet cs;
	protected int row;
	protected int col;
	protected String expStr; // ���ʽ�ַ���
	protected String tip;

	protected Object value;

	/**
	 * ���л�ʱʹ��
	 */
	public NormalCell() {
	}

	/**
	 * ������Ԫ��
	 * @param cs CellSet ��Ԫ������������
	 * @param r int �к�
	 * @param c int �к�
	 */
	public NormalCell(CellSet cs, int r, int c) {
		row = r;
		col = c;
		this.cs = cs;
	}

	/**
	 * ȡ�õ�ǰ��Ԫ����к�
	 * @return int
	 */
	public int getRow() {
		return row;
	}

	/**
	 * ȡ�õ�ǰ��Ԫ����к�
	 * @return int
	 */
	public int getCol() {
		return col;
	}

	/**
	 * ���õ�ǰ���к�
	 * @param r int
	 */
	public void setRow(int r) {
		row = r;
	}

	/**
	 * ���õ�ǰ���к�
	 * @param c int
	 */
	public void setCol(int c) {
		col = c;
	}

	/**
	 * ���õ�Ԫ������������
	 * @param cs ICellSet
	 */
	public void setCellSet(ICellSet cs) {
		if (this.cs != null && this.cs.isExecuteOnly()) {
			MessageManager mm = EngineMessage.get();
			throw new RQException(mm.getMessage("cellset.executeOnly"));
		}

		this.cs = (CellSet)cs;
	}

	/**
	 * ���ص�Ԫ������������
	 * @return ICellSet
	 */
	public ICellSet getCellSet() {
		return cs;
	}

	/**
	 * ���ص�Ԫ���ʶ
	 * @return String
	 */
	public String getCellId() {
		return CellLocation.getCellId(row, col);
	}

	/**
	 * ���õ�Ԫ����ʽ
	 * @param exp String
	 */
	public void setExpString(String exp) {
		if (cs != null && cs.isExecuteOnly()) {
			return;
		}

		this.expStr = exp;
	}

	/**
	 * @return String ���ص�Ԫ����ʽ
	 */
	public String getExpString() {
		if (cs != null && cs.isExecuteOnly()) {
			return null;
		}

		return expStr;
	}

	/**
	 * ���õ�Ԫ��ֵ
	 * @param value Object
	 */
	public void setValue(Object value) {
		this.value = value;
	}

	/**
	 * ���ص�Ԫ��ֵ��û�м����򷵻ؿ�
	 * @return Object
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * ���ص�Ԫ��ֵ��û�м����������
	 * @param doCalc boolean
	 * @return Object
	 */
	abstract public Object getValue(boolean doCalc);

	/**
	 * д���ݵ���
	 * @param out ObjectOutput �����
	 * @throws IOException
	 */
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeByte(2);

		out.writeObject(cs);
		out.writeInt(row);
		out.writeInt(col);
		out.writeObject(expStr);
		out.writeObject(tip);
		out.writeObject(value);
	}

	/**
	 * �����ж�����
	 * @param in ObjectInput ������
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		in.readByte(); // �汾

		cs = (CellSet) in.readObject();
		row = in.readInt();
		col = in.readInt();
		setExpString((String)in.readObject());
		tip = (String)in.readObject();
		value = in.readObject();
	}

	/**
	 * д���ݵ���
	 * @throws IOException
	 * @return �����
	 */
	public byte[] serialize() throws IOException{
		ByteArrayOutputRecord out = new ByteArrayOutputRecord();

		// cellset
		out.writeInt(row);
		out.writeInt(col);
		out.writeString(expStr);
		out.writeString(tip);
		return out.toByteArray();
	}

	/**
	 * �����ж�����
	 * @param buf byte[]
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void fillRecord(byte[] buf) throws IOException, ClassNotFoundException {
		ByteArrayInputRecord in = new ByteArrayInputRecord(buf);

		// cellset��CellSet�����
		row = in.readInt();
		col = in.readInt();
		setExpString(in.readString());
		tip = in.readString();
	}

	/**
	 * ����˵�Ԫ��
	 */
	abstract public void calculate();

	/**
	 * ���赥Ԫ��״̬Ϊ��ʼ״̬
	 */
	abstract public void reset();

	/**
	 * ���ص�Ԫ������
	 * @return int
	 */
	abstract public int getType();

	/**
	 * �����Ƿ��Ǽ����
	 * @return boolean
	 */
	abstract public boolean isCalculableBlock();

	/**
	 * �����Ƿ��Ǽ����
	 * @return boolean
	 */
	abstract public boolean isCalculableCell();

	/**
	 * ���ص�Ԫ���Ƿ���Ҫ�����ʽ��Ǩ
	 * @return boolean
	 */
	abstract protected boolean needRegulateString();

	/**
	 * undoʱ�ָ������Ĵ�������
	 */
	public void undoErrorRef() {
		cs.setCell(row, col, this);
	}

	protected ByteMap getExpMap(boolean isClone) {
		return null;
	}

	protected void setExpMap(ByteMap map) {
	}

	/**
	 * ���õ�Ԫ����ʾ
	 * @param tip String
	 */
	public void setTip(String tip) {
		if (cs != null && cs.isExecuteOnly()) {
			return;
		}

		this.tip = tip;
	}

	/**
	 * ���ص�Ԫ����ʾ
	 * @return String
	 */
	public String getTip() {
		return tip;
	}
}
