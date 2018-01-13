package com.lsjwzh.plugin.tinypng;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static com.lsjwzh.plugin.tinypng.TinyPngExtension.TINY_PNG_API_KEY;

public class SettingsEditor  implements Configurable {
    private JTextField tfApiKey;
    private JPanel contentPane;

    @Nls
    @Override
    public String getDisplayName() {
        return "TinyPng Setting";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        String apiKey = PropertiesComponent.getInstance().getValue(TINY_PNG_API_KEY);
        tfApiKey.setText(apiKey);
        return contentPane;
    }

    @Override
    public boolean isModified() {
        return true;
    }

    @Override
    public void apply() throws ConfigurationException {
        PropertiesComponent.getInstance().setValue(TINY_PNG_API_KEY, tfApiKey.getText());
    }
}
