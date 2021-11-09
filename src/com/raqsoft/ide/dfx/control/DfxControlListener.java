package com.raqsoft.ide.dfx.control;

import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.text.JTextComponent;

import com.raqsoft.cellset.datamodel.CellSet;
import com.raqsoft.cellset.datamodel.NormalCell;
import com.raqsoft.common.Area;
import com.raqsoft.common.CellLocation;
import com.raqsoft.common.StringUtils;
import com.raqsoft.ide.common.ConfigOptions;
import com.raqsoft.ide.common.GC;
import com.raqsoft.ide.common.IAtomicCmd;
import com.raqsoft.ide.common.control.CellRect;
import com.raqsoft.ide.dfx.AtomicCell;
import com.raqsoft.ide.dfx.GCDfx;

/**
 * ����ؼ��ļ�����
 *
 */
public class DfxControlListener implements EditorListener {
	/**
	 * ����ؼ��༭��
	 */
	DfxEditor editor;

	/**
	 * ���캯��
	 * 
	 * @param editor
	 *            ����ؼ��༭��
	 */
	public DfxControlListener(DfxEditor editor) {
		this.editor = editor;
	}

	/**
	 * ȡ����ؼ��༭��
	 * 
	 * @return
	 */
	public DfxEditor getEditor() {
		return editor;
	}

	/**
	 * �Ҽ�����¼�
	 */
	public void rightClicked(MouseEvent e, int clickPlace) {
		editor.getDFXListener().rightClicked(e.getComponent(), e.getX(),
				e.getY());
	}

	/**
	 * ѡ������
	 */
	public void regionsSelect(Vector<Object> vectRegion,
			Vector<Integer> selectedRows, Vector<Integer> selectedColumns,
			boolean selectedAll, boolean keyEvent) {

		editor.selectedRects.clear();
		for (int i = 0; i < vectRegion.size(); i++) {
			Area a = (Area) vectRegion.get(i);
			if (a == null) {
				continue;
			}
			editor.selectedRects.add(new CellRect(a));
		}
		editor.selectedCols = selectedColumns;
		editor.selectedRows = selectedRows;
		if (vectRegion.isEmpty()) {
			editor.selectState = GCDfx.SELECT_STATE_NONE;
		} else if (selectedAll) {
			editor.selectState = GCDfx.SELECT_STATE_DM;
		} else if (selectedColumns.size() > 0) {
			editor.selectState = GCDfx.SELECT_STATE_COL;
		} else if (selectedRows.size() > 0) {
			editor.selectState = GCDfx.SELECT_STATE_ROW;
		} else {
			editor.selectState = GCDfx.SELECT_STATE_CELL;
		}
		editor.getDFXListener()
				.selectStateChanged(editor.selectState, keyEvent);
	}

	/**
	 * �п�仯��
	 */
	public boolean columnWidthChange(Vector<Integer> vectColumn, float nWidth) {
		// �и��п�ı�ʱ��������������wrapString Key�����һ��
		ControlUtils.clearWrapBuffer();
		editor.selectedCols = vectColumn;
		editor.setColumnWidth(nWidth);
		return true;
	}

	/**
	 * �и߱仯��
	 */
	public boolean rowHeightChange(Vector<Integer> vectRow, float nHeight) {
		// �и��п�ı�ʱ��������������wrapString Key�����һ��
		ControlUtils.clearWrapBuffer();
		editor.selectedRows = vectRow;
		editor.setRowHeight(nHeight);
		return true;
	}

	/**
	 * �����ƶ��¼�
	 * 
	 * @param area
	 *            ����
	 * @param nRowPos
	 *            �к�
	 * @param nColumnPos
	 *            �к�
	 * @return
	 */
	public boolean cellRegionMove(Area area, int nRowPos, int nColumnPos) {
		return true;
	}

	/**
	 * ����ճ����Ϣ
	 * 
	 * @param area
	 *            ճ���ı������
	 * @param nRowPos
	 *            ճ������λ��
	 * @param nColumnPos
	 *            ճ������λ��
	 * @return ture ��Ϣ�ѱ�����false ��Ϣδ������
	 */
	public boolean cellRegionPaste(Area area, int nRowPos, int nColumnPos) {
		return true;
	}

