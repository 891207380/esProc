package com.raqsoft.expression;

import com.raqsoft.cellset.INormalCell;
import com.raqsoft.cellset.datamodel.PgmCellSet;
import com.raqsoft.common.MessageManager;
import com.raqsoft.common.RQException;
import com.raqsoft.dm.Context;
import com.raqsoft.resources.EngineMessage;

/**
 * ���μ��������г�������ĵ�Ԫ�񣬷������һ�������Ľ��
 * ??
 * @author RunQian
 *
 */
public class SubVal extends Node {
	private PgmCellSet pcs;

	public SubVal(PgmCellSet pcs) {
		this.pcs = pcs;
	}

	public Object calculate(Context ctx) {
		INormalCell cell = pcs.getCurrent();
		if (cell == null) {
			MessageManager mm = EngineMessage.get();
			throw new RQException(mm.getMessage("cellset.cellNotExist"));
		}

		return pcs.executeSubCell(cell.getRow(), cell.getCol());
	}
}
