package com.raqsoft.ide.dfx.control;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Vector;

import javax.swing.SwingUtilities;

import com.raqsoft.cellset.IRowCell;
import com.raqsoft.common.Area;
import com.raqsoft.common.CellLocation;
import com.raqsoft.ide.common.GC;
import com.raqsoft.ide.common.control.ControlUtilsBase;
import com.raqsoft.ide.dfx.GCDfx;

/**
 * �б�ͷ������
 *
 */
public class RowHeaderListener implements MouseMotionListener, MouseListener,
		KeyListener {
	/** ����༭�ؼ� */
	private DfxControl control;

	/** ѡ��ʱ����ʼ�� */
	private int startSelectedRow;

	/** �ı��и�ʱ�������ʼ��Y���� */
	private int resizeStartY;

	/** �ı��и�ʱ�ı������� */
	private int resizeStartRow;

	/** �ı��и�ǰ��ԭʼ�и� */
	private float oldCellHeight;

	/** �ı��и߹����е���ʱ�и� */
	private int tmpHeight;

	/** �Ƿ���Ա༭ */
	private boolean editable = true;
	/** ��ǰ������ */
	private transient CellLocation activeCell = null;

	/**
	 * ���������캯��
	 * 
	 * @param control
	 *            ����༭�ؼ�
	 */
	public RowHeaderListener(DfxControl control) {
		this(control, true);
	}

	/**
	 * ���������캯��
	 * 
	 * @param control
	 *            ����༭�ؼ�
	 * @param editable
	 *            �Ƿ���Ա༭
	 */
	public RowHeaderListener(DfxControl control, boolean editable) {
		this.control = control;
		this.editable = editable;
	}

	/**
	 * �������¼�
	 * 
	 * @param e
	 */
	public void mouseEntered(MouseEvent e) {
	}

	/**
	 * ��갴��ʱ�Ĵ���
	 * 
	 * @param e
	 *            ����¼�
	 */
	public void mousePressed(MouseEvent e) {
		if (!editable) {
			showPopup(e);
			return;
		}
		control.getRowHeader().getView().requestFocus();
		final int row = ControlUtils.lookupHeaderIndex(e.getY(), control.cellY,
				control.cellH);
		if (row < 0) {
			return;
		}

		boolean rowIsSelected = false;
		if (!control.m_selectedRows.isEmpty()) {
			for (int i = 0; i < control.m_selectedRows.size(); i++) {
				Integer r = (Integer) control.m_selectedRows.get(i);
				if (r.intValue() == row) {
					rowIsSelected = true;
					break;
				}
			}
		}
		if (e.getButton() == MouseEvent.BUTTON1 || !rowIsSelected) {
			if (control.status != GCDfx.STATUS_CELLRESIZE
					|| e.getButton() != MouseEvent.BUTTON1) {
				resizeStartRow = 0;
				if (!e.isControlDown()) {
					control.clearSelectedArea();
					control.m_selectedRows.clear();
				}
				control.m_selectedCols.clear();

				// ѧϰexcel��ѡ��������׸���Ϊ��ǰ��
				int firstCol = control.getContentPanel().drawStartCol;
				if (firstCol < 1 || firstCol > control.dfx.getColCount())
					firstCol = 1;
				control.setActiveCell(new CellLocation(row, firstCol), false);

				control.m_cornerSelected = false;
				if (e.isShiftDown() && this.startSelectedRow > 0) {
					control.m_selectedRows = new Vector<Integer>();
					int start = row < this.startSelectedRow ? row
							: this.startSelectedRow;
					int end = row < this.startSelectedRow ? this.startSelectedRow
							: row;
					for (int i = start; i <= end; i++) {
						control.addSelectedRow(new Integer(i));
					}
					control.addSelectedArea(new Area(start, (int) 1, end,
							(int) control.dfx.getColCount()), false);
				} else {
					this.startSelectedRow = row;
					control.addSelectedRow(new Integer(row));
					control.addSelectedArea(new Area(row, (int) 1, row,
							(int) control.dfx.getColCount()), false);
				}
				control.repaint();
				control.fireRegionSelect(true);
			} else if (e.getButton() == MouseEvent.BUTTON1) {
				control.getContentPanel().submitEditor();
				resizeStartY = e.getY();
				resizeStartRow = ControlUtils.lookupHeaderIndex(resizeStartY,
						control.cellY, control.cellH);
				oldCellHeight = control.cellH[row] / control.scale;
				tmpHeight = control.cellH[row];
			}
		}
		showPopup(e);
		activeCell = control.getActiveCell();
		control.setActiveCell(null);
	}

	/**
	 * ����ͷ�ʱ�Ĵ���
	 * 
	 * @param e
	 *            ����¼�
	 */
	public void mouseReleased(MouseEvent e) {
		if (!editable) {
			showPopup(e);
			return;
		}
		int y = e.getY();
		int row = ControlUtils.lookupHeaderIndex(y, control.cellY,
				control.cellH);
		// ��קʱ��ˢ�¿ؼ�����ϲ�����ֻҪ����ק�ˣ��ͽ�ֹ���
		if (activeCell != null && control.m_selectedRows != null
				&& control.m_selectedRows.contains(activeCell.getRow())) {
			control.setActiveCell(activeCell, false);
		}
		if (control.status == GCDfx.STATUS_CELLRESIZE) {
			if (resizeStartRow > 0) {
				if (row != resizeStartRow) {
					row = resizeStartRow;
				}
				Vector<Integer> willResizeRows = new Vector<Integer>();
				willResizeRows.add(new Integer(row));
				if (!control.m_selectedRows.isEmpty()) {
					int r1 = ((Integer) control.m_selectedRows.get(0))
							.intValue();
					int r2 = ((Integer) control.m_selectedRows
							.get(control.m_selectedRows.size() - 1)).intValue();
					int selectedStartRow = Math.min(r1, r2);
					int selectedEndRow = Math.max(r1, r2);
					if (row >= selectedStartRow && row <= selectedEndRow) {
						willResizeRows = control.m_selectedRows;
					}
				}
				IRowCell rc = control.dfx.getRowCell(row);
				if (rc != null) {
					rc.setHeight(oldCellHeight);
					control.fireRowHeaderResized(willResizeRows, tmpHeight
							/ control.scale);
				}
			}
		} else {
			if (e.getX() > RowHeaderPanel.getHeaderW(control)) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					DfxEditor editor = ControlUtils.extractDfxEditor(control);
					if (editor.expandRow(row)) {
						control.contentView.initCellLocations();
						((RowHeaderPanel) control.getRowHeaderPanel())
								.initRowLocations();
						Vector<Object> newAreas = new Vector<Object>();
						newAreas.add(new Area(row, (short) 1, row,
								(short) control.dfx.getColCount()));
						editor.setSelectedAreas(newAreas);
						editor.resetEditor();
						control.setActiveCell(new CellLocation(row, 1));
						control.getContentPanel().revalidate();
						control.getRowHeaderPanel().revalidate();
						control.getColHeaderPanel().revalidate();
						control.repaint();
						return;
					}
				}
			}

			control.fireRegionSelect(true);
			control.status = GCDfx.STATUS_NORMAL;
		}

		final MouseEvent me = e;
		Thread t = new Thread() {
			public void run() {
				showPopup(me);
			}
		};
		SwingUtilities.invokeLater(t); // �ӳٵ���������˵��ڿؼ��б���ס
	}

	/**
	 * ������¼�
	 */
	public void mouseClicked(MouseEvent e) {
		// ˫���б���ĸ��ߣ������Զ������иߵĹ���
		if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
			int y = e.getY();
			int row = ControlUtils.lookupHeaderIndex(y, control.cellY,
					control.cellH);
			if (row < 1) {
				return;
			}
			if (y >= control.cellY[row] + control.cellH[row] - 2
					&& y <= control.cellY[row] + control.cellH[row]) {
				DfxEditor editor = ControlUtils.extractDfxEditor(control);
				Vector<Integer> rows = new Vector<Integer>();
				rows.add(new Integer(row));
				editor.selectedRows = rows;
				editor.adjustRowHeight();
			}
		}
	}

	/**
	 * ����˳��¼�
	 */
	public void mouseExited(MouseEvent e) {
	}

	/**
	 * ��ס����϶����ʱ�Ĵ���
	 * 
	 * @param e
	 *            ����¼�
	 */
	public void mouseDragged(MouseEvent e) {
		if (!editable) {
			return;
		}
		int y = e.getY();
		int row = ControlUtils.lookupHeaderIndex(y, control.cellY,
				control.cellH);
		if (control.status == GCDfx.STATUS_NORMAL) {
			if (row < 0) {
				return;
			}
			int start = Math.min(startSelectedRow, row);
			int end = Math.max(startSelectedRow, row);
			if (start <= 0) {
				return;
			}
			// ��ק�����ٷ���ʱ���б���ѡ
			control.m_selectedRows.clear();
			for (int i = start; i <= end; i++) {
				control.addSelectedRow(new Integer(i));
			}
			control.addSelectedArea(
					new Area(start, 1, end, control.dfx.getColCount()), true);
			if (ControlUtils.scrollToVisible(control.getRowHeader(), control,
					row, (int) 0)) {
				Point p1 = control.getRowHeader().getViewPosition();
				Point p2 = control.getViewport().getViewPosition();
				p2.y = p1.y;
				control.getViewport().setViewPosition(p2);
			}
			control.repaint();
			control.fireRegionSelect(true);
		}
		if (control.status == GCDfx.STATUS_CELLRESIZE) {
			if (resizeStartRow > 0) {
				if (row != resizeStartRow) {
					row = resizeStartRow;
				}
				tmpHeight = tmpHeight + y - resizeStartY;
				resizeStartY = y;
				if (tmpHeight < 1) {
					tmpHeight = 1;
				}
				float newHeight = tmpHeight / control.scale;
				IRowCell rc = control.dfx.getRowCell(row);
				if (rc != null)
					rc.setHeight(newHeight);
				control.getRowHeader().getView().repaint();

				// ��ק���̲�ʵʱˢ�����ݣ��ᵼ�����ֵ�wrapBuffer��������
				control.getViewport().getView().repaint();
				ControlUtilsBase.clearWrapBuffer();
			}
		}
	}

	/**
	 * ����ƶ�ʱ�Ĵ���
	 * 
	 * @param e
	 *            ����¼�
	 */
	public void mouseMoved(MouseEvent e) {
		if (!editable) {
			return;
		}
		int y = e.getY();
		int row = ControlUtils.lookupHeaderIndex(y, control.cellY,
				control.cellH);
		if (row < 0) {
			control.status = GCDfx.STATUS_NORMAL;
			control.getRowHeader()
					.getView()
					.setCursor(
							Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			return;
		}
		if (y >= control.cellY[row] + control.cellH[row] - 2
				&& y <= control.cellY[row] + control.cellH[row]) {
			control.status = GCDfx.STATUS_CELLRESIZE;
			control.getRowHeader()
					.getView()
					.setCursor(
							Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
		} else {
			control.status = GCDfx.STATUS_NORMAL;
			control.getRowHeader()
					.getView()
					.setCursor(
							Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}

	/**
	 * ��ʾ�Ҽ������˵�
	 * 
	 * @param e
	 */
	void showPopup(MouseEvent e) {
		if (e.isPopupTrigger()) {
			control.fireRightClicked(e, GC.SELECT_STATE_ROW);
		}
	}

	/**
	 * ���̰����ͷ��¼�
	 */
	public void keyReleased(KeyEvent e) {
	}

	/**
	 * ���̱����µĴ��� �����µ���shift+���������Ӧ�ı䵱ǰѡ�е���
	 * 
	 * @param e
	 *            �����¼�
	 */
	public void keyPressed(KeyEvent e) {
		if (!editable) {
			return;
		}
		int key = e.getKeyCode();
		switch (key) {
		case KeyEvent.VK_DOWN:
			if (!e.isShiftDown()) {
				control.toDownCell();
				e.consume();
				return;
			}
			int start = ((Integer) control.m_selectedRows.get(0)).intValue();
			int end = ((Integer) control.m_selectedRows
					.get(control.m_selectedRows.size() - 1)).intValue();
			if (this.startSelectedRow == start) {
				end++;
			} else {
				start++;
			}
			if (end > control.dfx.getRowCount()) {
				end = control.dfx.getRowCount();
			}
			if (start < 1) {
				start = 1;
			}
			control.m_selectedCols.clear();
			control.m_cornerSelected = false;
			control.m_selectedRows.clear();
			for (int i = start; i <= end; i++) {
				control.addSelectedRow(new Integer(i));
			}
			control.addSelectedArea(new Area(start, (int) 1, end,
					(int) control.dfx.getColCount()), true);
			if (ControlUtils.scrollToVisible(control.getRowHeader(), control,
					end, (int) 0)) {
				Point p1 = control.getRowHeader().getViewPosition();
				Point p2 = control.getViewport().getViewPosition();
				p2.y = p1.y;
				control.getViewport().setViewPosition(p2);
			}
			control.repaint();
			control.fireRegionSelect(true);
			e.consume();
			break;
		case KeyEvent.VK_UP:
			if (!e.isShiftDown()) {
				control.toUpCell();
				e.consume();
				return;
			}
			start = ((Integer) control.m_selectedRows.get(0)).intValue();
			end = ((Integer) control.m_selectedRows.get(control.m_selectedRows
					.size() - 1)).intValue();
			if (this.startSelectedRow == end) {
				start--;
			} else {
				end--;
			}
			if (end > control.dfx.getRowCount()) {
				end = control.dfx.getRowCount();
			}
			if (start < 1) {
				start = 1;
			}
			control.m_selectedCols.clear();
			control.m_cornerSelected = false;
			control.m_selectedRows.clear();
			for (int i = start; i <= end; i++) {
				control.addSelectedRow(new Integer(i));
			}
			control.addSelectedArea(new Area(start, (int) 1, end,
					(int) control.dfx.getColCount()), true);
			if (ControlUtils.scrollToVisible(control.getRowHeader(), control,
					start, (int) 0)) {
				Point p1 = control.getRowHeader().getViewPosition();
				Point p2 = control.getViewport().getViewPosition();
				p2.y = p1.y;
				control.getViewport().setViewPosition(p2);
			}
			control.repaint();
			control.fireRegionSelect(true);
			e.consume();
			break;
		default:
			return;
		}
	}

	public void keyTyped(KeyEvent e) {
	}

}
