package com.raqsoft.ide.dfx;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Image;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import com.raqsoft.cellset.ICellSet;
import com.raqsoft.cellset.INormalCell;
import com.raqsoft.cellset.datamodel.CellSet;
import com.raqsoft.cellset.datamodel.Command;
import com.raqsoft.cellset.datamodel.NormalCell;
import com.raqsoft.cellset.datamodel.PgmCellSet;
import com.raqsoft.cellset.datamodel.PgmNormalCell;
import com.raqsoft.common.ByteMap;
import com.raqsoft.common.CellLocation;
import com.raqsoft.common.IByteMap;
import com.raqsoft.common.MessageManager;
import com.raqsoft.common.RQException;
import com.raqsoft.common.StringUtils;
import com.raqsoft.common.UUID;
import com.raqsoft.dm.Context;
import com.raqsoft.dm.DfxManager;
import com.raqsoft.dm.Env;
import com.raqsoft.dm.FileObject;
import com.raqsoft.dm.JobSpace;
import com.raqsoft.dm.JobSpaceManager;
import com.raqsoft.dm.Param;
import com.raqsoft.dm.ParamList;
import com.raqsoft.dm.Sequence;
import com.raqsoft.expression.Expression;
import com.raqsoft.expression.Node;
import com.raqsoft.expression.fn.Call;
import com.raqsoft.expression.fn.Func;
import com.raqsoft.expression.fn.Func.CallInfo;
import com.raqsoft.ide.common.CellSetTxtUtil;
import com.raqsoft.ide.common.ConfigFile;
import com.raqsoft.ide.common.ConfigOptions;
import com.raqsoft.ide.common.DataSource;
import com.raqsoft.ide.common.GC;
import com.raqsoft.ide.common.GM;
import com.raqsoft.ide.common.GV;
import com.raqsoft.ide.common.IAtomicCmd;
import com.raqsoft.ide.common.IPrjxSheet;
import com.raqsoft.ide.common.PrjxAppMenu;
import com.raqsoft.ide.common.control.CellRect;
import com.raqsoft.ide.common.control.IEditorListener;
import com.raqsoft.ide.common.control.PanelConsole;
import com.raqsoft.ide.common.dialog.DialogArgument;
import com.raqsoft.ide.common.dialog.DialogCellSetProperties;
import com.raqsoft.ide.common.dialog.DialogEditConst;
import com.raqsoft.ide.common.dialog.DialogInputArgument;
import com.raqsoft.ide.common.dialog.DialogInputPassword;
import com.raqsoft.ide.common.dialog.DialogRowHeight;
import com.raqsoft.ide.common.dialog.DialogSQLEditor;
import com.raqsoft.ide.common.dialog.DialogSelectDataSource;
import com.raqsoft.ide.common.resources.IdeCommonMessage;
import com.raqsoft.ide.custom.Server;
import com.raqsoft.ide.dfx.control.ContentPanel;
import com.raqsoft.ide.dfx.control.ControlUtils;
import com.raqsoft.ide.dfx.control.DfxControl;
import com.raqsoft.ide.dfx.control.DfxEditor;
import com.raqsoft.ide.dfx.control.EditControl;
import com.raqsoft.ide.dfx.dialog.DialogExecCmd;
import com.raqsoft.ide.dfx.dialog.DialogFTP;
import com.raqsoft.ide.dfx.dialog.DialogOptionPaste;
import com.raqsoft.ide.dfx.dialog.DialogOptions;
import com.raqsoft.ide.dfx.dialog.DialogPassword;
import com.raqsoft.ide.dfx.dialog.DialogSearch;
//import com.raqsoft.ide.dfx.etl.cellset.EtlCellSet;
import com.raqsoft.ide.dfx.resources.IdeDfxMessage;
import com.raqsoft.resources.EngineMessage;
import com.raqsoft.util.CellSetUtil;

/**
 * ������dfx�ļ��༭����
 *
 */
public class SheetDfx extends IPrjxSheet implements IEditorListener {
	private static final long serialVersionUID = 1L;
	/**
	 * ����ؼ�
	 */
	public DfxControl dfxControl = null;
	/**
	 * ����༭��
	 */
	public DfxEditor dfxEditor = null;

	/**
	 * �Ҽ������˵�
	 */
	private PopupDfx popupDFX = null;

	/**
	 * �ļ�·��
	 */
	private String filePath = null;

	/**
	 * ������
	 */
	private Context dfxCtx = new Context();

	/**
	 * ����ִ��ʱ���ӳ������ǵ�Ԫ������ֵ��ִ��ʱ�䣨���룩
	 */
	private Map<String, Long> debugTimeMap = new HashMap<String, Long>();

	/**
	 * ����ִ�еĵ�Ԫ������
	 */
	private transient CellLocation exeLocation = null;

	/**
	 * ����ѡ���״̬
	 */
	public byte selectState = GCDfx.SELECT_STATE_NONE;

	/**
	 * �Ƿ���Զ�̷������ϵ��ļ�
	 */
	public boolean isServerFile = false;

	/**
	 * �������Ե���Ϣ
	 */
	public StepInfo stepInfo = null;

	/**
	 * ���������Ƿ��ж���
	 */
	public boolean isStepStop = false;
	/**
	 * �Ƿ��ж���������
	 */
	public boolean stepStopOther = false;

	/**
	 * ���캯��
	 * 
	 * @param filePath
	 *            �ļ�·��
	 * @param cs
	 *            �������
	 * @throws Exception
	 */
	public SheetDfx(String filePath, PgmCellSet cs) throws Exception {
		this(filePath, cs, null);
	}

	/**
	 * ���캯��
	 * 
	 * @param filePath
	 *            �ļ�·��
	 * @param cs
	 *            �������
	 * @param stepInfo
	 *            �������Ե���Ϣ
	 * @throws Exception
	 */
	public SheetDfx(String filePath, PgmCellSet cs, StepInfo stepInfo)
			throws Exception {
		super(filePath);
		this.stepInfo = stepInfo;
		if (stepInfo != null) {
			this.sheets = stepInfo.sheets;
			this.sheets.add(this);
		}
		if (stepInfo != null && cs != null) {
			dfxCtx = cs.getContext();
		}
		try {
			ImageIcon image = GM.getLogoImage(true);
			final int size = 20;
			image.setImage(image.getImage().getScaledInstance(size, size,
					Image.SCALE_DEFAULT));
			setFrameIcon(image);
		} catch (Throwable t) {
		}
		this.filePath = filePath;
		dfxEditor = new DfxEditor(dfxCtx) {
			public PgmCellSet generateCellSet(int rows, int cols) {
				return new PgmCellSet(rows, cols);
			}

		};
		this.dfxControl = dfxEditor.getComponent();
		dfxControl.setDFXScrollBarListener();
		dfxEditor.addDFXListener(this);
		if (stepInfo != null) {
			INormalCell currentCell = cs.getCurrent();
			if (currentCell == null) {
				setExeLocation(stepInfo.startLocation);
			} else {
				setExeLocation(new CellLocation(currentCell.getRow(),
						currentCell.getCol()));
			}
			dfxControl.contentView.setEditable(false);
		}
		loadBreakPoints();
		if (cs != null) {
			dfxEditor.setCellSet(cs);
		}

		setTitle(this.filePath);
		popupDFX = new PopupDfx();

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(dfxEditor.getComponent(), BorderLayout.CENTER);
		addInternalFrameListener(new Listener(this));
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		if (!isSubSheet()) {
			dfxCtx = new Context();
			Context pCtx = GMDfx.prepareParentContext();
			dfxCtx.setParent(pCtx);
			dfxControl.dfx.setContext(dfxCtx);
			dfxControl.dfx.reset();
		}

	}

	/**
	 * �Ƿ�ETL�༭
	 * 
	 * @return
	 */
	public boolean isETL() {
		return false;
	}

	/**
	 * ȡ�����������
	 * 
	 * @return
	 */
	public Context getDfxContext() {
		return dfxCtx;
	}

	/**
	 * ���δ�ʱѡ���һ����Ԫ��
	 */
	private boolean isInitSelect = true;

