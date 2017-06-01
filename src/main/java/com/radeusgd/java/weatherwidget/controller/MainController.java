package com.radeusgd.java.weatherwidget.controller;

import com.radeusgd.java.weatherwidget.control.ValueControl;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import rx.Observable;

/**
 * Created by Radek on 01.06.2017.
 */
public class MainController {
    private static final int ERROR_MSG_MAX_LENGTH = 400;
    private static final int ERROR_MSG_DURATION = 30;//Show error icon for 30s

    @FXML
    private ValueControl test1;

    @FXML
    private Node errorIcon;

    @FXML
    private Node workingIcon;

    @FXML
    private Button refreshButton;

    @FXML
    private Button settingsButton;

    @FXML
    private void initialize(){

    }

    public Observable<String> getTestStream() {
        //TODO FIXME
        return Observable.just("Test FIXME TODO");
    }
}
