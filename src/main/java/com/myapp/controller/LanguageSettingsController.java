package com.myapp.controller;

import com.myapp.util.I18nUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

/**
        * Controller for managing language settings in the application.
        * <p>
 * This controller handles the language selection functionality, allowing users to switch
        * between supported languages (English and Simplified Chinese). It updates the application's
        * locale settings and provides visual feedback when changes are saved.
        * </p>
 *
 * @author JiaYi Du
 * @version 2.0
 * @since March 17, 2023
        */
public class LanguageSettingsController extends BaseController {

    /** Combo box for language selection */
    @FXML
    private ComboBox<String> languageComboBox;

    /**
            * Initializes the controller after its root element has been processed.
            *
            * @param location The location used to resolve relative paths for the root object
     * @param resources The resources used to localize the root object
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        languageComboBox.setValue(getCurrentLanguageName());
    }

    /**
            * Saves the selected language setting.
     * <p>
     * Updates the application locale based on the user's selection and shows
            * a success notification. The change takes effect immediately for the UI.
            * </p>
            */
    @FXML
    private void saveLanguageSettings() {
        String selected = languageComboBox.getValue();
        Locale newLocale = selected.equals("简体中文") ?
                Locale.SIMPLIFIED_CHINESE : Locale.ENGLISH;

        I18nUtil.setLocale(newLocale);
        showSaveSuccess();
    }

    /**
            * Gets the display name of the current application language.
     *
             * @return "English" if current locale is English, otherwise "简体中文"
            */
    private String getCurrentLanguageName() {
        return I18nUtil.getCurrentLocale().equals(Locale.ENGLISH) ?
                "English" : "简体中文";
    }

    /**
            * Shows a success alert when language settings are saved.
     */
    private void showSaveSuccess() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(I18nUtil.get("alert.success"));
        alert.setHeaderText(I18nUtil.get("settings.saved"));
        alert.show();
    }

    /**
            * Updates localized text elements when language changes.
     * <p>
     * This method is called automatically when the locale changes to update
     * all text elements that need to be translated.
            * </p>
            */
    @Override
    protected void updateLocalizedText() {
        languageComboBox.setPromptText(I18nUtil.get("language.select"));
    }
}