	/**
	 * ѡ���һ����Ԫ��
	 */
	public void selectFirstCell() {
		if (stepInfo != null)
			return;
		if (isInitSelect) {
			isInitSelect = false;
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					dfxEditor.selectFirstCell();
					selectState = GC.SELECT_STATE_CELL;
					refresh();
				}
			});
		}
	}

	/**
	 * ���������ı��ļ�
	 * 
	 * @return
	 */
	public boolean exportTxt() {
		File oldFile = new File(filePath);
		String oldFileName = oldFile.getName();
		int index = oldFileName.lastIndexOf(".");
		if (index > 0) {
			oldFileName = oldFileName.substring(0, index + 1);
			oldFileName += GC.FILE_SPL;
		}
		File f = GM.dialogSelectFile(GC.FILE_SPL, GV.lastDirectory,
				IdeDfxMessage.get().getMessage("public.export"), oldFileName,
				GV.appFrame);
		if (f == null)
			return false;
		if (f.exists() && !f.canWrite()) {
			JOptionPane.showMessageDialog(GV.appFrame, IdeCommonMessage.get()
					.getMessage("public.readonly", filePath));
			return false;
		}
		String filePath = f.getAbsolutePath();
		try {
			String cellSetStr = CellSetUtil.toString(dfxControl.dfx);
			GMDfx.writeSPLFile(filePath, cellSetStr);
		} catch (Throwable e) {
			GM.showException(e);
			return false;
		}
		JOptionPane.showMessageDialog(GV.appFrame, IdeDfxMessage.get()
				.getMessage("public.exportsucc", filePath));
		return true;
	}

	/**
	 * ����
	 */
	public boolean save() {
		if (isServerFile) { // Զ���ļ��ı���
			String serverName = filePath.substring(0, filePath.indexOf(':'));
			if (StringUtils.isValidString(serverName)) {
				Server server = GV.getServer(serverName);
				if (server != null) {
					try {
						ByteArrayOutputStream out = new ByteArrayOutputStream();
						CellSetUtil.writePgmCellSet(out,
								(PgmCellSet) getCellSet());
						String fileName = filePath.substring(
								filePath.indexOf(':') + 1).replaceAll("\\\\",
								"/");
						if (fileName.startsWith("/")) {
							fileName = fileName.substring(1);
						}
						server.save(fileName, out.toByteArray());
					} catch (Exception e) {
						GM.showException(e);
						return false;
					}
				}
			}
		} else if (GMDfx.isNewGrid(filePath, GCDfx.PRE_NEWPGM)
				|| !filePath.toLowerCase().endsWith(GC.FILE_DFX)) { // �½�
			boolean hasSaveAs = saveAs();
			if (hasSaveAs) {
				storeBreakPoints();
				if (stepInfo != null && isStepStop) { // ����֮������������
					stepInfo = null;
					isStepStop = false;
					stepStopOther = false;
					if (sheets != null)
						sheets.remove(this);
					sheets = null; // ��ǰ������֮ǰ�ĵ�������û�й�����
					resetRunState();
				}
			}
			return hasSaveAs;
		} else {
			File f = new File(filePath);
			if (f.exists() && !f.canWrite()) {
				JOptionPane.showMessageDialog(GV.appFrame, IdeCommonMessage
						.get().getMessage("public.readonly", filePath));
				return false;
			}

			try {
				if (ConfigOptions.bAutoBackup.booleanValue()) {
					String saveFile = filePath + ".bak";
					File fb = new File(saveFile);
					fb.delete();
					f.renameTo(fb);
				}
				GVDfx.panelValue.setCellSet((PgmCellSet) dfxControl.dfx);
				CellSetUtil.writePgmCellSet(filePath,
						(PgmCellSet) dfxControl.dfx);
				DfxManager.getInstance().clear();
				((PrjxAppMenu) GV.appMenu).refreshRecentFile(filePath);
			} catch (Throwable e) {
				GM.showException(e);
				return false;
			}
		}

		GM.setCurrentPath(filePath);
		dfxEditor.setDataChanged(false);
		dfxEditor.getDFXListener().commandExcuted();
		return true;
	}

	/**
	 * ���Ϊ
	 */
	public boolean saveAs() {
		boolean isNewFile;
		String fileExt;
		// if (dfxEditor.isEtlCellSet()) {
		// isNewFile = GMDfx.isNewGrid(filePath, GCDfx.PRE_NEWETL)
		// || !filePath.toLowerCase().endsWith(GC.FILE_SPL);
		// fileExt = GC.FILE_SPL;
		// } else {
		isNewFile = GMDfx.isNewGrid(filePath, GCDfx.PRE_NEWPGM)
				|| !filePath.toLowerCase().endsWith(GC.FILE_DFX);
		fileExt = GC.FILE_DFX;
		// }
		String path = filePath;
		if (stepInfo != null && isStepStop) {
			if (StringUtils.isValidString(stepInfo.filePath))
				path = stepInfo.filePath;
		}
		File saveFile = GM.dialogSelectFile(fileExt, GV.lastDirectory,
				IdeCommonMessage.get().getMessage("public.saveas"), path,
				GV.appFrame); // Save //edit by ryz 2017.08.02 ���Ӳ���owner
		// as
		if (saveFile == null) {
			return false;
		}

		String sfile = saveFile.getAbsolutePath();
		GV.lastDirectory = saveFile.getParent();

		if (!sfile.toLowerCase().endsWith(fileExt)) {
			saveFile = new File(saveFile.getParent(), saveFile.getName() + "."
					+ fileExt);
			sfile = saveFile.getAbsolutePath();
		}

		if (!GM.canSaveAsFile(sfile)) {
			return false;
		}
		if (!isNewFile) {
			storeBreakPoints(filePath, sfile);
		}
		changeFileName(sfile);
		return save();
	}

	/**
	 * ���浽FTP
	 */
	private void saveFTP() {
		if (!save())
			return;
		DialogFTP df = new DialogFTP();
		df.setFilePath(this.filePath);
		df.setVisible(true);
	}

	/**
	 * �޸��ļ���
	 */
	public void changeFileName(String newName) {
		GV.appMenu.removeLiveMenu(filePath);
		GV.appMenu.addLiveMenu(newName);
		this.filePath = newName;
		this.setTitle(newName);
		GV.toolWin.changeFileName(this, newName);
		((DFX) GV.appFrame).resetTitle();
	}

	/**
	 * ˢ��
	 */
	public void refresh() {
		refresh(false);
	}

	/**
	 * ˢ��
	 * 
	 * @param keyEvent
	 *            �Ƿ񰴼��¼�
	 */
	private void refresh(boolean keyEvent) {
		refresh(keyEvent, true);
	}

	/**
	 * ˢ��
	 * 
	 * @param keyEvent
	 *            �Ƿ񰴼��¼�
	 * @param isRefreshState
	 *            �Ƿ�ˢ��״̬
	 */
	private void refresh(boolean keyEvent, boolean isRefreshState) {
		if (dfxEditor == null) {
			return;
		}
		if (isClosed()) {
			return;
		}
		if (!(GV.appMenu instanceof MenuDfx)) {
			return;
		}
		// Menu
		MenuDfx md = (MenuDfx) GV.appMenu;
		md.setEnable(md.getMenuItems(), true);

		boolean isDataChanged = dfxEditor.isDataChanged();
		md.setMenuEnabled(GCDfx.iSAVE, isDataChanged);
		md.setMenuEnabled(GCDfx.iSAVEAS, !isServerFile);
		md.setMenuEnabled(GCDfx.iSAVEALL, true);
		md.setMenuEnabled(GCDfx.iSAVE_FTP, !isServerFile);

		md.setMenuEnabled(GCDfx.iREDO, dfxEditor.canRedo());
		md.setMenuEnabled(GCDfx.iUNDO, dfxEditor.canUndo());

		boolean canCopy = selectState != GCDfx.SELECT_STATE_NONE && true;
		md.setMenuEnabled(GCDfx.iCOPY, canCopy);
		md.setMenuEnabled(GCDfx.iCOPYVALUE, canCopy);
		md.setMenuEnabled(GCDfx.iCODE_COPY, canCopy);
		md.setMenuEnabled(GCDfx.iCOPY_HTML, canCopy);
		md.setMenuEnabled(GCDfx.iCUT, canCopy);

		md.setMenuEnabled(GCDfx.iMOVE_COPY_UP,
				selectState != GCDfx.SELECT_STATE_NONE);
		md.setMenuEnabled(GCDfx.iMOVE_COPY_DOWN,
				selectState != GCDfx.SELECT_STATE_NONE);
		md.setMenuEnabled(GCDfx.iMOVE_COPY_LEFT,
				selectState != GCDfx.SELECT_STATE_NONE);
		md.setMenuEnabled(GCDfx.iMOVE_COPY_RIGHT,
				selectState != GCDfx.SELECT_STATE_NONE);

		boolean canPaste = GMDfx.canPaste()
				&& selectState != GCDfx.SELECT_STATE_NONE;
		md.setMenuEnabled(GCDfx.iPASTE, canPaste);
		md.setMenuEnabled(GCDfx.iPASTE_ADJUST, canPaste);
		md.setMenuEnabled(GCDfx.iPASTE_SPECIAL, canPaste);

		md.setMenuEnabled(GCDfx.iCTRL_ENTER,
				selectState != GCDfx.SELECT_STATE_NONE);
		md.setMenuEnabled(GCDfx.iDUP_ROW,
				selectState != GCDfx.SELECT_STATE_NONE);
		md.setMenuEnabled(GCDfx.iDUP_ROW_ADJUST,
				selectState != GCDfx.SELECT_STATE_NONE);
		md.setMenuEnabled(GCDfx.iCTRL_INSERT,
				selectState != GCDfx.SELECT_STATE_NONE);
		md.setMenuEnabled(GCDfx.iALT_INSERT,
				selectState != GCDfx.SELECT_STATE_NONE);

		md.setMenuEnabled(GCDfx.iCLEAR, selectState != GCDfx.SELECT_STATE_NONE);
		md.setMenuEnabled(GCDfx.iFULL_CLEAR,
				selectState != GCDfx.SELECT_STATE_NONE);
		md.setMenuEnabled(GCDfx.iBREAKPOINTS,
				selectState != GCDfx.SELECT_STATE_NONE);
		md.setMenuEnabled(GCDfx.iDELETE_ROW,
				selectState != GCDfx.SELECT_STATE_NONE);
		md.setMenuEnabled(GCDfx.iDELETE_COL,
				selectState != GCDfx.SELECT_STATE_NONE);
		md.setMenuEnabled(GCDfx.iCTRL_BACK,
				selectState != GCDfx.SELECT_STATE_NONE);
		md.setMenuEnabled(GCDfx.iCTRL_DELETE,
				selectState != GCDfx.SELECT_STATE_NONE);

		md.setMenuRowColEnabled(selectState == GCDfx.SELECT_STATE_ROW
				|| selectState == GCDfx.SELECT_STATE_COL);
		md.setMenuVisible(GCDfx.iROW_HEIGHT,
				selectState == GCDfx.SELECT_STATE_ROW);
		md.setMenuVisible(GCDfx.iROW_ADJUST,
				selectState == GCDfx.SELECT_STATE_ROW);
		md.setMenuVisible(GCDfx.iROW_HIDE,
				selectState == GCDfx.SELECT_STATE_ROW);
		md.setMenuVisible(GCDfx.iROW_VISIBLE,
				selectState == GCDfx.SELECT_STATE_ROW);

		md.setMenuVisible(GCDfx.iCOL_WIDTH,
				selectState == GCDfx.SELECT_STATE_COL);
		md.setMenuVisible(GCDfx.iCOL_ADJUST,
				selectState == GCDfx.SELECT_STATE_COL);
		md.setMenuVisible(GCDfx.iCOL_HIDE,
				selectState == GCDfx.SELECT_STATE_COL);
		md.setMenuVisible(GCDfx.iCOL_VISIBLE,
				selectState == GCDfx.SELECT_STATE_COL);

		md.setMenuEnabled(GCDfx.iTEXT_EDITOR,
				selectState != GCDfx.SELECT_STATE_NONE);
		md.setMenuEnabled(GCDfx.iNOTE, selectState != GCDfx.SELECT_STATE_NONE);
		md.setMenuEnabled(GCDfx.iTIPS, selectState != GCDfx.SELECT_STATE_NONE);
		md.setMenuEnabled(GCDfx.iSEARCH, selectState != GCDfx.SELECT_STATE_NONE);
		md.setMenuEnabled(GCDfx.iREPLACE,
				selectState != GCDfx.SELECT_STATE_NONE);

		md.setMenuEnabled(GCDfx.iEDIT_CHART,
				selectState != GCDfx.SELECT_STATE_NONE);
		md.setMenuEnabled(GCDfx.iFUNC_ASSIST,
				selectState != GCDfx.SELECT_STATE_NONE);
		md.setMenuEnabled(GCDfx.iSHOW_VALUE,
				selectState != GCDfx.SELECT_STATE_NONE);
		md.setMenuEnabled(GCDfx.iCLEAR_VALUE,
				selectState != GCDfx.SELECT_STATE_NONE);

		md.setMenuEnabled(GCDfx.iDRAW_CHART,
				GVDfx.panelValue.tableValue.canDrawChart());
		md.setMenuVisible(GCDfx.iEDIT_CHART, true);
		md.setMenuVisible(GCDfx.iDRAW_CHART, true);
		// Toolbar
		GVDfx.appTool.setBarEnabled(true);
		GVDfx.appTool.setButtonEnabled(GCDfx.iSAVE, isDataChanged);
		GVDfx.appTool.setButtonEnabled(GCDfx.iCLEAR,
				selectState != GCDfx.SELECT_STATE_NONE);
		GVDfx.appTool.setButtonEnabled(GCDfx.iBREAKPOINTS,
				selectState != GCDfx.SELECT_STATE_NONE && !isStepStop);
		GVDfx.appTool.setButtonEnabled(GCDfx.iUNDO, dfxEditor.canUndo());
		GVDfx.appTool.setButtonEnabled(GCDfx.iREDO, dfxEditor.canRedo());

		if (dfxEditor != null && selectState != GCDfx.SELECT_STATE_NONE) {
			NormalCell nc = dfxEditor.getDisplayCell();
			boolean lockOtherCell = false;
			if (nc != null) {
				IByteMap values = dfxEditor.getProperty();
				GV.toolBarProperty.refresh(selectState, values);
				Object value = nc.getValue();
				GVDfx.panelValue.tableValue.setCellId(nc.getCellId());
				String oldId = GVDfx.panelValue.tableValue.getCellId();
				if (nc.getCellId().equals(oldId)) { // refresh
					GVDfx.panelValue.tableValue
							.setValue1(value, nc.getCellId());
				} else {
					lockOtherCell = true;
					GVDfx.panelValue.tableValue.setValue(value);
				}
				GVDfx.panelValue.setDebugTime(debugTimeMap.get(nc.getCellId()));
			}

			if (lockOtherCell && GVDfx.panelValue.tableValue.isLocked()) {
				String cellId = GVDfx.panelValue.tableValue.getCellId();
				if (StringUtils.isValidString(cellId)) {
					try {
						INormalCell lockCell = dfxControl.dfx.getCell(cellId);
						Object oldVal = GVDfx.panelValue.tableValue
								.getOriginalValue();
						Object newVal = lockCell.getValue();
						boolean isValChanged = false;
						if (oldVal == null) {
							isValChanged = newVal != null;
						} else {
							isValChanged = !oldVal.equals(newVal);
						}
						if (isValChanged)
							GVDfx.panelValue.tableValue.setValue1(newVal,
									cellId);
					} catch (Exception e) {
					}
				}
			}
		}

		GV.toolBarProperty.setEnabled(selectState != GCDfx.SELECT_STATE_NONE);

		GVDfx.tabParam.resetParamList(dfxCtx.getParamList());

		if (GVDfx.panelValue.tableValue.isLocked1()) {
			GVDfx.panelValue.tableValue.setLocked1(false);
		}

		if (dfxControl.dfx.getCurrentPrivilege() != PgmCellSet.PRIVILEGE_FULL) {
			md.setEnable(md.getMenuItems(), false);
			md.setMenuEnabled(GCDfx.iSAVE, isDataChanged);

			md.setMenuEnabled(GCDfx.iPROPERTY, true);
			md.setMenuEnabled(GCDfx.iCONST, false);
			md.setMenuEnabled(GCDfx.iPASSWORD, true);

			GVDfx.appTool.setBarEnabled(false);
			GVDfx.appTool.setButtonEnabled(GCDfx.iSAVE, isDataChanged);

			GV.toolBarProperty.setEnabled(false);
		}

		boolean canShow = false;
		if (GV.useRemoteServer && GV.fileTree != null
				&& GV.fileTree.getServerList() != null
				&& GV.fileTree.getServerList().size() > 0) {
			canShow = true;
		}
		md.setMenuEnabled(GCDfx.iREMOTE_SERVER_LOGOUT, canShow);
		md.setMenuEnabled(GCDfx.iREMOTE_SERVER_DATASOURCE, canShow);
		md.setMenuEnabled(GCDfx.iREMOTE_SERVER_UPLOAD_FILE, canShow);

		md.setMenuEnabled(GCDfx.iVIEW_CONSOLE,
				ConfigOptions.bIdeConsole.booleanValue());
		if (stepInfo != null) {
			// �жϵ��������Ժ�,��ǰ������call(dfx)ʱ�˵�����
			if (!isStepStopCall()) {
				md.setMenuEnabled(md.getAllMenuItems(), false);
				GVDfx.appTool.setButtonEnabled(GCDfx.iCLEAR, false);
				GVDfx.appTool.setButtonEnabled(GCDfx.iBREAKPOINTS, false);
				GV.toolBarProperty.setEnabled(false);
			}
		}
		resetRunState(isRefreshState, false);
		md.resetPasswordMenu(dfxControl.dfx.getCurrentPrivilege() == PgmCellSet.PRIVILEGE_FULL);
	}

	/**
	 * �Ƿ񵥲�����ֹͣ
	 * 
	 * @return
	 */
	private boolean isStepStopCall() {
		if (stepInfo == null)
			return false;
		return isStepStop && stepInfo.parentCall != null;
	}

	/**
	 * ȡ����
	 */
	public String getSheetTitle() {
		return getFileName();
	}

	/**
	 * ���ñ���
	 */
	public void setSheetTitle(String filePath) {
		this.filePath = filePath;
		setTitle(filePath);
		this.repaint();
	}

	/**
	 * ȡ�ļ�·��
	 */
	public String getFileName() {
		return filePath;
	}

	/**
	 * �Զ������߳�
	 */
	private CalcCellThread calcCellThread = null;

	/**
	 * ���㵱ǰ��
	 */
	public void calcActiveCell() {
		calcActiveCell(true);
	}

	/**
	 * ���㵱ǰ��
	 * 
	 * @param lock
	 *            �Ƿ����
	 */
	public void calcActiveCell(boolean lock) {
		dfxControl.getContentPanel().submitEditor();
		dfxControl.getContentPanel().requestFocus();
		CellLocation cl = dfxControl.getActiveCell();
		if (cl == null)
			return;
		if (GVDfx.appFrame instanceof DFX) {
			PanelConsole pc = ((DFX) GVDfx.appFrame).getPanelConsole();
			if (pc != null)
				pc.autoClean();
		}
		calcCellThread = new CalcCellThread(cl);
		calcCellThread.start();
		if (lock)
			GVDfx.panelValue.tableValue.setLocked(true);
	}

	/**
	 * ��Ԫ�������߳�
	 *
	 */
	class CalcCellThread extends Thread {
		/**
		 * ��Ԫ������
		 */
		private CellLocation cl;

		/**
		 * ���캯��
		 * 
		 * @param cl
		 *            ��Ԫ������
		 */
		public CalcCellThread(CellLocation cl) {
			this.cl = cl;
		}

		/**
		 * ִ�м���
		 */
		public void run() {
			try {
				int row = cl.getRow();
				int col = cl.getCol();
				dfxControl.setCalcPosition(new CellLocation(row, col));
				long t1 = System.currentTimeMillis();
				dfxControl.dfx.runCell(row, col);
				long t2 = System.currentTimeMillis();
				String cellId = CellLocation.getCellId(row, col);
				debugTimeMap.put(cellId, t2 - t1);
				NormalCell nc = (NormalCell) dfxControl.dfx.getCell(row, col);
				if (nc != null) {
					Object value = nc.getValue();
					GVDfx.panelValue.tableValue
							.setValue1(value, nc.getCellId());
				}
			} catch (Exception x) {
				String msg = x.getMessage();
				if (!StringUtils.isValidString(msg)) {
					StringBuffer sb = new StringBuffer();
					Throwable t = x.getCause();
					if (t != null) {
						sb.append(t.getMessage());
						sb.append("\r\n");
					}
					StackTraceElement[] ste = x.getStackTrace();
					for (int i = 0; i < ste.length; i++) {
						sb.append(ste[i]);
						sb.append("\r\n");
					}
					msg = sb.toString();
					showException(msg);
				} else {
					showException(x);
				}
			} finally {
				dfxControl.contentView.repaint();
				SwingUtilities.invokeLater(new Thread() {
					public void run() {
						refresh();
					}
				});
			}
		}
	}

	/**
	 * ���������Ի���
	 * 
	 * @param replace
	 *            boolean �Ƿ����滻�Ի���
	 */
	public void dialogSearch(boolean replace) {
		if (GVDfx.searchDialog != null) {
			GVDfx.searchDialog.setVisible(false);
		}
		GVDfx.searchDialog = new DialogSearch();
		GVDfx.searchDialog.setControl(dfxEditor, replace);
		GVDfx.searchDialog.setVisible(true);
	}

	/**
	 * ��ΪXML��Node�������������ֻ���������ſ�ͷ
	 * 
	 * @param name
	 *            �ڵ���
	 * @return
	 */
	private String getBreakPointNodeName(String nodeName) {
		if (nodeName == null)
			return "";
		nodeName = nodeName.replaceAll("[^0-9a-zA-Z-._]", "_");
		return "_" + nodeName;
	}

	/**
	 * ���ضϵ�
	 */
	private void loadBreakPoints() {
		ConfigFile cf = null;
		try {
			cf = ConfigFile.getConfigFile();
			String oldNode = cf.getConfigNode();
			cf.setConfigNode(ConfigFile.NODE_BREAKPOINTS);
			String breaks = cf.getAttrValue(getBreakPointNodeName(filePath));
			if (StringUtils.isValidString(breaks)) {
				StringTokenizer token = new StringTokenizer(breaks, ";");
				ArrayList<CellLocation> breakPoints = new ArrayList<CellLocation>();
				while (token.hasMoreElements()) {
					String cellName = token.nextToken();
					CellLocation cp = new CellLocation(cellName);
					breakPoints.add(cp);
				}
				dfxEditor.getComponent().setBreakPoints(breakPoints);
				cf.setConfigNode(oldNode);
			}
		} catch (Throwable ex) {
		}
	}

	/**
	 * ��ֹ����ϵ�
	 */
	private boolean preventStoreBreak = false;

	/**
	 * ����ϵ�
	 */
	private void storeBreakPoints() {
		storeBreakPoints(null, filePath);
	}

	/**
	 * ����ϵ�
	 * 
	 * @param oldName
	 *            �ɽڵ���
	 * @param filePath
	 *            ��·��
	 */
	private void storeBreakPoints(String oldName, String filePath) {
		if (preventStoreBreak) {
			return;
		}
		// δ����״̬������,*���Ų��ܵ� xml ��Key
		if (filePath.endsWith("*")) {
			return;
		}

		if (GMDfx.isNewGrid(filePath, GCDfx.PRE_NEWPGM)
				|| !filePath.toLowerCase().endsWith(GC.FILE_DFX)) {
			return;
		}

		ConfigFile cf = null;
		String oldNode = null;
		try {
			cf = ConfigFile.getConfigFile();
			oldNode = cf.getConfigNode();
			cf.setConfigNode(ConfigFile.NODE_BREAKPOINTS);
			ArrayList<CellLocation> breaks = dfxEditor.getComponent()
					.getBreakPoints();
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < breaks.size(); i++) {
				CellLocation cp = breaks.get(i);
				if (i > 0) {
					sb.append(";");
				}
				sb.append(cp.toString());
			}
			if (oldName != null)
				cf.setAttrValue(getBreakPointNodeName(oldName), "");
			cf.setAttrValue(getBreakPointNodeName(filePath), sb.toString());
			cf.save();
		} catch (Throwable ex) {
		} finally {
			cf.setConfigNode(oldNode);
		}
	}

	/**
	 * ���û���
	 */
	public void reset() {
		if (runThread != null) {
			terminate();
		}
		closeRunThread();
		debugTimeMap.clear();
		if (!isSubSheet()) {
			setExeLocation(null);
			dfxCtx = new Context();
			Context pCtx = GMDfx.prepareParentContext();
			dfxCtx.setParent(pCtx);
			dfxControl.dfx.setContext(dfxCtx);
			dfxControl.dfx.reset();
			closeSpace();
		}
		GVDfx.tabParam.resetParamList(null);
		GVDfx.panelDfxWatch.watch(null);
	}

	/**
	 * ������߳���
	 */
	private ThreadGroup tg = null;
	/**
	 * �߳�����
	 */
	private int threadCount = 0;
	/**
	 * �����߳�
	 */
	private transient RunThread runThread = null;
	/**
	 * ����ռ�
	 */
	private JobSpace jobSpace;

	/**
	 * ִ��
	 */
	public void run() {
		if (!prepareStart()) {
			return;
		}
		if (stepInfo == null)
			if (jobSpace == null)
				return;
		beforeRun();
		threadCount++;
		synchronized (threadLock) {
			runThread = new RunThread(tg, "t" + threadCount, false);
			runThread.start();
		}
	}

	/**
	 * �Ƿ񵥲����Ե��Ӵ��ڡ�true�ǵ��Խ���򿪵��ļ���false���½����ߴ��ļ��򿪵��ļ�
	 * 
	 * @return
	 */
	private boolean isSubSheet() {
		return stepInfo != null;
	}

	/**
	 * ����ִ��
	 * 
	 * @param debugType
	 *            ���Է�ʽ
	 */
	public void debug(byte debugType) {
		synchronized (threadLock) {
			if (runThread == null) {
				if (!prepareStart())
					return;
				if (!isSubSheet())
					if (jobSpace == null)
						return;
				beforeRun();
				threadCount++;
				runThread = new RunThread(tg, "t" + threadCount, true);
				runThread.setDebugType(debugType);
				runThread.start();
			} else {
				preventRun();
				runThread.continueRun(debugType);
			}
		}
	}

	/**
	 * ��ͣ���߼���ִ��
	 */
	public synchronized void pause() {
		synchronized (threadLock) {
			if (runThread == null)
				return;
			if (runThread.getRunState() == RunThread.PAUSED) {
				runThread.continueRun();
			} else {
				runThread.pause();
			}
		}
	}

	/**
	 * ִ�е�׼������
	 * 
	 * @return
	 */
	private boolean prepareStart() {
		try {
			preventRun();
			reset();
			if (!isSubSheet())
				if (!prepareArg())
					return false;
			if (stepInfo == null) {
				String uuid = UUID.randomUUID().toString();
				jobSpace = JobSpaceManager.getSpace(uuid);
				dfxCtx.setJobSpace(jobSpace);
			}
			tg = new ThreadGroup(filePath);
			threadCount = 0;
			return true;
		} catch (Throwable e) {
			GM.showException(e);
			resetRunState();
			return false;
		}
	}

	/**
	 * ִ��ǰ��������
	 */
	private void beforeRun() {
		dfxControl.contentView.submitEditor();
		dfxControl.contentView.initEditor(ContentPanel.MODE_HIDE);
		GVDfx.panelValue.tableValue.setValue(null);
		if (GVDfx.appFrame instanceof DFX) {
			PanelConsole pc = ((DFX) GVDfx.appFrame).getPanelConsole();
			if (pc != null)
				pc.autoClean();
		}
	}

	/**
	 * ���������Ƿ�������ļ�����ֹ2�δ�
	 */
	private boolean subSheetOpened = false;
	/**
	 * �������Ե���ҳ�б���˳���
	 */
	public List<SheetDfx> sheets = null;

	/**
	 * ȡ��ҳ����
	 * 
	 * @return
	 */
	private SheetDfx getParentSheet() {
		if (sheets == null)
			return null;
		for (int i = 0; i < sheets.size(); i++) {
			if (sheets.get(i) == this) {
				if (i == 0)
					return null;
				else
					return sheets.get(i - 1);
			}
		}
		return null;
	}

	/**
	 * ȡ��ҳ����
	 * 
	 * @return
	 */
	private SheetDfx getSubSheet() {
		if (sheets == null)
			return null;
		for (int i = 0; i < sheets.size(); i++) {
			if (sheets.get(i) == this) {
				if (i == sheets.size() - 1)
					return null;
				else
					return sheets.get(i + 1);
			}
		}
		return null;
	}

	/**
	 * �������Ժ󣬽�����ҳִ�н��
	 * 
	 * @param returnVal
	 *            ����ֵ
	 * @param continueRun
	 *            �Ƿ����ִ��
	 */
	public void acceptResult(final Object returnVal, final boolean continueRun) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					SheetDfx subSheet = getSubSheet();
					if (subSheet != null) {
						((DFX) GV.appFrame).closeSheet(subSheet, false);
					}
					if (exeLocation == null)
						return;
					PgmNormalCell lastCell = (PgmNormalCell) dfxControl.dfx
							.getCell(exeLocation.getRow(), exeLocation.getCol());
					lastCell.setValue(returnVal);
					dfxControl.dfx.setCurrent(lastCell);
					dfxControl.dfx.setNext(exeLocation.getRow(),
							exeLocation.getCol() + 1, false);
					INormalCell nextCell = dfxControl.dfx.getCurrent();
					if (nextCell != null)
						setExeLocation(new CellLocation(nextCell.getRow(),
								nextCell.getCol()));
					else {
						setExeLocation(null);
						synchronized (threadLock) {
							if (runThread != null)
								runThread.continueRun();
						}
					}
					dfxControl.contentView.repaint();
					GV.appFrame.showSheet(SheetDfx.this);
					subSheetOpened = false;
					if (continueRun) {
						synchronized (threadLock) {
							if (runThread != null)
								runThread.continueRun();
						}
					}
				} catch (Exception e) {
					GM.showException(e);
				}
			}
		});

	}

	/**
	 * ��������ֹͣ
	 * 
	 * @param stopOther
	 *            �Ƿ�ֹͣ����ҳ
	 */
	public void stepStop(boolean stopOther) {
		stepStopOther = stopOther;
		debug(RunThread.STEP_STOP);
	}

	/**
	 * ִ���߳�
	 *
	 */
	class RunThread extends Thread {
		/**
		 * �Ƿ����ģʽ
		 */
		private boolean isDebugMode = true;

		/** ִ����� */
		static final byte FINISH = 0;
		/** ����ִ�� */
		static final byte RUN = 1;
		/** ��ִͣ�� */
		static final byte PAUSING = 2;
		/** ִ�б���ͣ�� */
		static final byte PAUSED = 3;
		/**
		 * ִ�е�״̬
		 */
		private Byte runState = FINISH;

		/** ����ִ�� */
		static final byte DEBUG = 1;
		/** ִ�е���� */
		static final byte CURSOR = 2;
		/** �������� */
		static final byte STEP_OVER = 3;
		/** �������Խ��� */
		static final byte STEP_INTO = 4;
		/** �������Է��� */
		static final byte STEP_RETURN = 5;
		/** ֻ�������̵߳ȴ���ʲô������ */
		static final byte STEP_INTO_WAIT = 6;
		/** �������Է��غ����ִ�� */
		static final byte STEP_RETURN1 = 7;
		/** ��������ֹͣ */
		static final byte STEP_STOP = 8;
		/**
		 * ���Է�ʽ
		 */
		private byte debugType = DEBUG;
		/**
		 * �Ƿ���ͣ��
		 */
		private Boolean isPaused = Boolean.FALSE;
		/**
		 * ��ǰ�������
		 */
		private CellLocation clCursor = null;
		/**
		 * ��ǰ�������
		 */
		private PgmCellSet curCellSet;

		/**
		 * ���캯��
		 * 
		 * @param tg
		 *            �߳���
		 * @param name
		 *            �߳�����
		 * @param isDebugMode
		 *            �Ƿ����ģʽ
		 */
		public RunThread(ThreadGroup tg, String name, boolean isDebugMode) {
			super(tg, name);
			this.isDebugMode = isDebugMode;
			curCellSet = dfxControl.dfx;
		}

		/**
		 * ִ��
		 */
		public void run() {
			runState = RUN;
			resetRunState();
			boolean isThreadDeath = false;
			boolean hasReturn = false;
			Object returnVal = null;
			try {
				do {
					synchronized (runState) {
						if (runState == PAUSING) {
							stepFinish();
							if (!GVDfx.panelDfxWatch.isCalculating())
								GVDfx.panelDfxWatch.watch(dfxControl.dfx
										.getContext());
						}
					}
					while (isPaused) {
						try {
							sleep(5);
						} catch (Exception e) {
						}
					}

					if (debugType != STEP_INTO_WAIT) {
						long start = System.currentTimeMillis();
						PgmNormalCell pnc = null;
						if (exeLocation != null) {
							pnc = curCellSet.getPgmNormalCell(
									exeLocation.getRow(), exeLocation.getCol());
						} else if (curCellSet.getCurrent() != null) {
							INormalCell icell = curCellSet.getCurrent();
							pnc = curCellSet.getPgmNormalCell(icell.getRow(),
									icell.getCol());
						}
						if (pnc != null) {
							if (stepInfo != null && stepInfo.endRow > -1) {
								if (pnc.getRow() > stepInfo.endRow) {
									break;
								}
							}
						}
						// �������Խ���
						if (debugType == STEP_INTO) {
							if (!subSheetOpened) {
								if (pnc != null) {
									Expression exp = pnc.getExpression();
									if (exp != null) {
										Node home = exp.getHome();
										if (home instanceof Call) { // call����
											Call call = (Call) home;
											PgmCellSet subCellSet = call
													.getCallPgmCellSet(dfxCtx);
											subCellSet.setCurrent(subCellSet
													.getPgmNormalCell(1, 1));
											subCellSet.setNext(1, 1, true); // ���Ӹ�ʼִ��
											openSubSheet(pnc, subCellSet, null,
													null, -1, call);
										} else if (home instanceof Func) { // Func��
											// Funcʹ��������Ϊ��֧�ֵݹ�
											Func func = (Func) home;
											CallInfo ci = func
													.getCallInfo(dfxCtx);
											PgmCellSet cellSet = ci
													.getPgmCellSet();
											int row = ci.getRow();
											int col = ci.getCol();
											Object[] args = ci.getArgs();
											int rc = cellSet.getRowCount();
											int cc = cellSet.getColCount();
											if (row < 1 || row > rc || col < 1
													|| col > cc) {
												MessageManager mm = EngineMessage
														.get();
												throw new RQException(
														mm.getMessage("engine.callNeedSub"));
											}

											PgmNormalCell nc = cellSet
													.getPgmNormalCell(row, col);
											Command command = nc.getCommand();
											if (command == null
													|| command.getType() != Command.FUNC) {
												MessageManager mm = EngineMessage
														.get();
												throw new RQException(
														mm.getMessage("engine.callNeedSub"));
											}

											// ����������ĸ���
											PgmCellSet pcs = cellSet.newCalc();
											int endRow = cellSet
													.getCodeBlockEndRow(row,
															col);
											for (int r = row; r <= endRow; ++r) {
												for (int c = col; c <= cc; ++c) {
													INormalCell tmp = cellSet
															.getCell(r, c);
													INormalCell cellClone = (INormalCell) tmp
															.deepClone();
													cellClone.setCellSet(pcs);
													pcs.setCell(r, c, cellClone);
												}
											}
											int colCount = pcs.getColCount();

											// �Ѳ���ֵ�赽func��Ԫ���ϼ�����ĸ���
											if (args != null) {
												int paramRow = row;
												int paramCol = col;
												for (int i = 0, pcount = args.length; i < pcount; ++i) {
													pcs.getPgmNormalCell(
															paramRow, paramCol)
															.setValue(args[i]);
													if (paramCol < colCount) {
														paramCol++;
													} else {
														break;
													}
												}
											}
											pcs.setCurrent(pcs
													.getPgmNormalCell(row, col));
											pcs.setNext(row, col + 1, false); // ���Ӹ�ʼִ��
											openSubSheet(pnc, pcs, ci,
													new CellLocation(row,
															col + 1), endRow,
													null);
										}
										final SheetDfx subSheet = getSubSheet();
										if (subSheet != null) {
											SwingUtilities
													.invokeLater(new Runnable() {
														public void run() {
															try {
																subSheet.debug(STEP_INTO_WAIT);
															} catch (Exception e) {
																GM.showException(e);
															}
														}
													});
										}
									}
								}
							} else {
								final SheetDfx subSheet = getSubSheet();
								if (subSheet != null) {
									SwingUtilities.invokeLater(new Runnable() {
										public void run() {
											try {
												((DFX) GV.appFrame)
														.showSheet(subSheet);
											} catch (Exception e) {
											}
										}
									});
								}
							}
						} else if (debugType == STEP_RETURN) {
							isDebugMode = false; // һֱ����
							debugType = STEP_RETURN1;
						} else if (debugType == STEP_STOP) {
							isStepStop = true;
							if (stepStopOther) {
								if (sheets != null)
									for (SheetDfx sheet : sheets) {
										if (sheet != SheetDfx.this)
											sheet.stepStop(false);
									}
							}
							return; // ֱ�ӽ��������߳�
						} else {
							if (pnc == null) {
								exeLocation = curCellSet.runNext();
							} else {
								if (stepInfo != null && stepInfo.endRow > -1) {
									Command cmd = pnc.getCommand();
									if (cmd != null
											&& cmd.getType() == Command.RETURN) {
										hasReturn = true;
										Expression exp1 = cmd.getExpression(
												curCellSet, dfxCtx);
										if (exp1 != null) {
											returnVal = exp1.calculate(dfxCtx);
										}
										break;
									}
								}
								exeLocation = curCellSet.runNext();
							}
						}
						if (isDebugMode && pnc != null) {
							long end = System.currentTimeMillis();
							String cellId = CellLocation.getCellId(
									pnc.getRow(), pnc.getCol());
							debugTimeMap.put(cellId, end - start);
						}
					}
					if (isDebugMode) {
						if (checkBreak()) {
							if (!GVDfx.panelDfxWatch.isCalculating()) {
								GVDfx.panelDfxWatch.watch(dfxControl.dfx
										.getContext());
							}
							while (true) {
								if (isPaused) {
									try {
										sleep(5);
									} catch (Exception e) {
									}
								} else {
									break;
								}
							}
						}
					}
				} while (exeLocation != null);
			} catch (ThreadDeath td) {
				isThreadDeath = true;
			} catch (Throwable x) {
				if (x != null) {
					Throwable cause = x.getCause();
					if (cause != null && cause instanceof ThreadDeath) {
						isThreadDeath = true;
					}
				}
				if (!isThreadDeath) {
					String msg = x.getMessage();
					if (!StringUtils.isValidString(msg)) {
						StringBuffer sb = new StringBuffer();
						Throwable t = x.getCause();
						if (t != null) {
							sb.append(t.getMessage());
							sb.append("\r\n");
						}
						StackTraceElement[] ste = x.getStackTrace();
						for (int i = 0; i < ste.length; i++) {
							sb.append(ste[i]);
							sb.append("\r\n");
						}
						msg = sb.toString();
						showException(msg);
					} else {
						showException(x);
					}
				}
			} finally {
				runState = FINISH;
				if (!isThreadDeath)
					resetRunState(false, true);
				GVDfx.panelDfxWatch.watch(dfxControl.dfx.getContext());
				closeRunThread();
				// ������ӳ��򣬼�����ɺ�رյ�ǰ������ʾ��ҳ��
				SheetDfx parentSheet = getParentSheet();
				if (stepInfo != null && !isStepStop) { // ����ж��ӳ���Ͳ��ٷ���ֵ��
					if (!isThreadDeath) {
						if (returnVal == null && !hasReturn) {
							if (stepInfo.endRow > -1) {
								// δ����returnȱʡ���ش���������һ�������ֵ
								int endRow = stepInfo.endRow;
								CallInfo ci = stepInfo.callInfo;
								for (int r = endRow; r >= ci.getRow(); --r) {
									for (int c = curCellSet.getColCount(); c > ci
											.getCol(); --c) {
										PgmNormalCell cell = curCellSet
												.getPgmNormalCell(r, c);
										if (cell.isCalculableCell()
												|| cell.isCalculableBlock()) {
											returnVal = cell.getValue();
										}
									}
								}
							} else {
								if (curCellSet.hasNextResult()) {
									returnVal = curCellSet.nextResult();
								}
							}
						}
					}
					if (parentSheet != null)
						parentSheet.acceptResult(returnVal, debugType == DEBUG);
				}
				if (!isStepStop) {
					if (sheets != null) {
						if (parentSheet == null) // ��������sheets
							sheets = null;
					}
				}
			}
		}

		/**
		 * �������Խ������ҳ
		 * 
		 * @param pnc
		 *            �������
		 * @param subCellSet
		 *            ���������
		 * @param ci
		 *            CallInfo����
		 * @param startLocation
		 *            ������ʼ���������
		 * @param endRow
		 *            ����������
		 * @param call
		 *            Call����
		 */
		private void openSubSheet(PgmNormalCell pnc,
				final PgmCellSet subCellSet, CallInfo ci,
				CellLocation startLocation, int endRow, Call call) {
			String newName = new File(filePath).getName();
			if (newName.toLowerCase().endsWith("." + GC.FILE_DFX)) {
				newName = newName.substring(0, newName.length() - 4);
			}
			String cellId = CellLocation.getCellId(pnc.getRow(), pnc.getCol());
			newName += "(" + cellId + ")";
			final String nn = newName;
			List<SheetDfx> sheets = SheetDfx.this.sheets;
			if (sheets == null) {
				sheets = new ArrayList<SheetDfx>();
				sheets.add(SheetDfx.this);
				SheetDfx.this.sheets = sheets;
			}
			final StepInfo stepInfo = new StepInfo(sheets);
			if (call != null) { // call dfx
				stepInfo.filePath = call.getDfxPathName(dfxCtx);
			} else if (SheetDfx.this.stepInfo == null) { // ��ǰ��������
				stepInfo.filePath = filePath;
			} else { // ��ǰ���ӳ��򣬴���һ����ȡ
				stepInfo.filePath = SheetDfx.this.stepInfo.filePath;
			}
			stepInfo.dfxCtx = dfxCtx;
			stepInfo.parentLocation = new CellLocation(pnc.getRow(),
					pnc.getCol());
			stepInfo.callInfo = ci;
			stepInfo.startLocation = startLocation;
			stepInfo.endRow = endRow;
			stepInfo.parentCall = call;
			subSheetOpened = true;
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					((DFX) GV.appFrame).openSheet(nn, subCellSet, false,
							stepInfo);
				}
			});
		}

		/**
		 * �����������
		 */
		private void stepFinish() {
			isPaused = Boolean.TRUE;
			runState = PAUSED;
			resetRunState(false, true);
		}

		/**
		 * ���ϵ�
		 * 
		 * @return
		 */
		private boolean checkBreak() {
			if (exeLocation == null)
				return false;
			if (debugType == STEP_INTO_WAIT || debugType == STEP_OVER
					|| debugType == STEP_INTO) {
				stepFinish();
				if (ConfigOptions.bStepLastLocation.booleanValue()) {
					if (lastLocation != null) {
						SwingUtilities.invokeLater(new Thread() {
							public void run() {
								dfxEditor.selectCell(lastLocation.getRow(),
										lastLocation.getCol());
							}
						});
					}
				}
				return true;
			}
			if (dfxControl.isBreakPointCell(exeLocation.getRow(),
					exeLocation.getCol())) {
				stepFinish();
				return true;
			}
			if (debugType == CURSOR) {
				if (clCursor != null && exeLocation.equals(clCursor)) {
					stepFinish();
					return true;
				}
			}
			return false;
		}

		/**
		 * ��ͣ
		 */
		public void pause() {
			runState = PAUSING;
			resetRunState(false, false);
		}

		/**
		 * ȡִ��״̬
		 * 
		 * @return
		 */
		public byte getRunState() {
			return runState;
		}

		/**
		 * ���õ�������
		 * 
		 * @param debugType
		 */
		public void setDebugType(byte debugType) {
			this.debugType = debugType;
			if (debugType == CURSOR) {
				CellLocation activeCell = dfxControl.getActiveCell();
				if (activeCell != null)
					clCursor = new CellLocation(activeCell.getRow(),
							activeCell.getCol());
			}
		}

		/**
		 * �Ƿ����ģʽ
		 * 
		 * @return
		 */
		public boolean isDebugMode() {
			return isDebugMode;
		}

		/**
		 * ����ִ��
		 */
		public void continueRun() {
			continueRun(DEBUG);
		}

		/**
		 * ����ִ��
		 * 
		 * @param debugType
		 *            ���Է�ʽ
		 */
		public void continueRun(byte debugType) {
			runState = RUN;
			setDebugType(debugType);
			resetRunState();
			isPaused = Boolean.FALSE;
		}

		/**
		 * �ر��߳�
		 */
		public void closeThread() {
			pause();
			closeResource();
		}
	}

	/**
	 * �ر�ҳ��������¼���ʱ���ر�����ռ�
	 */
	private void closeSpace() {
		if (jobSpace != null)
			JobSpaceManager.closeSpace(jobSpace.getID());
	}

	/**
	 * ������ɻ��жϺ���������ռ����Դ�������������
	 */
	private void closeResource() {
		if (jobSpace != null)
			jobSpace.closeResource();
	}

	/**
	 * �߳����쳣
	 * 
	 * @param ex
	 */
	private void showException(final Object ex) {
		SwingUtilities.invokeLater(new Thread() {
			public void run() {
				if (ex != null)
					GM.showException(ex);
			}
		});
	}

	/**
	 * ִ�в˵�״̬��Ϊ�����ã���ֹ�������
	 */
	private void preventRun() {
		setMenuToolEnabled(new short[] { GCDfx.iEXEC, GCDfx.iEXE_DEBUG,
				GCDfx.iSTEP_CURSOR, GCDfx.iSTEP_NEXT, GCDfx.iCALC_AREA,
				GCDfx.iCALC_LOCK }, false);
	}

	/**
	 * ����ִ�в˵�״̬
	 */
	public void resetRunState() {
		resetRunState(false, false);
	}

	/**
	 * ����ִ�в˵�״̬
	 * 
	 * @param isRefresh
	 *            �Ƿ�ˢ�·������õ�
	 * @param afterRun
	 *            �Ƿ�ִ�н������õ�
	 */
	private synchronized void resetRunState(final boolean isRefresh,
			final boolean afterRun) {
		SwingUtilities.invokeLater(new Thread() {
			public void run() {
				resetRunStateThread(isRefresh, afterRun);
			}
		});
	}

	/**
	 * ����ִ�в˵�״̬�߳�
	 * 
	 * @param isRefresh
	 *            �Ƿ�ˢ�·������õ�
	 * @param afterRun
	 *            �Ƿ�ִ�н������õ�
	 */
	private synchronized void resetRunStateThread(boolean isRefresh,
			boolean afterRun) {
		if (!(GV.appMenu instanceof MenuDfx))
			return;
		MenuDfx md = (MenuDfx) GV.appMenu;

		if (isStepStop) {
			setMenuToolEnabled(new short[] { GCDfx.iEXEC, GCDfx.iEXE_DEBUG,
					GCDfx.iSTEP_CURSOR, GCDfx.iSTEP_NEXT, GCDfx.iSTEP_INTO,
					GCDfx.iCALC_AREA, GCDfx.iCALC_LOCK, GCDfx.iSTEP_RETURN,
					GCDfx.iSTEP_STOP, GCDfx.iPAUSE }, false);
			setMenuToolEnabled(new short[] { GCDfx.iSTOP }, true); // ֻ���ж���
			boolean editable = dfxControl.dfx.getCurrentPrivilege() == PgmCellSet.PRIVILEGE_FULL;
			if (!isRefresh) {
				dfxEditor.getComponent().getContentPanel()
						.setEditable(editable);
				if (editable)
					dfxControl.contentView.initEditor(ContentPanel.MODE_HIDE);
			}
			if (afterRun) {
				setExeLocation(exeLocation);
				dfxControl.contentView.repaint();
				refresh();
			}
			return;
		}

		boolean isPaused = false;
		boolean editable = true;
		boolean canStepInto = canStepInto();
		boolean isDebugMode = false;
		boolean isThreadNull;
		byte runState = RunThread.FINISH;
		synchronized (threadLock) {
			if (runThread != null) {
				synchronized (runThread) {
					isThreadNull = runThread == null;
					if (!isThreadNull) {
						isDebugMode = runThread.isDebugMode;
						runState = runThread.getRunState();
					}
				}
			} else {
				isThreadNull = true;
			}
		}
		if (isThreadNull) {
			setMenuToolEnabled(new short[] { GCDfx.iEXEC, GCDfx.iEXE_DEBUG },
					stepInfo == null);
			setMenuToolEnabled(new short[] { GCDfx.iSTEP_CURSOR,
					GCDfx.iSTEP_NEXT }, true);
			setMenuToolEnabled(new short[] { GCDfx.iPAUSE }, false);
			setMenuToolEnabled(new short[] { GCDfx.iSTOP }, stepInfo != null);
			setMenuToolEnabled(new short[] { GCDfx.iSTEP_INTO }, canStepInto
					&& stepInfo != null);
			setMenuToolEnabled(new short[] { GCDfx.iSTEP_RETURN },
					stepInfo != null);
			setMenuToolEnabled(new short[] { GCDfx.iSTEP_STOP },
					stepInfo != null && stepInfo.parentCall != null);
			setMenuToolEnabled(
					new short[] { GCDfx.iCALC_AREA, GCDfx.iCALC_LOCK },
					canRunCell() && (stepInfo == null || isStepStop));
		} else {
			switch (runState) {
			case RunThread.RUN:
				setMenuToolEnabled(new short[] { GCDfx.iEXEC, GCDfx.iEXE_DEBUG,
						GCDfx.iSTEP_CURSOR, GCDfx.iSTEP_NEXT, GCDfx.iSTEP_INTO,
						GCDfx.iCALC_AREA, GCDfx.iCALC_LOCK }, false);
				setMenuToolEnabled(new short[] { GCDfx.iSTEP_RETURN },
						stepInfo != null);
				setMenuToolEnabled(new short[] { GCDfx.iSTEP_STOP },
						stepInfo != null && stepInfo.parentCall != null);
				setMenuToolEnabled(new short[] { GCDfx.iPAUSE, GCDfx.iSTOP },
						true);
				editable = false;
				break;
			case RunThread.PAUSING:
				setMenuToolEnabled(new short[] { GCDfx.iEXEC, GCDfx.iEXE_DEBUG,
						GCDfx.iSTEP_CURSOR, GCDfx.iSTEP_NEXT, GCDfx.iSTEP_INTO,
						GCDfx.iCALC_AREA, GCDfx.iCALC_LOCK, GCDfx.iPAUSE },
						false);
				setMenuToolEnabled(new short[] { GCDfx.iSTEP_RETURN },
						stepInfo != null);
				setMenuToolEnabled(new short[] { GCDfx.iSTEP_STOP },
						stepInfo != null && stepInfo.parentCall != null);
				setMenuToolEnabled(new short[] { GCDfx.iSTOP }, true);
				break;
			case RunThread.PAUSED:
				setMenuToolEnabled(
						new short[] { GCDfx.iEXEC, GCDfx.iEXE_DEBUG }, false);
				setMenuToolEnabled(new short[] { GCDfx.iSTEP_CURSOR,
						GCDfx.iSTEP_NEXT }, isDebugMode);
				setMenuToolEnabled(new short[] { GCDfx.iSTEP_INTO },
						canStepInto);
				setMenuToolEnabled(new short[] { GCDfx.iSTEP_RETURN },
						stepInfo != null);
				setMenuToolEnabled(new short[] { GCDfx.iSTEP_STOP },
						stepInfo != null && stepInfo.parentCall != null);
				setMenuToolEnabled(new short[] { GCDfx.iPAUSE, GCDfx.iSTOP },
						true);
				isPaused = true;
				break;
			case RunThread.FINISH:
				setMenuToolEnabled(new short[] { GCDfx.iEXEC, GCDfx.iEXE_DEBUG,
						GCDfx.iSTEP_CURSOR, GCDfx.iSTEP_NEXT }, true);
				setMenuToolEnabled(new short[] { GCDfx.iSTEP_INTO,
						GCDfx.iSTEP_RETURN, GCDfx.iSTEP_STOP }, false);
				setMenuToolEnabled(new short[] { GCDfx.iPAUSE, GCDfx.iSTOP },
						false);
				setMenuToolEnabled(new short[] { GCDfx.iCALC_AREA,
						GCDfx.iCALC_LOCK }, canRunCell());
				break;
			}
		}
		if (dfxControl.dfx.getCurrentPrivilege() != PgmCellSet.PRIVILEGE_FULL) {
			setMenuToolEnabled(new short[] { GCDfx.iEXE_DEBUG,
					GCDfx.iSTEP_CURSOR, GCDfx.iSTEP_NEXT, GCDfx.iSTEP_INTO,
					GCDfx.iSTEP_RETURN, GCDfx.iSTEP_STOP, GCDfx.iPAUSE }, false);
			isPaused = false;
			editable = false;
		}
		if (stepInfo != null) {
			editable = false;
		}
		md.resetPauseMenu(isPaused);
		((ToolBarDfx) GVDfx.appTool).resetPauseButton(isPaused);
		if (!isRefresh)
			dfxEditor.getComponent().getContentPanel().setEditable(editable);

		if (afterRun) {
			setExeLocation(exeLocation);
			dfxControl.contentView.repaint();
			refresh();
		}
	}

	/**
	 * �Ƿ���Ե��Խ���
	 * 
	 * @return
	 */
	private boolean canStepInto() {
		try {
			INormalCell cell = dfxControl.dfx.getCurrent();
			if (!(cell instanceof PgmNormalCell)) {
				return false;
			}
			PgmNormalCell nc = (PgmNormalCell) cell;
			if (nc != null) {
				Expression exp = nc.getExpression();
				if (exp != null) {
					Node home = exp.getHome();
					if (home instanceof Call || home instanceof Func) {
						return true;
					}
				}
			}
		} catch (Throwable ex) {
		}
		return false;
	}

	/**
	 * ʹ�ú͹رռ����߳�ʱ��Ҫʹ�ô�����
	 */
	private byte[] threadLock = new byte[0];

	/**
	 * �ر�ִ���߳�
	 */
	private void closeRunThread() {
		synchronized (threadLock) {
			runThread = null;
		}
	}

	/**
	 * ���ò˵��͹������Ƿ����
	 * 
	 * @param ids
	 * @param enabled
	 */
	private void setMenuToolEnabled(short[] ids, boolean enabled) {
		MenuDfx md = (MenuDfx) GV.appMenu;
		for (int i = 0; i < ids.length; i++) {
			md.setMenuEnabled(ids[i], enabled);
			GVDfx.appTool.setButtonEnabled(ids[i], enabled);
		}
	}

	/**
	 * ִֹͣ��
	 */
	public synchronized void terminate() {
		if (sheets != null) { // �������Խ���
			int count = sheets.size();
			for (int i = 0; i < count; i++) {
				SheetDfx sheet = sheets.get(i);
				sheet.terminateSelf();
				if (sheet.stepInfo != null) {
					GV.appFrame.closeSheet(sheet);
					i--;
					count--;
				}
			}
			SheetDfx sheetParent = sheets.get(0);
			if (sheetParent != null) { // ��ʾ���յĸ�����
				try {
					sheetParent.stepInfo = null;
					GV.appFrame.showSheet(sheetParent);
				} catch (Exception e) {
					GM.showException(e);
				}
			}
			sheets = null;
		} else {
			terminateSelf();
		}
	}

	/**
	 * ִֹͣ�е�ǰҳ
	 */
	public synchronized void terminateSelf() {
		// ˳���Ϊ��ɱ�̺߳��ͷ���Դ
		Thread t = new Thread() {
			public void run() {
				synchronized (threadLock) {
					if (runThread != null) {
						synchronized (runThread) {
							if (runThread != null) {
								runThread.pause();
							}
							if (runThread != null
									&& runThread.getRunState() != RunThread.FINISH) {
								if (tg != null) {
									try {
										if (tg != null)
											tg.interrupt();
									} catch (Throwable t) {
									}
									try {
										if (tg != null) {
											int nthreads = tg.activeCount();
											Thread[] threads = new Thread[nthreads];
											if (tg != null)
												tg.enumerate(threads);
											for (int i = 0; i < nthreads; i++) {
												try {
													threads[i].stop();
												} catch (Throwable t1) {
												}
											}
										}
									} catch (Throwable t) {
									}
								}
							}
						}
					}
				}
				if (tg != null) {
					try {
						if (tg != null && tg.activeCount() != 0)
							sleep(100);
						tg.destroy();
					} catch (Throwable t1) {
					}
				}
				tg = null;
				closeRunThread();
				try {
					closeResource();
				} catch (Throwable t1) {
					t1.printStackTrace();
				}
				SwingUtilities.invokeLater(new Thread() {
					public void run() {
						setExeLocation(null);
						refresh(false, false);
						if (isStepStop) {
							isStepStop = !isStepStop;
							stepInfo = null;
							subSheetClosed();
							resetRunState(false, true);
						}
						dfxControl.repaint();
					}
				});

			}
		};
		t.setPriority(1);
		t.start();
	}

	/**
	 * ����������ʽ
	 * 
	 * @param exps
	 */
	public void setCellSetExps(Sequence exps) {
		ByteMap bm = dfxControl.dfx.getCustomPropMap();
		if (bm == null) {
			bm = new ByteMap();
		}
		bm.put(GC.CELLSET_EXPS, exps);
		dfxControl.dfx.setCustomPropMap(bm);
		setChanged(true);
	}

	/**
	 * ��ʾ��Ԫ��ֵ
	 */
	public void showCellValue() {
		dfxEditor.showCellValue();
	}

	/**
	 * ��һ��ִ�и�����
	 */
	private CellLocation lastLocation = null;

	/**
	 * ����ִ�и�����
	 * 
	 * @param cl
	 *            ����
	 */
	private void setExeLocation(CellLocation cl) {
		exeLocation = cl;
		if (cl != null) {
			dfxControl.setStepPosition(new CellLocation(cl.getRow(), cl
					.getCol()));
			lastLocation = new CellLocation(cl.getRow(), cl.getCol());
		} else {
			dfxControl.setStepPosition(null);
		}
	}

	/**
	 * ��Ԫ���Ƿ����ִ��
	 * 
	 * @return
	 */
	private boolean canRunCell() {
		if (dfxEditor == null || selectState == GCDfx.SELECT_STATE_NONE) {
			return false;
		}
		PgmNormalCell nc = (PgmNormalCell) dfxEditor.getDisplayCell();
		if (nc == null)
			return false;
		String expStr = nc.getExpString();
		if (!StringUtils.isValidString(expStr))
			return false;
		if (nc.getType() == PgmNormalCell.TYPE_COMMAND_CELL) {
			Command cmd = nc.getCommand();
			switch (cmd.getType()) {
			case Command.SQL:
				return true;
			default:
				return false;
			}
		}
		return true;
	}

	/**
	 * ׼������
	 * 
	 * @return
	 */
	private boolean prepareArg() {
		CellSet cellSet = dfxControl.dfx;
		ParamList paras = cellSet.getParamList();
		if (paras == null || paras.count() == 0) {
			return true;
		}
		if (paras.isUserChangeable()) {
			try {
				DialogInputArgument dia = new DialogInputArgument(dfxCtx);
				dia.setParam(paras);
				dia.setVisible(true);
				if (dia.getOption() != JOptionPane.OK_OPTION) {
					return false;
				}
				HashMap<String, Object> values = dia.getParamValue();
				Iterator<String> it = values.keySet().iterator();
				while (it.hasNext()) {
					String paraName = it.next();
					Object value = values.get(paraName);
					dfxCtx.setParamValue(paraName, value, Param.VAR);
				}
			} catch (Throwable t) {
				GM.showException(t);
			}
		} else {
			// ȡ��ʼֵ������������
			// resetʱ�Ѿ�����Ĭ��ֵ�����ﲻ������
			// CellSetUtil.putArgValue(cellSet, null);
			// for (int i = 0; i < paras.count(); i++) {
			// Param p = paras.get(i);
			// if (p.getKind() == Param.VAR)
			// dfxCtx.setParamValue(p.getName(), p.getValue(), Param.VAR);
			// }
		}
		return true;
	}

	/**
	 * ��������Ի���
	 */
	public void dialogParameter() {
		DialogArgument dp = new DialogArgument();
		dp.setParameter(dfxControl.dfx.getParamList());
		dp.setVisible(true);
		if (dp.getOption() == JOptionPane.OK_OPTION) {
			AtomicDfx ar = new AtomicDfx(dfxControl);
			ar.setType(AtomicDfx.SET_PARAM);
			ar.setValue(dp.getParameter());
			dfxEditor.executeCmd(ar);
		}
	}

	/**
	 * ��������Ի���
	 */
	public void dialogPassword() {
		DialogPassword dp = new DialogPassword();
		dp.setCellSet(dfxControl.dfx);
		dp.setVisible(true);
		if (dp.getOption() != JOptionPane.OK_OPTION) {
			return;
		}
		refresh();
		setChanged(true);
	}

	/**
	 * ��������
	 */
	private void dialogInputPassword() {
		DialogInputPassword dip = new DialogInputPassword(true);
		dip.setPassword(null);
		dip.setVisible(true);
		if (dip.getOption() == JOptionPane.OK_OPTION) {
			String psw = dip.getPassword();
			dfxControl.dfx.setCurrentPassword(psw);
			boolean isFull = dfxControl.dfx.getCurrentPrivilege() == PgmCellSet.PRIVILEGE_FULL;
			((MenuDfx) GV.appMenu).resetPasswordMenu(isFull);
			boolean lastEditable = dfxControl.contentView.isEditable();
			if (lastEditable != isFull) {
				dfxControl.contentView.setEditable(isFull);
				if (isFull)
					dfxControl.contentView.initEditor(ContentPanel.MODE_SHOW);
			}
			refresh();
		}
	}

	/**
	 * �ر�ҳ
	 */
	public boolean close() {
		// ��ֹͣ���б༭���ı༭
		((EditControl) dfxEditor.getComponent()).acceptText();
		boolean isChanged = dfxEditor.isDataChanged();
		// û���ӳ�������񣬻������ӳ������Ѿ��ж�ִ�е�call���񣬶���ʾ����
		if (isChanged && (stepInfo == null || isStepStopCall())) {
			String t1, t2;
			t1 = IdeCommonMessage.get().getMessage("public.querysave",
					IdeCommonMessage.get().getMessage("public.file"), filePath);
			t2 = IdeCommonMessage.get().getMessage("public.save");
			int option = JOptionPane.showConfirmDialog(GV.appFrame, t1, t2,
					JOptionPane.YES_NO_CANCEL_OPTION);
			switch (option) {
			case JOptionPane.YES_OPTION:
				if (!save())
					return false;
				break;
			case JOptionPane.NO_OPTION:
				break;
			default:
				return false;
			}
		}
		if (tg != null) {
			try {
				tg.interrupt();
				tg.destroy();
			} catch (Throwable t) {
			}
		}
		try {
			closeSpace();
		} catch (Throwable t) {
			GM.showException(t);
		}
		if (stepInfo != null && stepInfo.isCall()) {
			try {
				if (stepInfo.parentCall != null) {
					stepInfo.parentCall.finish(dfxControl.dfx);
				}
			} catch (Exception e) {
				GM.showException(e);
			}
		}
		GVDfx.panelValue.tableValue.setLocked1(false);
		GVDfx.panelValue.tableValue.setCellId(null);
		GVDfx.panelValue.tableValue.setValue(null);
		GVDfx.panelValue.setCellSet(null);
		storeBreakPoints();
		GM.setWindowDimension(this);
		dispose();
		if (stepInfo != null) {
			SheetDfx parentSheet = getParentSheet();
			if (parentSheet != null) {
				parentSheet.subSheetClosed();
			}
		}
		if (sheets != null) {
			sheets.remove(this);
		}
		return true;
	}

	/**
	 * ��ҳ�ر���
	 */
	public void subSheetClosed() {
		this.subSheetOpened = false;
	}

	/**
	 * ѡ���״̬������
	 */
	public void selectStateChanged(byte newState, boolean keyEvent) {
		selectState = newState;
		GVDfx.cmdSender = null;
		refresh(keyEvent);
	}

	/**
	 * ȡ��ǰѡ���״̬
	 * 
	 * @return
	 */
	public byte getSelectState() {
		return selectState;
	}

	/**
	 * �Ҽ������������˵���
	 */
	public void rightClicked(Component invoker, int x, int y) {
		popupDFX.getDFXPop(selectState).show(invoker, x, y);
	}

	/**
	 * ��ʾ��ǰ�񣬲�����ʱ��������ǰ��λ��
	 */
	public boolean scrollActiveCellToVisible = true;

	/**
	 * ����ִ�к�
	 */
	public void commandExcuted() {
		dfxEditor.selectAreas(scrollActiveCellToVisible);
		scrollActiveCellToVisible = true;
		refresh();
		dfxControl.repaint();
		ControlUtils.clearWrapBuffer();
	}

	/**
	 * ���ȱ��浱ǰ�ļ�
	 */
	private static final String ERROR_NOT_SAVE = IdeDfxMessage.get()
			.getMessage("sheetdfx.savefilebefore");

	/**
	 * ����ͬ���ı��ļ�
	 */
	public void importSameNameTxt() {
		if (stepInfo != null)
			return;
		try {
			if (GMDfx.isNewGrid(filePath, GCDfx.PRE_NEWPGM)) { // �½�
				JOptionPane.showMessageDialog(GV.appFrame, ERROR_NOT_SAVE);
				return;
			}
			File f = new File(filePath);
			if (!f.isFile() || !f.exists()) {
				JOptionPane.showMessageDialog(GV.appFrame, ERROR_NOT_SAVE);
				return;
			}
			synchronized (threadLock) {
				if (runThread != null) {
					// ��δִ����ɵ������Ƿ��ж�ִ�У�
					int option = JOptionPane.showOptionDialog(
							GV.appFrame,
							IdeDfxMessage.get().getMessage(
									"sheetdfx.queryclosethread"), IdeDfxMessage
									.get().getMessage("sheetdfx.closethread"),
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE, null, null, null);
					if (option == JOptionPane.OK_OPTION) {
						runThread.closeThread();
						try {
							Thread.sleep(50);
						} catch (Throwable t) {
						}
						terminate();
					}
				}
			}
			tg = null;
			closeRunThread();
			setExeLocation(null);
			EditControl control = (EditControl) dfxEditor.getComponent();
			boolean isEditable = control.getContentPanel().isEditable();
			PgmCellSet cellSet = dfxControl.dfx;
			String txtPath = filePath.substring(0, filePath.length()
					- GC.FILE_DFX.length())
					+ GC.FILE_TXT;
			CellRect rect = new CellRect(1, 1, cellSet.getRowCount(),
					cellSet.getColCount());
			Vector<IAtomicCmd> cmds = dfxEditor.getClearRectCmds(rect,
					DfxEditor.CLEAR);
			dfxEditor.executeCmd(cmds);
			CellSetTxtUtil.readCellSet(txtPath, cellSet);
			dfxEditor.setCellSet(cellSet);
			dfxCtx = new Context();
			Context pCtx = GMDfx.prepareParentContext();
			dfxCtx.setParent(pCtx);
			dfxControl.dfx.setContext(dfxCtx);
			resetRunState();
			refresh();
			dfxControl.repaint();
			dfxEditor.selectFirstCell();
			control.getContentPanel().setEditable(isEditable);
			control.getContentPanel().initEditor(ContentPanel.MODE_HIDE);
			control.reloadEditorText();
		} catch (Exception e) {
			GM.showException(e);
		}
	}

	/**
	 * ���µ����ļ�
	 */
	public void reloadFile() {
		try {
			if (GMDfx.isNewGrid(filePath, GCDfx.PRE_NEWPGM)) { // �½�
				JOptionPane.showMessageDialog(GV.appFrame, ERROR_NOT_SAVE);
				return;
			}
			File f = new File(filePath);
			if (!f.isFile() || !f.exists()) {
				JOptionPane.showMessageDialog(GV.appFrame, ERROR_NOT_SAVE);
				return;
			}
			synchronized (threadLock) {
				if (runThread != null) {
					// ��δִ����ɵ������Ƿ��ж�ִ�У�
					int option = JOptionPane.showOptionDialog(
							GV.appFrame,
							IdeDfxMessage.get().getMessage(
									"sheetdfx.queryclosethread"), IdeDfxMessage
									.get().getMessage("sheetdfx.closethread"),
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE, null, null, null);
					if (option == JOptionPane.OK_OPTION) {
						runThread.closeThread();
						try {
							Thread.sleep(50);
						} catch (Throwable t) {
						}
						terminate();
					}
				}
			}
			tg = null;
			closeRunThread();
			setExeLocation(null);
			EditControl control = (EditControl) dfxEditor.getComponent();
			boolean isEditable = control.getContentPanel().isEditable();
			PgmCellSet cellSet = CellSetUtil.readPgmCellSet(filePath);
			dfxEditor.setCellSet(cellSet);
			dfxCtx = new Context();
			Context pCtx = GMDfx.prepareParentContext();
			dfxCtx.setParent(pCtx);
			dfxControl.dfx.setContext(dfxCtx);
			resetRunState();
			refresh();
			dfxControl.repaint();
			dfxEditor.selectFirstCell();
			control.getContentPanel().setEditable(isEditable);
			control.getContentPanel().initEditor(ContentPanel.MODE_HIDE);
			control.reloadEditorText();
		} catch (Exception e) {
			GM.showException(e);
		}
	}

	/**
	 * ִ������
	 * 
	 * @param cmd
	 *            GCDfx�ж���ĳ���
	 */
	public void executeCmd(short cmd) {
		switch (cmd) {
		case GCDfx.iFILE_REOPEN:
			reloadFile();
			break;
		case GCDfx.iSAVE_FTP:
			saveFTP();
			break;
		case GC.iOPTIONS:
			boolean showDB = ConfigOptions.bShowDBStruct;
			new DialogOptions().setVisible(true);
			((DFX) GV.appFrame).refreshOptions();
			if (showDB != ConfigOptions.bShowDBStruct) {
				if (GVDfx.tabParam != null) {
					GVDfx.tabParam.resetEnv();
				}
			}
			break;
		case GCDfx.iRESET:
			reset();
			break;
		case GCDfx.iEXEC:
			run();
			break;
		case GCDfx.iEXE_DEBUG:
			debug(RunThread.DEBUG);
			break;
		case GCDfx.iPAUSE:
			pause();
			break;
		case GCDfx.iCALC_LOCK:
			calcActiveCell(true);
			break;
		case GCDfx.iCALC_AREA:
			calcActiveCell(false);
			break;
		case GCDfx.iSHOW_VALUE:
			showCellValue();
			break;
		case GCDfx.iSTEP_NEXT:
			debug(RunThread.STEP_OVER);
			break;
		case GCDfx.iSTEP_INTO:
			debug(RunThread.STEP_INTO);
			break;
		case GCDfx.iSTEP_RETURN:
			debug(RunThread.STEP_RETURN);
			break;
		case GCDfx.iSTEP_STOP:
			stepStop(true);
			break;
		case GCDfx.iSTEP_CURSOR:
			debug(RunThread.CURSOR);
			break;
		case GCDfx.iSTOP:
			this.terminate();
			break;
		case GCDfx.iBREAKPOINTS:
			dfxControl.setBreakPoint();
			break;
		case GCDfx.iUNDO:
			dfxEditor.undo();
			break;
		case GCDfx.iREDO:
			dfxEditor.redo();
			break;
		case GCDfx.iCOPY:
			dfxEditor.copy();
			break;
		case GCDfx.iCOPYVALUE:
			dfxEditor.copy(false, true);
			break;
		case GCDfx.iCODE_COPY:
			dfxEditor.codeCopy();
			break;
		case GCDfx.iCOPY_HTML:
			if (dfxEditor.canCopyPresent())
				dfxEditor.copyPresent();
			break;
		case GCDfx.iCOPY_HTML_DIALOG:
			dfxEditor.copyPresentDialog();
			break;
		case GCDfx.iCUT:
			dfxEditor.cut();
			break;
		case GCDfx.iPASTE:
			dfxEditor.paste(false);
			break;
		case GCDfx.iPASTE_ADJUST:
			dfxEditor.paste(true);
			break;
		case GCDfx.iPASTE_SPECIAL:
			byte o = getPasteOption();
			if (o != DfxEditor.PASTE_OPTION_NORMAL) {
				dfxEditor.paste(isAdjustPaste, o);
			}
			break;
		case GCDfx.iCLEAR_VALUE:
			dfxEditor.clear(DfxEditor.CLEAR_VAL);
			break;
		case GCDfx.iPARAM:
			dialogParameter();
			break;
		case GCDfx.iPASSWORD:
			if (dfxControl.dfx.getCurrentPrivilege() == PgmCellSet.PRIVILEGE_FULL)
				dialogPassword();
			else {
				dialogInputPassword();
			}
			break;
		case GCDfx.iCTRL_BACK:
			dfxControl.ctrlBackSpace();
			break;
		case GCDfx.iCLEAR:
			dfxEditor.clear(DfxEditor.CLEAR_EXP);
			break;
		case GCDfx.iFULL_CLEAR:
			dfxEditor.clear(DfxEditor.CLEAR);
			break;
		case GCDfx.iCTRL_DELETE:
			dfxControl.ctrlDelete();
			break;
		case GCDfx.iDELETE_COL:
		case GCDfx.iDELETE_ROW:
			dfxEditor.delete(cmd);
			break;
		case GCDfx.iTEXT_EDITOR:
			dfxEditor.textEditor();
			break;
		case GCDfx.iNOTE:
			dfxEditor.note();
			break;
		case GCDfx.iTIPS:
			dfxEditor.setTips();
			break;
		case GCDfx.iSEARCH:
			dialogSearch(false);
			break;
		case GCDfx.iREPLACE:
			dialogSearch(true);
			break;
		case GCDfx.iROW_HEIGHT:
			CellRect cr = dfxEditor.getSelectedRect();
			int row = cr.getBeginRow();
			float height = dfxControl.dfx.getRowCell(row).getHeight();
			DialogRowHeight drh = new DialogRowHeight(true, height);
			drh.setVisible(true);
			if (drh.getOption() == JOptionPane.OK_OPTION) {
				height = drh.getRowHeight();
				dfxEditor.setRowHeight(height);
			}
			break;
		case GCDfx.iCOL_WIDTH:
			cr = dfxEditor.getSelectedRect();
			int col = cr.getBeginCol();
			float width = dfxControl.dfx.getColCell(col).getWidth();
			drh = new DialogRowHeight(false, width);
			drh.setVisible(true);
			if (drh.getOption() == JOptionPane.OK_OPTION) {
				width = drh.getRowHeight();
				dfxEditor.setColumnWidth(width);
			}
			break;
		case GCDfx.iROW_ADJUST:
			dfxEditor.adjustRowHeight();
			break;
		case GCDfx.iCOL_ADJUST:
			dfxEditor.adjustColWidth();
			break;
		case GCDfx.iROW_HIDE:
			dfxEditor.setRowVisible(false);
			break;
		case GCDfx.iROW_VISIBLE:
			dfxEditor.setRowVisible(true);
			break;
		case GCDfx.iCOL_HIDE:
			dfxEditor.setColumnVisible(false);
			break;
		case GCDfx.iCOL_VISIBLE:
			dfxEditor.setColumnVisible(true);
			break;
		case GCDfx.iEDIT_CHART:
			dfxEditor.dialogChartEditor();
			break;
		case GCDfx.iFUNC_ASSIST:
			dfxEditor.dialogFuncEditor();
			break;
		case GCDfx.iDRAW_CHART:
			GVDfx.panelValue.tableValue.drawChart();
			break;
		case GCDfx.iCTRL_ENTER:
			dfxEditor.hotKeyInsert(DfxEditor.HK_CTRL_ENTER);
			break;
		case GCDfx.iCTRL_INSERT:
			dfxEditor.hotKeyInsert(DfxEditor.HK_CTRL_INSERT);
			break;
		case GCDfx.iALT_INSERT:
			dfxEditor.hotKeyInsert(DfxEditor.HK_ALT_INSERT);
			break;
		case GCDfx.iMOVE_COPY_UP:
		case GCDfx.iMOVE_COPY_DOWN:
		case GCDfx.iMOVE_COPY_LEFT:
		case GCDfx.iMOVE_COPY_RIGHT:
			dfxEditor.moveCopy(cmd);
			break;
		case GCDfx.iINSERT_COL:
			dfxEditor.insertCol(true);
			break;
		case GCDfx.iADD_COL:
			dfxEditor.insertCol(false);
			break;
		case GCDfx.iDUP_ROW:
			dfxEditor.dupRow(false);
			break;
		case GCDfx.iDUP_ROW_ADJUST:
			dfxEditor.dupRow(true);
			break;
		case GC.iPROPERTY:
			PgmCellSet pcs = (PgmCellSet) dfxEditor.getComponent().getCellSet();
			DialogCellSetProperties dcsp = new DialogCellSetProperties(
					pcs.getCurrentPrivilege() == PgmCellSet.PRIVILEGE_FULL);
			dcsp.setPropertyMap(pcs.getCustomPropMap());
			dcsp.setVisible(true);
			if (dcsp.getOption() == JOptionPane.OK_OPTION) {
				pcs.setCustomPropMap(dcsp.getPropertyMap());
				dfxEditor.setDataChanged(true);
			}
			break;
		case GCDfx.iCONST:
			DialogEditConst dce = new DialogEditConst(false);
			ParamList pl = Env.getParamList(); // GV.session
			Vector<String> usedNames = new Vector<String>();
			if (pl != null) {
				for (int j = 0; j < pl.count(); j++) {
					usedNames.add(((Param) pl.get(j)).getName());
				}
			}
			dce.setUsedNames(usedNames);
			dce.setParamList(dfxControl.dfx.getParamList());
			dce.setVisible(true);
			if (dce.getOption() == JOptionPane.OK_OPTION) {
				AtomicDfx ar = new AtomicDfx(dfxControl);
				ar.setType(AtomicDfx.SET_CONST);
				ar.setValue(dce.getParamList());
				dfxEditor.executeCmd(ar);
				refresh();
			}
			break;
		case GCDfx.iSQLGENERATOR: {
			DialogSelectDataSource dsds = new DialogSelectDataSource(
					DialogSelectDataSource.TYPE_SQL);
			dsds.setVisible(true);
			if (dsds.getOption() != JOptionPane.OK_OPTION) {
				return;
			}
			DataSource ds = dsds.getDataSource();
			try {
				DialogSQLEditor dse = new DialogSQLEditor(ds);
				dse.setCopyMode();
				dse.setVisible(true);
			} catch (Throwable ex) {
				GM.showException(ex);
			}
			break;
		}
		case GCDfx.iEXEC_CMD:
			// ��ֹͣ���б༭���ı༭
			((EditControl) dfxEditor.getComponent()).acceptText();
			boolean isChanged = dfxEditor.isDataChanged();
			if (isChanged) {
				String t1, t2;
				t1 = IdeCommonMessage.get().getMessage("public.querysave",
						IdeCommonMessage.get().getMessage("public.file"),
						filePath);
				t2 = IdeCommonMessage.get().getMessage("public.save");
				int option = JOptionPane.showConfirmDialog(GV.appFrame, t1, t2,
						JOptionPane.YES_NO_CANCEL_OPTION);
				switch (option) {
				case JOptionPane.YES_OPTION:
					if (!save())
						return;
					break;
				case JOptionPane.NO_OPTION:
					break;
				default:
					return;
				}
			}
			DialogExecCmd dec = new DialogExecCmd();
			if (StringUtils.isValidString(filePath)) {
				FileObject fo = new FileObject(filePath, "s", new Context());
				if (fo.isExists())
					dec.setDfxFile(filePath);
			}
			dec.setVisible(true);
			break;
		}
	}

	/**
	 * �Ƿ����ճ��
	 */
	private boolean isAdjustPaste = false;

	/**
	 * ȡճ��ѡ��
	 * 
	 * @return
	 */
	private byte getPasteOption() {
		byte option = DfxEditor.PASTE_OPTION_NORMAL;
		if (GVDfx.cellSelection != null) {
			switch (GVDfx.cellSelection.selectState) {
			case GC.SELECT_STATE_ROW: // ���ѡ�е�������
				option = DfxEditor.PASTE_OPTION_INSERT_ROW;
				break;
			case GC.SELECT_STATE_COL: // ���ѡ�е�������
				option = DfxEditor.PASTE_OPTION_INSERT_COL;
				break;
			}
		}
		if (option == DfxEditor.PASTE_OPTION_NORMAL) {
			DialogOptionPaste dop = new DialogOptionPaste();
			dop.setVisible(true);
			if (dop.getOption() == JOptionPane.OK_OPTION) {
				option = dop.getPasteOption();
				isAdjustPaste = dop.isAdjustExp();
			}
		}
		return option;
	}

	/**
	 * ȡ�������
	 * 
	 * @return �������
	 */
	public ICellSet getCellSet() {
		return dfxControl.getCellSet();
	}

	/**
	 * �����������
	 * 
	 * @param cellSet
	 *            �������
	 */
	public void setCellSet(Object cellSet) {
		try {
			dfxEditor.setCellSet((PgmCellSet) cellSet);
		} catch (Exception ex) {
		}
		this.repaint();
	}

	/**
	 * ���������Ƿ��޸���
	 * 
	 * @param isChanged
	 *            �����Ƿ��޸���
	 */
	public void setChanged(boolean isChanged) {
		dfxEditor.setDataChanged(isChanged);
	}

	/**
	 * ��ǰҳ������
	 *
	 */
	class Listener extends InternalFrameAdapter {
		/**
		 * ��ǰҳ����
		 */
		SheetDfx sheet;

		/**
		 * ���캯��
		 * 
		 * @param sheet
		 *            ҳ����
		 */
		public Listener(SheetDfx sheet) {
			super();
			this.sheet = sheet;
		}

		/**
		 * ��ǰҳ������
		 */
		public void internalFrameActivated(InternalFrameEvent e) {
			// ���߳������Եȴ���Ĵ��ڳ��׹رղż���ô���
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					GV.appSheet = sheet;

					GVDfx.dfxEditor = sheet.dfxEditor;
					sheet.dfxControl.repaint();
					GV.appFrame.changeMenuAndToolBar(
							((DFX) GV.appFrame).newMenuDfx(),
							GVDfx.getDfxTool());

					GV.appMenu.addLiveMenu(sheet.getSheetTitle());
					GV.appMenu.resetPrivilegeMenu();
					GM.setCurrentPath(sheet.getFileName());
					if (sheet.dfxControl == null) {
						GVDfx.panelValue.setCellSet(null);
						GVDfx.panelValue.tableValue.setValue(null);
						GVDfx.tabParam.resetParamList(null);
						return;
					}
					((ToolBarProperty) GV.toolBarProperty).init();
					sheet.refresh();
					sheet.resetRunState();
					((DFX) GV.appFrame).resetTitle();
					GV.toolWin.refreshSheet(sheet);
					sheet.selectFirstCell();
					GVDfx.panelDfxWatch.setCellSet(sheet.dfxControl.dfx);
					GVDfx.panelDfxWatch.watch(sheet.getDfxContext());
					GVDfx.panelValue.setCellSet(sheet.dfxControl.dfx);
					if (GVDfx.searchDialog != null
							&& GVDfx.searchDialog.isVisible()) {
						if (dfxEditor != null)
							GVDfx.searchDialog.setControl(dfxEditor);
					}
				}
			});
		}

		/**
		 * ��ǰҳ���ڹر�
		 */
		public void internalFrameClosing(InternalFrameEvent e) {
			GVDfx.appFrame.closeSheet(sheet);
			GV.toolBarProperty.setEnabled(false);
		}

		/**
		 * ��ǰҳ���ڷǼ���״̬
		 */
		public void internalFrameDeactivated(InternalFrameEvent e) {
			GVDfx.dfxEditor = null;
			// �����ı�û�л��ƣ����ڲ��ʱˢ��һ��
			sheet.dfxControl.repaint();
			GV.toolBarProperty.setEnabled(false);
			GVDfx.panelDfxWatch.setCellSet(null);
			if (GVDfx.matchWindow != null) {
				GVDfx.matchWindow.dispose();
				GVDfx.matchWindow = null;
			}
		}
	}

	/**
	 * �ύ��Ԫ��༭
	 */
	public boolean submitEditor() {
		try {
			dfxControl.contentView.submitEditor();
			return true;
		} catch (Exception ex) {
			GM.showException(ex);
		}
		return false;
	}

	/**
	 * ���񱣴浽�������
	 * 
	 * @param os
	 * @return
	 */
	public boolean saveOutStream(OutputStream os) {
		try {
			CellSetUtil.writePgmCellSet(os, (PgmCellSet) dfxControl.dfx);
			DfxManager.getInstance().clear();
			((PrjxAppMenu) GV.appMenu).refreshRecentFile(filePath);
		} catch (Throwable e) {
			GM.showException(e);
			return false;
		}
		dfxEditor.setDataChanged(false);
		dfxEditor.getDFXListener().commandExcuted();
		return true;
	}
}
