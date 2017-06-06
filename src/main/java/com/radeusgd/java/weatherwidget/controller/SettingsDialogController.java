package com.radeusgd.java.weatherwidget.controller;

import com.jfoenix.controls.JFXDialog;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import rx.observables.JavaFxObservable;

/**
 * Created by Programistagd on 06.06.2017.
 */
public class SettingsDialogController {
    @FXML
    private JFXDialog dialog;

    @FXML
    private Button acceptButton;

    @FXML
    private Button cancelButton;

    @FXML
    private void initialize() {
        JavaFxObservable.actionEventsOf(acceptButton).subscribe(ignore -> {

        });

        JavaFxObservable.actionEventsOf(cancelButton).subscribe(ignore -> {
            dialog.close();
        });
    }

    public void show(StackPane pane) {
        dialog.show(pane);
    }
}
