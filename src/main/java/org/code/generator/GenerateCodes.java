package org.code.generator;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class GenerateCodes extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        InputValueForm dialog = new InputValueForm(e.getProject());
        dialog.pack();
        dialog.initLocation(e.getProject());
        dialog.setData();
        dialog.setVisible(true);


    }
}
