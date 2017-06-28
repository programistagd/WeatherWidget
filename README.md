A simple JavaFX app that fetches weather and pollution data for Warsaw.

![screenshot](https://raw.githubusercontent.com/programistagd/WeatherWidget/master/weatherwidget.png "Screenshot")

Overview of data flow in the app:
 - DataSource fetches data and reports it onto its stream
 - DataProvider does the book-keeping of request status (started, finished/failed) and a particular implementation splits the data from D

DataSource stream into separate streams for each of values provided

ValueControl displays values from these streams
