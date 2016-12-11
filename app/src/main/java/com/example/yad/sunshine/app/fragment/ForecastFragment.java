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
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.yad.sunshine.app.ForecastAdapter;
import com.example.yad.sunshine.app.Utility;
import com.example.yad.sunshine.app.activity.DetailActivity;
import com.example.yad.sunshine.app.FetchForecastTask;
import com.example.yad.sunshine.app.R;
import com.example.yad.sunshine.app.Weather;
import com.example.yad.sunshine.app.activity.SettingsActivity;
import com.example.yad.sunshine.app.data.WeatherContract;

import java.util.ArrayList;
import java.util.Arrays;

public class ForecastFragment extends Fragment {
    private final String TAG = ForecastFragment.class.getSimpleName();

    private ArrayAdapter<String> stringArrayAdapter;
    private CursorAdapter weatherAdapter;

    private OnFragmentInteractionListener mListener;
    private Weather weather;

    public ForecastFragment() {
        // Required empty public constructor
        weather = new Weather();
        stringArrayAdapter = null;
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String[] data = {
                "Mon 6/23 - Sunny - 31/17",
                "Tue 6/24 - Foggy - 21/8",
                "Wed 6/25 - Cloudy - 22/17",
                "Thurs 6/26 - Rainy - 18/11",
                "Fri 6/27 - Foggy - 21/10",
                "Sat 6/28 - TRAPPED IN WEATHERSTATION - 23/18",
                "Sun 6/29 - Sunny - 20/7"
        };
        stringArrayAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.item_forecast,
                R.id.list_item_forecast_textview,
                new ArrayList(Arrays.asList(data)));
        ListView listView = (ListView) view.findViewById(R.id.listview_forecast);
        listView.setAdapter(stringArrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String forecast = stringArrayAdapter.getItem(position);
                Intent intent = new Intent(getContext(), DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, forecast);
                startActivity(intent);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String locationSetting = Utility.getPreferredLocation(getActivity());

        // Sort order:  Ascending, by date.
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                locationSetting, System.currentTimeMillis());

        Cursor cur = getActivity().getContentResolver().query(weatherForLocationUri,
                null, null, null, sortOrder);

        // The CursorAdapter will take data from our cursor and populate the ListView
        // However, we cannot use FLAG_AUTO_REQUERY since it is deprecated, so we will end
        // up with an empty list the first time we run.
        weatherAdapter = new ForecastAdapter(getActivity(), cur, 0);

        View rootView = inflater.inflate(R.layout.fragment_forecast_list, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(weatherAdapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        weather.registFetchForecastLoader(this, weatherAdapter);

        super.onActivityCreated(savedInstanceState);
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

        if(intent.resolveActivity(this.getContext().getPackageManager()) != null){
            startActivity(intent);
        }
        else{
            Log.d(TAG, "Could not show map");
        }
    }

    private void updateWeather() {
        weather.fetchForecast(this, new FetchForecastTask.AsyncResponse() {
            @Override
            public void processFinished(String[] result) {
                if (result == null) {
                    return;
                }
                stringArrayAdapter.clear();
                stringArrayAdapter.addAll(result);
            }
        });
    }


    public void onButtonClick(View view) {
        Log.i("", "This is a test");
    }
}

