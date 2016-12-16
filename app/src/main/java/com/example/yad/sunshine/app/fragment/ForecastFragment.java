package com.example.yad.sunshine.app.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.yad.sunshine.app.model.loader.ForecastAdapter;
import com.example.yad.sunshine.app.Utility;
import com.example.yad.sunshine.app.model.loader.WeatherCursorLoader;
import com.example.yad.sunshine.app.activity.DetailActivity;
import com.example.yad.sunshine.app.model.loader.FetchForecastTask;
import com.example.yad.sunshine.app.R;
import com.example.yad.sunshine.app.model.Weather;
import com.example.yad.sunshine.app.activity.SettingsActivity;
import com.example.yad.sunshine.app.data.WeatherContract;

public class ForecastFragment extends Fragment {
    private final String TAG = ForecastFragment.class.getSimpleName();

    private CursorAdapter weatherAdapter;

    private OnFragmentInteractionListener mListener;
    private Weather weather;

    public ForecastFragment() {
        // Required empty public constructor
        weather = new Weather();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        weather.registerFetchForecastLoader(this, weatherAdapter);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_forecast_list, container, false);

        String locationSetting = Utility.getPreferredLocation(getActivity());

        // Sort order:  Ascending, by date.
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                locationSetting, System.currentTimeMillis());

        Cursor cur = getActivity().getContentResolver().query(
                weatherForLocationUri,
                WeatherCursorLoader.FORECAST_COLUMNS,
                null, null,
                sortOrder);

        // The CursorAdapter will take data from our cursor and populate the ListView
        // However, we cannot use FLAG_AUTO_REQUERY since it is deprecated, so we will end
        // up with an empty list the first time we run.
        weatherAdapter = new ForecastAdapter(getActivity(), cur, 0);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (cursor != null) {
                    String locationSetting = Utility.getPreferredLocation(getActivity());
                    Intent intent = new Intent(getActivity(), DetailActivity.class)
                            .setData(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                                    locationSetting, cursor.getLong(WeatherCursorLoader.COL_WEATHER_DATE)
                            ));
                    startActivity(intent);
                }
            }
        });
        listView.setAdapter(weatherAdapter);
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forcast_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                Log.d(TAG, "onOptionsItemSelected: refresh selected");
                updateWeather();
                return true;
            case R.id.action_settings:
                Intent settingIntent = new Intent(this.getContext(), SettingsActivity.class);
                startActivity(settingIntent);
                return true;
            case R.id.action_map:
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
                String location = preferences.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
                opnLocationOnMap(location);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void opnLocationOnMap(String location) {
        Uri geoLocation = Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q", location)
                .build();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if (intent.resolveActivity(this.getContext().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(TAG, "Could not show map");
        }
    }

    //Deprecated cursorAdapter notified when cursorLoader changed
    private void updateWeather() {
        weather.fetchForecast(this, new FetchForecastTask.AsyncResponse() {
            @Override
            public void processFinished(String[] result) {
                if (result == null) {
                    return;
                }
//                stringArrayAdapter.clear();
//                stringArrayAdapter.addAll(result);
            }
        });
    }


    public void onButtonClick(View view) {
        Log.i("", "This is a test");
    }
}

