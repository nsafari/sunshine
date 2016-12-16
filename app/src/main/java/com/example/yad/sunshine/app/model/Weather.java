package com.example.yad.sunshine.app.model;

import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;

import com.example.yad.sunshine.app.model.loader.DetailForecastViewHolder;
import com.example.yad.sunshine.app.model.loader.FetchForecastTask;
import com.example.yad.sunshine.app.R;
import com.example.yad.sunshine.app.model.loader.WeatherCursorLoader;
import com.example.yad.sunshine.app.model.loader.WeatherDetailCursorLoader;

/**
 * Created by Emertat on 9/16/2016.
 */
public class Weather {
    FetchForecastTask fetchForecastTask;

    public Weather(){

    }

    public void fetchForecast(Fragment fragment, FetchForecastTask.AsyncResponse asyncResponse){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(fragment.getContext());
        String location = preferences.getString(fragment.getString(R.string.pref_location_key), fragment.getString(R.string.pref_location_default));

        //AsyncTask can be executed only once, so create a new one on each call
        fetchForecastTask = new FetchForecastTask(fragment.getActivity(), asyncResponse);
        fetchForecastTask.execute(location);
    }

    public void registerFetchForecastLoader(Fragment fragment, CursorAdapter weatherCursorAdapter){
        fragment.getLoaderManager().restartLoader(
                WeatherCursorLoader.WEATHER_CURSOR_LOADER_ID,
                null,
                new WeatherCursorLoader(fragment.getContext(), weatherCursorAdapter)
                );
    }

    public void registerDetailFetchForecastLoader(Fragment fragment, Uri data, DetailForecastViewHolder viewHolder){
        fragment.getLoaderManager().restartLoader(
                WeatherDetailCursorLoader.WEATHER_DETAIL_CURSOR_LOADER_ID,
                null,
                new WeatherDetailCursorLoader(fragment.getContext(), data, viewHolder)
                );
    }
}
