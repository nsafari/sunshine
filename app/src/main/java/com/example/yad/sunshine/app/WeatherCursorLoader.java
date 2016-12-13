package com.example.yad.sunshine.app;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;

import com.example.yad.sunshine.app.data.WeatherContract;

/**
 * Created by Emertat on 11/30/2016.
 */
public class WeatherCursorLoader implements LoaderManager.LoaderCallbacks<Cursor> {
    private final Context mContext;
    private final CursorAdapter weatherCursorAdapter;

    public static final int WEATHER_CURSOR_LOADER_ID = 1001;

    public WeatherCursorLoader(Context context, CursorAdapter cursorAdapter) {
        mContext = context;
        weatherCursorAdapter = cursorAdapter;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";

        String locationSetting = Utility.getPreferredLocation(mContext);
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                locationSetting, System.currentTimeMillis());

        return new CursorLoader(
                mContext,
                weatherForLocationUri,
                null, null, null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        weatherCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        weatherCursorAdapter.swapCursor(null);
    }
}
