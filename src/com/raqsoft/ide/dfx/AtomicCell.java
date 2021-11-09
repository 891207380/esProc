package com.raqsoft.ide.dfx;

import java.util.Vector;

import com.raqsoft.cellset.datamodel.ColCell;
import com.raqsoft.cellset.datamodel.NormalCell;
import com.raqsoft.cellset.datamodel.PgmNormalCell;
import com.raqsoft.cellset.datamodel.RowCell;
import com.raqsoft.common.Area;
import com.raqsoft.common.StringUtils;
import com.raqsoft.ide.common.GC;
import com.raqsoft.ide.common.GM;
import com.raqsoft.ide.common.IAtomicCmd;
import com.raqsoft.ide.dfx.control.ControlUtils;
import com.raqsoft.ide.dfx.control.DfxControl;

/**
 * ��Ԫ���ԭ�Ӳ���
 *
 */
public class AtomicCell implements IAtomicCmd {
	/** ��Ԫ��ֵ */
	public static final byte CELL_VALUE = 0;
	/** ��Ԫ����ʽ */
	public static final byte CELL_EXP = 1;
	/** ��Ԫ����ʾ */
	public static final byte CELL_TIPS = 2;
	/** ETL �����༭���� */
	public static final byte CELL_FUNC_OBJECT = 3;

	/**
	 * �п�
	 */
	public static final byte COL_WIDTH = 100;
	/**
	 * �и�
	 */
	public static final byte ROW_HEIGHT = 101;
	/**
	 * �п���
	 */
	public static final byte COL_VISIBLE = 103;
	/**
	 * �п���
	 */
	public static final byte ROW_VISIBLE = 104;

	/**
	 * ���Ӷ���
	 */
	private Object cell;
	/**
	 * ��������
	 */
	private byte property;
	/**
	 * ֵ
	 */
	private Object value;
	/**
	 * ����ؼ�
	 */
	private DfxControl control;
	/**
	 * ѡ�е������б�
	 */
	private Vector<Object> selectedAreas;

	/**
	 * �к�
	 */
	private int row = 0;

	/**
	 * ���캯��
	 * 
	 * @param control
	 *            ����ؼ�
	 * @param cell
	 *            ��Ԫ��
	 */
	public AtomicCell(DfxControl control, Object cell) {
		this.control = control;
		this.cell = cell;
		selectedAreas = new Vector<Object>();
		selectedAreas.addAll(control.getSelectedAreas());
	}

	/**
	 * ���캯�������������ò����������ʱ
	 * 
	 * @param control
	 *            ����ؼ�
	 * @param row
	 *            �к�
	 */
	public AtomicCell(DfxControl control, int row) {
		this.control = control;
		this.row = row;
		selectedAreas = new Vector<Object>();
		selectedAreas.addAll(control.getSelectedAreas());
	}

	/**
	 * ת�ַ���
	 */
	public String toString() {
		return "cell:" + cell + "#key:" + property + "#val:" + value;
	}

	/**
	 * ������������
	 * 
	 * @param property
	 */
	public void setProperty(byte property) {
		this.property = property;
	}

	/**
	 * ����ֵ
	 * 
	 * @param value
	 */
	public void setValue(Object value) {
		if (value instanceof String) {
			if (!StringUtils.isValidString(value)) {
				this.value = null;
			}
		}
		this.value = value;
	}

	/**
	 * ��¡
	 */
	public Object clone() {
		AtomicCell an = new AtomicCell(control, cell);
		an.setProperty(property);
		an.setValue(value);
		return an;
	}

