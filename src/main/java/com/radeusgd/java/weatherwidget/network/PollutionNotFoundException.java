package com.radeusgd.java.weatherwidget.network;

public class PollutionNotFoundException extends Exception {
    public PollutionNotFoundException(Throwable cause){
        super("There was an error fetching pollution data", cause);
    }
}
