package com.radeusgd.java.weatherwidget;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Border;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.log4j.BasicConfigurator;
import com.jfoenix.controls.JFXDecorator;

/**
 * Created by Radek on 01.06.2017.
 */
public class AppMain extends Application{
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(AppMain.class);

    private static final String FXML_MAIN_FORM_TEMPLATE = "/fxml/widget-main.fxml";
    private static final String JFX_CSS = "/css/main.css";
    private static final String ICON = "/icons/icon.png";

    private Stage mainStage;

    public void start(Stage primaryStage) throws Exception {
        log.info("Starting Weather Widget...");

        mainStage = primaryStage;

        Parent pane = FXMLLoader.load(AppMain.class.getResource(FXML_MAIN_FORM_TEMPLATE));
        //Transform the main stage (aka the main window) into an undecorated window
        JFXDecorator decorator = new JFXDecorator(mainStage, pane, false, false, true);
        ObservableList<Node> buttonsList = ((Pane) decorator.getChildren().get(0)).getChildren();
        buttonsList.get(buttonsList.size() - 1).getStyleClass().add("close-button");



        Scene scene = new Scene(decorator);
        scene.setFill(null);

        scene.getStylesheets().addAll(AppMain.class.getResource(JFX_CSS).toExternalForm());

        mainStage.setScene(scene);

        mainStage.setWidth(420);
        mainStage.setHeight(240);
        mainStage.setResizable(false);

        mainStage.getIcons().add(new Image(AppMain.class.getResourceAsStream(ICON)));

        mainStage.show();

        log.info("Initialization successful");
    }

    public static void main(String[] args){
        BasicConfigurator.configure();//Prepare logging

        Thread.setDefaultUncaughtExceptionHandler(
                (t, e) -> log.error("Uncaught exception in thread \'" + t.getName() + "\'", e));//Log uncaught exceptions

        Platform.setImplicitExit(true);//Make application exit when windows is closed
        Application.launch(AppMain.class, args);
    }
}
