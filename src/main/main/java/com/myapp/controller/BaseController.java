package com.myapp.controller;

import com.google.common.eventbus.Subscribe;
import com.myapp.util.I18nUtil;
import javafx.application.Platform;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public abstract class BaseController implements Initializable {
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        I18nUtil.register(this);
        updateLocalizedText();
    }

    @Subscribe
    public void onLocaleChanged(I18nUtil.LocaleChangeEvent event) {
        Platform.runLater(this::updateLocalizedText);
    }

    protected abstract void updateLocalizedText();
}