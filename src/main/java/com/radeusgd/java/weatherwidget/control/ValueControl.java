package com.radeusgd.java.weatherwidget.control;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import rx.Observable;

/**
 * Created by Radek on 01.06.2017.
 */
public class ValueControl extends Pane {
    private StringProperty prefixProperty = new SimpleStringProperty();

    private ObjectProperty<Observable<String>> sourceProperty = new SimpleObjectProperty<>();
    private HBox innerContainer;
    private Text prefixLabel;
    private Text textControl;

    public Observable<String> getSource() {
        return sourceProperty.get();
    }

    public void setSource(Observable<String> source) {
        source.subscribe(e -> {
            if (innerContainer == null) {
                createContentControls();
            }

            textControl.setText(e);
        });

        sourceProperty.set(source);
    }

    private void createContentControls() {
        textControl = new Text();
        textControl.getStyleClass().add("rate-value");

        prefixLabel = new Text();
        prefixLabel.textProperty().bind(prefixProperty);
        prefixLabel.getStyleClass().add("helper-label");

        innerContainer = new HBox();
        innerContainer.getStyleClass().add("value-container");
        innerContainer.getChildren().addAll(prefixLabel, textControl);

        getChildren().add(innerContainer);
    }

    public String getPrefix() {
        return prefixProperty.get();
    }

    public void setPrefix(String prefix) {
        prefixProperty.set(prefix);
    }
}
