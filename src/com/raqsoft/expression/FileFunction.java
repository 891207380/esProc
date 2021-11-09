package com.raqsoft.expression;

import com.raqsoft.dm.FileObject;

/**
 * �ļ���Ա��������
 * file.f()
 * @author RunQian
 *
 */
public abstract class FileFunction extends MemberFunction {
	protected FileObject file;
	
	public boolean isLeftTypeMatch(Object obj) {
		return obj instanceof FileObject;
	}

	public void setDotLeftObject(Object obj) {
		file = (FileObject)obj;
	}
}