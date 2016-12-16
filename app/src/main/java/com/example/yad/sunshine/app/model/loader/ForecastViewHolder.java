package com.example.yad.sunshine.app.model.loader;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yad.sunshine.app.R;

/**
 * Created by Emertat on 12/16/2016.
 */
public class ForecastViewHolder {

    public ImageView iconView;
    public TextView dateView;
    public TextView descriptionView;
    public TextView highView;
    public TextView lowView;

    public ForecastViewHolder(View view){
        iconView = (ImageView) view.findViewById(R.id.list_item_icon);
        dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
        descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
        highView = (TextView) view.findViewById(R.id.list_item_high_textview);
        lowView = (TextView) view.findViewById(R.id.list_item_low_textview);
    }
}
