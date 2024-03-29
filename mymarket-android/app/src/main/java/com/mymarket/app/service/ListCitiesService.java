package com.mymarket.app.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.mymarket.app.R;
import com.mymarket.app.model.City;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ListCitiesService extends IntentService {


    private boolean connectionError = false;
    private int secondsToWait = 0;
    private int connectionAttempts = 0;

    public ListCitiesService() {
        super("ListCitiesService");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Thread.sleep(secondsToWait * 1000);

            if (connectionAttempts > 3) {
                Intent i = new Intent("CITIES");
                LocalBroadcastManager.getInstance(this).sendBroadcast(i);
                connectionError = true;
                return;
            }

            final URL url = new URL(intent.getStringExtra("root-url") + "/rest/city/list");

            HttpURLConnection connection = null;
            ExecutorService executor = Executors.newCachedThreadPool();
            Callable<Object> task = new Callable<Object>() {
                public Object call() throws IOException {
                    return url.openConnection();
                }
            };
            Future<Object> future = executor.submit(task);
            connection = (HttpURLConnection) future.get(5, TimeUnit.SECONDS);

            connection.setRequestProperty("Accept", "application/json");


            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String response = "";
            String line = null;
            while ((line = in.readLine()) != null) {
                response += line;
            }
            in.close();
            Intent intentReturn = new Intent("CITIES");
            if ((connection.getResponseCode() == 200) && (!response.isEmpty())) {
                ArrayList<City> values = new ArrayList<>();
                //add select message
                {
                    City c = new City();
                    c.setId(0);
                    c.setName(getResources().getString(R.string.select_city_service_list_cities));
                    values.add(c);
                }
                JSONArray cities = new JSONArray(response);
                int length = cities.length();
                for (int i = 0; i < length; i++) {
                    JSONObject city = cities.getJSONObject(i);
                    int id = city.getInt("id");
                    String name = city.getString("name");
                    String latitude = city.getString("latitude");
                    String longitude = city.getString("longitude");

                    City c = new City();
                    c.setId(id);
                    c.setName(name);
                    c.setLatitude(latitude);
                    c.setLongitude(longitude);

                    values.add(c);
                }
                intentReturn.putExtra("cities", values);
            } else {
                Toast.makeText(this, R.string.some_error_occur, Toast.LENGTH_LONG);
            }
            LocalBroadcastManager.getInstance(this).sendBroadcast(intentReturn);
        } catch (TimeoutException e) {
            connectionAttempts++;
            secondsToWait = 2;
            onHandleIntent(intent);
        } catch (Exception e) {
            connectionAttempts++;
            secondsToWait = 2;
            onHandleIntent(intent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (connectionError) {
            Toast.makeText(getApplicationContext(), R.string.error_connect_using_local_data, Toast.LENGTH_SHORT).show();
        }
    }
}
