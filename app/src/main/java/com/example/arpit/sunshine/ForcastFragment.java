package com.example.arpit.sunshine;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForcastFragment extends Fragment {

    public ForcastFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        String[] weatherdata = {"today 23 sunny", "friday 25 rain ", "sat 29 foggy,",
                "sun 31 sunny", "mon 21 foggy", "tue 33 rain", "wed 34 rsin", "today 23 sunny"
                , "today 23 sunny", "today 23 sunny", "today 23 sunny", "today 23 sunny"};
        List<String> weatherDataLisr = new ArrayList<>(Arrays.asList(weatherdata));
        ArrayAdapter<String> weatherAdaptor = new ArrayAdapter<String>(getActivity(), R.layout.list_item_forcast
                , R.id.list_item_forcast_textview, weatherDataLisr);
        ListView listView = (ListView) rootView.findViewById(R.id.listviewforcast);
        listView.setAdapter(weatherAdaptor);
        // These two need to be declared outside the try/catch
// so that they can be closed in the finally block.

        return rootView;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater )
    {
        inflater.inflate(R.menu.forcastfragment, menu) ;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            FetchWeatherTask weatherTask = new FetchWeatherTask();
            weatherTask.execute() ;
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

class FetchWeatherTask extends AsyncTask<Void,Void,Void>
{   private final String LOG_TAG = FetchWeatherTask.class.getSimpleName() ;
    @Override
    protected Void doInBackground(Void... voids) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String forecastJsonStr = null;

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7" +
                    "&APPID=c8527db1d17dc61b30545cdeb9361eba");

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null ;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
               return null ;
            }
            forecastJsonStr = buffer.toString();
            Log.v(LOG_TAG,"forecastJsonStr is"+forecastJsonStr) ;
        } catch (IOException e) {
            Log.e("PlaceholderFragment", "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            forecastJsonStr = null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }
        return null;
    }
}
