package com.raqsoft.ide.starter;

import java.awt.event.ActionEvent;

import com.raqsoft.ide.common.ConfigMenuAction;
import com.raqsoft.ide.common.dialog.DialogDemoFiles;

/**
 * ����������
 * 
 * @author wunan
 *
 */
public class SampleProgramListener extends ConfigMenuAction {

	public void actionPerformed(ActionEvent arg0) {
		new DialogDemoFiles().setVisible(true);
	}
}