	/**
	 * ���ø�������
	 * 
	 * @param cell
	 *            ����
	 * @param property
	 *            ��������
	 * @param newVal
	 *            ֵ
	 */
	public static void setCellProperty(Object cell, byte property, Object newVal) {
		NormalCell nc = null;
		if (cell instanceof NormalCell) {
			nc = (NormalCell) cell;
			nc = (NormalCell) nc.getCellSet().getCell(nc.getRow(), nc.getCol());
		}
		switch (property) {
		case CELL_VALUE:
			if (newVal == null) { // ���������
				nc.reset();
			} else {
				nc.setValue(GM.getOptionTrimChar0Value(newVal));
			}
			break;
		case CELL_FUNC_OBJECT:
			// ((EtlNormalCell) nc).setFuncObj((ObjectElement) newVal);
			break;
		case CELL_EXP:
			nc.setExpString(newVal == null ? null : GM
					.getOptionTrimChar0String(((String) newVal)));
			break;
		case CELL_TIPS:
			nc.setTip((String) newVal);
			break;
		case COL_WIDTH:
			((ColCell) cell).setWidth(((Float) newVal).floatValue());
			break;
		case ROW_HEIGHT:
			((RowCell) cell).setHeight(((Float) newVal).floatValue());
			break;
		case COL_VISIBLE:
			((ColCell) cell)
					.setVisible(((Boolean) newVal).booleanValue() ? ColCell.VISIBLE_ALWAYS
							: ColCell.VISIBLE_ALWAYSNOT);
			break;
		case ROW_VISIBLE:
			((RowCell) cell)
					.setVisible(((Boolean) newVal).booleanValue() ? RowCell.VISIBLE_ALWAYS
							: RowCell.VISIBLE_ALWAYSNOT);
			break;
		}
	}

	/**
	 * ȡ��������
	 * 
	 * @param cell
	 *            ����
	 * @param property
	 *            ��������
	 * @return
	 */
	public static Object getCellProperty(Object cell, byte property) {
		Object oldValue = null;
		switch (property) {
		case CELL_VALUE:
			oldValue = ((NormalCell) cell).getValue();
			break;
		case CELL_FUNC_OBJECT:
			// oldValue = ((EtlNormalCell) cell).getFuncObj();
			break;
		case CELL_EXP:
			oldValue = ((NormalCell) cell).getExpString();
			break;
		case CELL_TIPS:
			oldValue = ((NormalCell) cell).getTip();
			break;
		case COL_WIDTH:
			oldValue = new Float(((ColCell) cell).getWidth());
			break;
		case ROW_HEIGHT:
			oldValue = new Float(((RowCell) cell).getHeight());
			break;
		case COL_VISIBLE:
			oldValue = new Boolean(
					((ColCell) cell).getVisible() != ColCell.VISIBLE_ALWAYSNOT);
			break;
		case ROW_VISIBLE:
			oldValue = new Boolean(
					((RowCell) cell).getVisible() != RowCell.VISIBLE_ALWAYSNOT);
			break;
		}
		return oldValue;
	}

	/**
	 * ����ֵ
	 * 
	 * @param undoAn
	 * @param newVal
	 */
	private void setValue(AtomicCell undoAn, Object newVal) {
		Object oldValue = getCellProperty(cell, property);
		if (newVal instanceof String) {
			if (!StringUtils.isValidString(newVal)
					|| newVal.equals(new String("\u007F"))) {
				newVal = null;
			}
		}

		setCellProperty(cell, property, newVal);
		undoAn.setValue(oldValue);
	}

	/**
	 * ִ��
	 */
	public IAtomicCmd execute() {
		if (cell == null && row > 0) {
			cell = control.dfx.getRowCell(row);
		}
		AtomicCell undoAn = (AtomicCell) this.clone();
		if (cell == null) {
			return undoAn;
		}
		if (value != GC.NULL) {
			setValue(undoAn, value);
		}
		if (cell instanceof PgmNormalCell && selectedAreas.isEmpty()) {
			PgmNormalCell nc = (PgmNormalCell) cell;
			Vector<Object> v = new Vector<Object>();
			v.add(new Area(nc.getRow(), nc.getCol(), nc.getRow(), nc.getCol()));
			undoAn.selectedAreas = v;
		}
		ControlUtils.extractDfxEditor(control).setSelectedAreas(selectedAreas);
		return undoAn;

	}
}
