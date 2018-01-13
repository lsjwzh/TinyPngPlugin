package com.lsjwzh.plugin.tinypng;

import com.intellij.openapi.components.PersistentStateComponent;
import org.jetbrains.annotations.Nullable;

public class TinyService implements PersistentStateComponent<TinyService.Setting> {

  static class Setting {
    public String apiKey;
  }

  Setting mySetting;

  @Nullable
  @Override
  public Setting getState() {
    return mySetting;
  }

  @Override
  public void loadState(Setting setting) {
    mySetting = setting;
  }

}