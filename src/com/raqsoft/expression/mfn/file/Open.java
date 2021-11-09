package com.raqsoft.expression.mfn.file;

import java.io.File;

import com.raqsoft.dm.Context;
import com.raqsoft.dw.GroupTable;
import com.raqsoft.dw.TableMetaData;
import com.raqsoft.expression.FileFunction;
import com.raqsoft.parallel.ClusterFile;

/**
 * �����
 * f.open()
 * @author RunQian
 *
 */
public class Open extends FileFunction {
	public Object calculate(Context ctx) {
		if (file.isRemoteFile()) {
			// Զ���ļ�
			String host = file.getIP();
			int port = file.getPort();
			String fileName = file.getFileName();
			Integer partition = file.getPartition();
			int p = partition == null ? -1 : partition.intValue();
			ClusterFile cf = new ClusterFile(host, port, fileName, p, ctx);
			return cf.openGroupTable(ctx);
		} else {
			// �����ļ�
			File f = file.getLocalFile().file();
			TableMetaData table = GroupTable.openBaseTable(f, ctx);
			
			Integer partition = file.getPartition();
			if (partition != null && partition.intValue() > 0) {
				table.getGroupTable().setPartition(partition);
			}
			
			return table;
		}
	}
}
