package com.example.yad.sunshine.app.model.loader;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yad.sunshine.app.R;
import com.example.yad.sunshine.app.Utility;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {

    private final int VIEW_TYPE_TODAY = 0;
    private final int VIEW_TYPE_FUTURE_DAY = 1;

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /**
     * Prepare the weather high/lows for presentation.
     */
    private static String formatHighLows(Context mContext, double high, double low, boolean isMetric) {
        String highLowStr = Utility.formatTemperature(mContext, high, isMetric) + "/" + Utility.formatTemperature(mContext, low, isMetric);
        return highLowStr;
    }

    /*
        This is ported from FetchWeatherTask --- but now we go straight from the cursor to the
        string.
     */
    public static String ConvertCursorRowToUXFormat(Context mContext, Cursor cursor, boolean isMetric) {
        String highAndLow = formatHighLows(
                mContext,
                cursor.getDouble(WeatherCursorLoader.COL_WEATHER_MAX_TEMP),
                cursor.getDouble(WeatherCursorLoader.COL_WEATHER_MIN_TEMP),
                isMetric);

        return Utility.formatDate(cursor.getLong(WeatherCursorLoader.COL_WEATHER_DATE)) +
                " - " + cursor.getString(WeatherCursorLoader.COL_WEATHER_DESC) +
                " - " + highAndLow;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    /*
            Remember that these views are reused as needed.
         */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;

        if(viewType == VIEW_TYPE_TODAY){
            layoutId = R.layout.list_item_forecast_today;
        }else if(viewType == VIEW_TYPE_FUTURE_DAY){
            layoutId = R.layout.list_item_forecast;
        }

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        view.setTag(new ForecastViewHolder(view));
        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ForecastViewHolder forecastViewHolder = (ForecastViewHolder) view.getTag();

        // Read weather icon ID from cursor
        int weatherId = cursor.getInt(WeatherCursorLoader.COL_WEATHER_CONDITION_ID);
        int viewType = getItemViewType(cursor.getPosition());
        switch (viewType) {
            case VIEW_TYPE_TODAY: {
                // Get weather icon
                forecastViewHolder.iconView.setImageResource(Utility.getArtResourceForWeatherCondition(
                        weatherId));
                break;
            }
            case VIEW_TYPE_FUTURE_DAY: {
                // Get weather icon
                forecastViewHolder.iconView.setImageResource(Utility.getIconResourceForWeatherCondition(
                        weatherId));
                break;
            }
        }

        // Read date from cursor
        long dateInMillis = cursor.getLong(WeatherCursorLoader.COL_WEATHER_DATE);
        forecastViewHolder.dateView.setText(Utility.getFriendlyDayString(context, dateInMillis));

        // Read weather forecast from cursor
        String description = cursor.getString(WeatherCursorLoader.COL_WEATHER_DESC);
        forecastViewHolder.descriptionView.setText(description);

        // Read user preference for metric or imperial temperature units
        boolean isMetric = Utility.isMetric(context);

        // Read high temperature from cursor
        double high = cursor.getDouble(WeatherCursorLoader.COL_WEATHER_MAX_TEMP);
        forecastViewHolder.highView.setText(Utility.formatTemperature(mContext, high, isMetric));

        // Read low temperature from cursor
        double low = cursor.getDouble(WeatherCursorLoader.COL_WEATHER_MIN_TEMP);
        forecastViewHolder.lowView.setText(Utility.formatTemperature(mContext, low, isMetric));
    }
}