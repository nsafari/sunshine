package com.example.yad.sunshine.app;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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
        String location = preferences.getString(fragment.getString(R.string.pref_location_key), fragment.getString(R.string.prf_location_default));

        //AsyncTask can be executed only once, so create a new one on each call
        fetchForecastTask = new FetchForecastTask(fragment.getActivity(), asyncResponse);
        fetchForecastTask.execute(location);
    }
}
