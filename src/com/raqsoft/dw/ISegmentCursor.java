package com.raqsoft.dw;

import com.raqsoft.dm.Sequence;

/**
 * �α�ķֶνӿ�
 * @author runqian
 *
 */
public interface ISegmentCursor {
	void setAppendData(Sequence seq);
	void setSegment(int startBlock, int endBlock);
	public TableMetaData getTableMetaData();
}