package com.example.yad.sunshine.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.CursorAdapter;

import com.example.yad.sunshine.app.data.WeatherProvider;
import com.example.yad.sunshine.app.fragment.ForecastFragment;

/**
 * Created by Emertat on 9/16/2016.
 */
public class Weather {
    FetchForecastTask fetchForecastTask;

    public Weather(){

    }

    public void fetchForecast(ForecastFragment fragment, FetchForecastTask.AsyncResponse asyncResponse){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(fragment.getContext());
        String location = preferences.getString(fragment.getString(R.string.pref_location_key), fragment.getString(R.string.pref_location_default));

        //AsyncTask can be executed only once, so create a new one on each call
        fetchForecastTask = new FetchForecastTask(fragment.getActivity(), asyncResponse);
        fetchForecastTask.execute(location);
    }

    public void registFetchForecastLoader(ForecastFragment fragment, CursorAdapter weatherCursorAdapter){
        fragment.getLoaderManager().restartLoader(
                WeatherCursorLoader.WEATHER_CURSOR_LOADER_ID,
                null,
                new WeatherCursorLoader(fragment.getContext(), weatherCursorAdapter)
                );


    }
}
