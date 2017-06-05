package com.radeusgd.java.weatherwidget;

import com.radeusgd.java.weatherwidget.network.PollutionDataProvider;
import com.radeusgd.java.weatherwidget.network.WeatherDataSource;
import com.radeusgd.java.weatherwidget.network.WeatherProxy;
import com.radeusgd.java.weatherwidget.network.datasources.MeteoWaw;
import com.radeusgd.java.weatherwidget.network.datasources.OpenWeatherMap;
import com.radeusgd.java.weatherwidget.network.datasources.PowietrzeGiosGov;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.log4j.BasicConfigurator;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Radek on 01.06.2017.
 */
public class AppMain extends Application{
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(AppMain.class);

    private static final String FXML_MAIN_FORM_TEMPLATE = "/fxml/widget-main.fxml";
    private static final String JFX_CSS = "/css/jfx.css";

    private Stage mainStage;

    private void onClose() {
        log.info("onClose");

        /*if (closeDialogController == null) {
            closeDialogController = new CloseDialogController();
            FXMLLoader loader = new FXMLLoader(AppMain.class.getResource(FXML_CLOSE_DIALOG_TEMPLATE));
            loader.setController(closeDialogController);
            try {
                loader.load();
            } catch (IOException e) {
                log.error(e);
                throw new RuntimeException(e);
            }
        }

        closeDialogController.show(getMainPane());*/
    }

    public void start(Stage primaryStage) throws Exception {
        log.info("Starting Weather Widget...");

        mainStage = primaryStage;

        //setupTooltipDuration();

        //setupDataSources();

        //setupEventHandler();

        Parent pane = FXMLLoader.load(AppMain.class.getResource(FXML_MAIN_FORM_TEMPLATE));
        //Transform the main stage (aka the main window) into an undecorated window
        JFXDecorator decorator = new JFXDecorator(mainStage, pane, false, false, true);
        ObservableList<Node> buttonsList = ((Pane) decorator.getChildren().get(0)).getChildren();
        buttonsList.get(buttonsList.size() - 1).getStyleClass().add("close-button");
        //decorator.setOnCloseButtonAction(this::onClose);

        Scene scene = new Scene(decorator);
        scene.setFill(null);

        scene.getStylesheets().addAll(/*AppMain.class.getResource(FONT_CSS).toExternalForm(),
                AppMain.class.getResource(MATERIAL_CSS).toExternalForm(),*/
                AppMain.class.getResource(JFX_CSS).toExternalForm());

        mainStage.setScene(scene);

        mainStage.setWidth(400);
        mainStage.setHeight(300);
        mainStage.setResizable(false);

        //addLogo();

        mainStage.show();

        log.info("Initialization successful");
    }

    public static void main(String[] args){
        BasicConfigurator.configure();//Prepare logging

        Thread.setDefaultUncaughtExceptionHandler(
                (t, e) -> log.error("Uncaught exception in thread \'" + t.getName() + "\'", e));//Log uncaught exceptions


        ///TODO that's only testing
        List<WeatherDataSource> sources = new ArrayList();
        sources.add(new MeteoWaw());
        sources.add(new OpenWeatherMap("db09a595e245a0ee1640c8e9ecdaff52"));
        WeatherProxy wp = new WeatherProxy(sources);
        wp.chooseSource(1);
        wp.getStatusStream().subscribe(evt -> System.out.println(evt));
        wp.getUpdateStream().subscribe(w -> System.out.println(w.temperature+"; "+w.clouds));
        wp.manualRefreshRequest();

        PollutionDataProvider pdp = new PollutionDataProvider(new PowietrzeGiosGov());
        pdp.getStatusStream().subscribe(evt -> System.out.println(evt));
        pdp.getUpdateStream().subscribe(p -> System.out.println(p.pm10+"; "+p.pm25));
        pdp.manualRefreshRequest();

        Platform.setImplicitExit(true);//Make application exit when windows is closed
        Application.launch(AppMain.class, args);
    }
}
