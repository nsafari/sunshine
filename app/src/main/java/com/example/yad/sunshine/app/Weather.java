package com.example.yad.sunshine.app;

/**
 * Created by Emertat on 9/16/2016.
 */
public class Weather {
    FetchForecastTask fetchForecastTask;

    public Weather(){

    }

    public void fetchForecast(FetchForecastTask.AsyncResponse asyncResponse){
        //AsyncTask can be executed only once, so create a new one on each call
        fetchForecastTask = new FetchForecastTask(asyncResponse);
        fetchForecastTask.execute("112931");
    }
}
