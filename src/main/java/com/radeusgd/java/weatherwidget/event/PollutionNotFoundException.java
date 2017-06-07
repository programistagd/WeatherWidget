package com.radeusgd.java.weatherwidget.event;

/**
 * Created by Programistagd on 05.06.2017.
 */
public class PollutionNotFoundException extends Exception {
    public PollutionNotFoundException(Throwable cause){
        super("There was an error fetching pollution data", cause);
    }
}
