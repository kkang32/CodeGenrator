package org.code.generator;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.wm.ex.WindowManagerEx;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class InputValueForm extends JDialog {
    private JPanel contentPane;
    private JScrollPane scrollPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTable tableVariables;
    private DefaultTableModel tableModel;
    private Project parentProject;
    private SettingService settingService;
    private final String TOKEN = "#";

    public InputValueForm(Project project) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        parentProject = project;

        settingService = SettingService.getInstance();

    }

    private void onOK() {



        //템플릿 경로
        String templateDirectoryPath = settingService.getTemplateDirectoryPath();
        //템플릿 경로 내 모든 목록 획득
        List<Path> fileList = getFileList(templateDirectoryPath);

        //System.out.println(parentProject.getBasePath());

        WriteCommandAction.runWriteCommandAction(parentProject, () -> {
            try {

                LocalFileSystem instance = LocalFileSystem.getInstance();
                //프로젝트의 root 경로
                VirtualFile projectBaseDir = instance.findFileByPath(parentProject.getBasePath());
                if (projectBaseDir != null) {
                    for(Path path : fileList) { //템플릿 경로 내 모든 파일 대상으로 작업
                        //템플릿의 절대경로를 획득
                        String fileName = path.toAbsolutePath().toString();
                        //절대 경로가 root 이거나 숨김파일일때 skip
                        if (templateDirectoryPath.equals(fileName) || fileName.contains(".DS_Store")) {
                            continue;
                        }
                        //절대경로로 파일 객체 생성
                        File f = new File(fileName);
                        if (f.isDirectory()){ //디렉토리일 경우

                            String childName = replaceFileName(f.getName());
                            if (projectBaseDir != null && projectBaseDir.findChild(childName) == null){
                                projectBaseDir.createChildDirectory(this, childName);
                            }
                            fileName = fileName.substring(templateDirectoryPath.length() + 1);
                            projectBaseDir = instance.findFileByPath(parentProject.getBasePath() + File.separator + fileName);
                        }else{//파일일 경우
                            String parentPath = f.getParentFile().getAbsolutePath().replaceAll(templateDirectoryPath + File.separator, "");
                            VfsUtil.createDirectories(parentProject.getBasePath() + File.separator + parentPath);
                            projectBaseDir = instance.findFileByPath(parentProject.getBasePath() + File.separator + parentPath);
                            String fileBody = Files.readString(Paths.get(fileName));
                            fileBody = replaceVariables(fileBody);
                            String childName = replaceFileName(f.getName());
                            if (projectBaseDir.findChild(childName) == null){
                                VirtualFile newFile = projectBaseDir.createChildData(this, childName);
                                VfsUtil.saveText(newFile, fileBody);
                            }
                        }
                        // 파일 시스템 갱신
                        VirtualFileManager.getInstance().syncRefresh();
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });


        dispose();
    }

    private List<Path> getFileList(String templateDirectoryPath) {
        List<Path> pathList = new ArrayList<>();
        try (Stream<Path> pathStream = Files.walk(Paths.get(templateDirectoryPath))) {
            pathList = pathStream.map(Path::normalize)
                    .toList();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return pathList;
    }

    private String replaceFileName(String fileName) {


        int rowCount = tableModel.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            String variable = (String)tableModel.getValueAt(i, 0);
            String value = (String)tableModel.getValueAt(i, 1);
            fileName = fileName.replaceAll(TOKEN + variable + TOKEN, value);
        }

        return fileName;
    }

    private String replaceVariables(String fileBody) {
        int rowCount = tableModel.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            String variable = (String)tableModel.getValueAt(i, 0);
            String value = (String)tableModel.getValueAt(i, 1);
            fileBody = fileBody.replaceAll(TOKEN + variable + TOKEN, value);
        }

        return fileBody;
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    //ide 안쪽에서 dialog 를 띄운다. pack 에서 사이즈를 설정하기 때문에 pack 이후에 호출되어야 함.
    public void initLocation(Project project){
        setLocationRelativeTo(WindowManagerEx.getInstanceEx().getIdeFrame(project).getComponent());
    }

    private void createUIComponents() {
        contentPane = new JPanel(new BorderLayout());
        tableModel = new DefaultTableModel();
        // 컬럼 헤더 설정
        tableModel.addColumn("Variable name");
        tableModel.addColumn("Value");

        // JTable 생성
        tableVariables = new JBTable(tableModel);

        // JScrollPane 생성 후 JTable 추가
        scrollPane = new JBScrollPane(tableVariables);

        // 패널 생성 후 JScrollPane 추가
        contentPane.add(scrollPane, BorderLayout.CENTER);
    }

    public void setData() {
        try {
            List<String> readAllLines = Files.readAllLines(Paths.get(settingService.getSettingFilePath()));
            readAllLines.forEach(line -> {
                tableModel.addRow(new Object[]{line, ""});
            });
        } catch (IOException e) {
            Messages.showMessageDialog(parentProject, e.getMessage(), "error!", Messages.getInformationIcon());
        }
    }
}
