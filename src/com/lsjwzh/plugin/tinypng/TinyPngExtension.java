package com.lsjwzh.plugin.tinypng;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.WindowManager;
import com.tinify.Tinify;
import org.apache.http.util.TextUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class TinyPngExtension extends AnAction {
    public static final String TINY_PNG_API_KEY = "tiny_png_api_key";

    static ExecutorService sExecutorService = Executors.newSingleThreadExecutor();

    @Override
    public void actionPerformed(AnActionEvent actionEvent) {
        Project project = actionEvent.getProject();
        VirtualFile[] selectedFiles = PlatformDataKeys.VIRTUAL_FILE_ARRAY.getData(actionEvent.getDataContext());
        String apiKey = PropertiesComponent.getInstance().getValue(TINY_PNG_API_KEY);
        if (TextUtils.isEmpty(apiKey)) {
            apiKey = Messages.showInputDialog(project, "What's your ApiKey?", "ApiKey", Messages.getQuestionIcon());
            PropertiesComponent.getInstance().setValue(TINY_PNG_API_KEY, apiKey);
        }
        Tinify.setKey(apiKey);
        ProgressDialog dialog = new ProgressDialog();
        AtomicBoolean runningFlag = new AtomicBoolean(true);
        sExecutorService.submit(() -> {
            //writing to file
            int i = 1;
            int successCount = 0;
            int failCount = 0;
            List<VirtualFile> failFileList = new ArrayList<>();
            for (VirtualFile file : selectedFiles) {
                if (runningFlag.get() &&
                        file.getExtension() != null) {
                    dialog.setLabelMsg(i + "/" + selectedFiles.length + " processing :" + file.getName());
                    try {
                        Tinify.fromFile(file.getPath()).toFile(file.getPath());
                        dialog.setLabelMsg(i + "/" + selectedFiles.length + " process done :" + file.getName());
                        successCount++;
                    } catch (Throwable e) {
                        e.printStackTrace();
                        dialog.setLabelMsg(i + "/" + selectedFiles.length + " process error :" + file.getName());
                        failFileList.add(file);
                        failCount++;
                    }
                }
                i++;
            }
            dialog.setLabelMsg("Success :" + successCount + " Fail :" + failCount);
            dialog.setButtonOKVisible();
        });
        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                runningFlag.set(false);
            }
        });
        dialog.setLabelMsg("processing");
        JFrame frame = WindowManager.getInstance().getFrame(project);
        dialog.setMinimumSize(new Dimension(frame.getWidth() / 4, frame.getHeight() / 4));
        dialog.setLocationRelativeTo(frame);
        dialog.pack();
        dialog.setVisible(true);
    }
}