	/**
	 * ������չ��Ϣ����ûʵ�֣�
	 * 
	 * @param area
	 *            ��չ�ı������
	 * @param nColumnExpand
	 *            ����չ��(����������չ������������չ��0������չ)
	 * @param nRowExpand
	 *            ����չ��(����������չ������������չ��0������չ)
	 * @return ture ��Ϣ�ѱ�����false ��Ϣδ������
	 */
	public boolean cellRegionExpand(Area area, int nColumnExpand, int nRowExpand) {
		return true;
	}

	/**
	 * ����������Ϣ����ûʵ�֣�
	 * 
	 * @param area
	 *            �����ı������
	 * @param nRowShrink
	 *            ����������������ȥ��������0����������
	 * @param nColumnShrink
	 *            ����������������ȥ��������0����������
	 * @return ture ��Ϣ�ѱ�����false ��Ϣδ������
	 */
	public boolean cellRegionShrink(Area area, int nColumnShrink, int nRowShrink) {
		return true;
	}

	/**
	 * ȡ�и߱仯��ԭ������
	 * 
	 * @param control
	 *            ����ؼ�
	 * @param row
	 *            �к�
	 * @param col
	 *            �к�
	 * @param newText
	 *            �ı�
	 * @return
	 */
	public static AtomicCell getCellHeightCmd(DfxControl control, int row,
			int col, String newText) {
		// �����Ԫ����ʾʱ���Զ������иߣ�ϣ�������ľ���һ�д���
		if (!ConfigOptions.bAutoSizeRowHeight.booleanValue()) {
			return null;
		}
		CellSet ics = control.dfx;
		float w = control.getContentPanel().getEditableWidth(newText, row, col);
		float h = ics.getRowCell(row).getHeight();
		float textH = ControlUtils.getStringHeight(newText, w, GC.font) + 10;
		if (h < textH) {
			AtomicCell rac = new AtomicCell(control, ics.getRowCell(row));
			rac.setProperty(AtomicCell.ROW_HEIGHT);
			rac.setValue(new Float(textH));
			return rac;
		}
		return null;
	}

	/**
	 * ����ı��༭��Ϣ
	 * 
	 * @param row
	 *            �༭���к�
	 * @param col
	 *            �༭���к�
	 * @param strText
	 *            �༭����ı�
	 * @return ture ��Ϣ�ѱ�����false ��Ϣδ������
	 */
	public boolean cellTextInput(int row, int col, String strText) {
		CellSet ics = editor.getComponent().dfx;
		strText = strText != null ? strText : "";
		Vector<IAtomicCmd> cmds = new Vector<IAtomicCmd>();
		NormalCell nc = (NormalCell) ics.getCell(row, (int) col);
		AtomicCell ac = new AtomicCell(editor.getComponent(), nc);
		ac.setProperty(AtomicCell.CELL_EXP);
		if (StringUtils.isValidString(strText)) {
			ac.setValue(strText);
		} else {
			ac.setValue(null);
		}
		cmds.add(ac);

		ac = getCellHeightCmd(editor.getComponent(), row, (int) col, strText);
		if (ac != null) {
			cmds.add(ac);
		}
		editor.executeCmd(cmds);
		return true;
	}

	public void editorInputing(String text) {
	}

	/**
	 * ���˫���¼�
	 * 
	 * @param e
	 *            MouseEvent
	 */
	public void doubleClicked(MouseEvent e) {
		if (editor.getComponent() == null)
			return;
		CellLocation pos = editor.getComponent().getActiveCell();
		if (pos != null) {
			JTextComponent textEditor = editor.getComponent().getEditor();
			if (textEditor == null || !textEditor.isEditable())
				return;
			editor.textEditor();
		}
	}

	/**
	 * ����ƶ�
	 * 
	 * @param row
	 *            ��
	 * @param col
	 *            ��
	 */
	public void mouseMove(int row, int col) {
	}

	/**
	 * �������ƶ��¼�
	 */
	public void scrollBarMoved() {

	}
}
