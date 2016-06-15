package com.mymarket.app.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.zxing.integration.android.IntentIntegrator;
import com.mymarket.app.R;
import com.mymarket.app.activity.dialogs.AskLocationDialog;
import com.mymarket.app.model.City;
import com.mymarket.app.model.Market;
import com.mymarket.app.model.Place;
import com.mymarket.app.service.ListCitiesService;
import com.mymarket.app.service.ListMarketsService;
import com.mymarket.app.service.ListPlacesService;
import com.mymarket.app.service.ResendDataService;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import at.markushi.ui.CircleButton;

public class MainActivity extends AppCompatActivity implements LocationListener, AskLocationDialog.NoticeDialogListener {

    private final int PERMISSIONS_REQUEST_CAMERA = 112;
    private final int PERMISSIONS_REQUEST_LOCATION = 113;

    private boolean placeFoundByGPS = false;

    private Spinner citiesSpinner;
    private Spinner placesSpinner;
    private Spinner marketsSpinner;
    private int citySpinnerPosition = 0;
    private int placeSpinnerPosition = 0;
    private int marketSpinnerPosition = 0;

    private ProgressDialog citiesProgress;
    private ProgressDialog placesProgress;
    private ProgressDialog marketsProgress;

    private CircleButton reloadCitiesButton;
    private CircleButton reloadPlacesButton;
    private CircleButton reloadMarketsButton;

    private String rootURL;

    private City city;
    private Place place;
    private Market market;

    private ArrayList<City> cities;
    private ArrayList<Place> places;
    private ArrayList<Market> markets;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;

    private BroadcastReceiver citiesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                cities = (ArrayList<City>) intent.getSerializableExtra("cities");
                if (cities != null) {
                    FileOutputStream fos = context.openFileOutput("cities.list", Context.MODE_PRIVATE);
                    ObjectOutputStream os = null;
                    os = new ObjectOutputStream(fos);
                    os.writeObject(cities);
                    os.close();
                    fos.close();

                    ArrayAdapter<City> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, cities);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    citiesSpinner.setAdapter(adapter);

                    FileInputStream fileInputStream = getApplicationContext().openFileInput("city.selected");
                    ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                    city = (City) objectInputStream.readObject();
                    objectInputStream.close();
                    fileInputStream.close();
                    int position = adapter.getPosition(city);
                    citySpinnerPosition = position;

                    new LocationAsyncTask().execute(citiesSpinner);

                    reloadCitiesButton.setImageDrawable(getResources().getDrawable(R.drawable.reloadbuttondesabled));
                    reloadCitiesButton.setColor(getResources().getColor(R.color.grey_300));
                    reloadCitiesButton.setEnabled(false);
                } else {
                    FileInputStream fis = context.openFileInput("cities.list");
                    ObjectInputStream is = new ObjectInputStream(fis);
                    cities = (ArrayList<City>) is.readObject();
                    is.close();
                    fis.close();

                    ArrayAdapter<City> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, cities);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    citiesSpinner.setAdapter(adapter);

                    FileInputStream fileInputStream = getApplicationContext().openFileInput("city.selected");
                    ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                    city = (City) objectInputStream.readObject();
                    objectInputStream.close();
                    fileInputStream.close();
                    int position = adapter.getPosition(city);
                    citySpinnerPosition = position;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if (citiesProgress != null && citiesProgress.isShowing()) {
                citiesProgress.dismiss();
            }
        }
    };
    private BroadcastReceiver placesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                places = (ArrayList<Place>) intent.getSerializableExtra("places");
                if (places != null) {
                    FileOutputStream fos = context.openFileOutput("places.list", Context.MODE_PRIVATE);
                    ObjectOutputStream os = null;
                    os = new ObjectOutputStream(fos);
                    os.writeObject(places);
                    os.close();
                    fos.close();

                    ArrayAdapter<Place> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, places);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    placesSpinner.setAdapter(adapter);

                    FileInputStream fileInputStream = getApplicationContext().openFileInput("place.selected");
                    ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                    place = (Place) objectInputStream.readObject();
                    objectInputStream.close();
                    fileInputStream.close();
                    int position = adapter.getPosition(place);
                    placeSpinnerPosition = position;

                    reloadPlacesButton.setImageDrawable(getResources().getDrawable(R.drawable.reloadbuttondesabled));
                    reloadPlacesButton.setColor(getResources().getColor(R.color.grey_300));
                    reloadPlacesButton.setEnabled(false);
                } else {
                    FileInputStream fis = context.openFileInput("places.list");
                    ObjectInputStream is = new ObjectInputStream(fis);
                    places = (ArrayList<Place>) is.readObject();
                    is.close();
                    fis.close();

                    ArrayAdapter<Place> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, places);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    placesSpinner.setAdapter(adapter);

                    FileInputStream fileInputStream = getApplicationContext().openFileInput("place.selected");
                    ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                    place = (Place) objectInputStream.readObject();
                    objectInputStream.close();
                    fileInputStream.close();
                    int position = adapter.getPosition(place);
                    placeSpinnerPosition = position;

                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (placesProgress != null && placesProgress.isShowing()) {
                    placesProgress.dismiss();
                }
                if (mLastLocation != null) {
                    if (placesSpinner.getAdapter() != null) {
                        Float placeDistance = null;
                        Integer placePosition = null;
                        for (int count = 1; count < placesSpinner.getAdapter().getCount(); count++) {
                            Place place = (Place) placesSpinner.getItemAtPosition(count);
                            Location placeLocation = new Location("database");
                            placeLocation.setLatitude(Double.parseDouble(place.getLatitude()));
                            placeLocation.setLongitude(Double.parseDouble(place.getLongitude()));
                            float d = mLastLocation.distanceTo(placeLocation);
                            if (placeDistance == null || placeDistance > d) {
                                placeDistance = d;
                                placePosition = count;
                            }
                        }

                        if (placePosition != null) {
                            AskLocationDialog dialog = new AskLocationDialog();
                            dialog.setPlace(((Place) placesSpinner.getItemAtPosition(placePosition)).getName());
                           // dialog.show(getFragmentManager(), "AskLocationDialog");

                            Fragment d = dialog;
                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            transaction.add(d, "AskLocationDialog");
                            transaction.commitAllowingStateLoss();

                            placeSpinnerPosition = placePosition;
                        }
                    }
                }
            }
        }
    };
    private BroadcastReceiver marketsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                markets = (ArrayList<Market>) intent.getSerializableExtra("markets");
                if (markets != null) {
                    FileOutputStream fos = context.openFileOutput("markets.list", Context.MODE_PRIVATE);
                    ObjectOutputStream os = new ObjectOutputStream(fos);
                    os.writeObject(markets);
                    os.close();
                    fos.close();

                    ArrayAdapter<Market> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, markets);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    marketsSpinner.setAdapter(adapter);

                    reloadMarketsButton.setImageDrawable(getResources().getDrawable(R.drawable.reloadbuttondesabled));
                    reloadMarketsButton.setEnabled(false);
                    reloadMarketsButton.setColor(getResources().getColor(R.color.grey_300));
                } else {
                    FileInputStream fis = context.openFileInput("markets.list");
                    ObjectInputStream is = new ObjectInputStream(fis);
                    markets = (ArrayList<Market>) is.readObject();
                    is.close();
                    fis.close();

                    ArrayAdapter<Market> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, markets);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    marketsSpinner.setAdapter(adapter);
                }
            } catch (IOException e) {
                Toast.makeText(MainActivity.this, R.string.error_reading_stored_markets, Toast.LENGTH_SHORT).show();
            } catch (ClassNotFoundException e) {
                Toast.makeText(MainActivity.this, R.string.error_reading_stored_markets, Toast.LENGTH_SHORT).show();
            } finally {
                if (marketsProgress != null && marketsProgress.isShowing()) {
                    marketsProgress.dismiss();
                }
                if (mLastLocation != null) {
                    if (marketsSpinner.getAdapter() != null && placeFoundByGPS) {
                        Float marketDistance = null;
                        Integer marketPosition = null;
                        for (int count = 1; count < marketsSpinner.getAdapter().getCount(); count++) {
                            Market market = (Market) marketsSpinner.getItemAtPosition(count);
                            Location marketLocation = new Location("database");
                            marketLocation.setLatitude(Double.parseDouble(market.getLatitude()));
                            marketLocation.setLongitude(Double.parseDouble(market.getLongitude()));
                            float d = mLastLocation.distanceTo(marketLocation);
                            if (marketDistance == null || marketDistance > d) {
                                marketDistance = d;
                                marketPosition = count;
                            }
                        }

                        if (marketPosition != null) {
                            AskLocationDialog dialog = new AskLocationDialog();
                            dialog.setPlace(((Market) marketsSpinner.getItemAtPosition(marketPosition)).getName());
                           // dialog.show(getFragmentManager(), "AskLocationDialog");

                            Fragment d = dialog;
                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            transaction.add(d, "AskLocationDialog");
                            transaction.commitAllowingStateLoss();

                            marketSpinnerPosition = marketPosition;
                        }
                    }
                    mGoogleApiClient.disconnect();
                }
            }
        }
    };

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("city", city);
        outState.putSerializable("place", place);
        outState.putSerializable("market", market);
        outState.putSerializable("cities", cities);
        outState.putSerializable("places", places);
        outState.putSerializable("markets", markets);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            InputStream is = getBaseContext().getAssets().open("mymarket.properties");
            Properties properties = new Properties();
            properties.load(is);
            rootURL = properties.getProperty("root.url");
        } catch (IOException e) {
            e.printStackTrace();
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_main));
            getSupportActionBar().setHomeButtonEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            FrameLayout toolbarSpace = (FrameLayout) findViewById(R.id.toolbarSpace);

            LinearLayout layout = (LinearLayout) findViewById(R.id.activity_main);
            layout.removeView(toolbarSpace);
            layout.removeView(toolbar);
        }

        Intent initResendService = new Intent(MainActivity.this, ResendDataService.class);
        initResendService.putExtra("root-url", rootURL);
        startService(initResendService);


        citiesSpinner = (Spinner) findViewById(R.id.citiesSpinner);
        placesSpinner = (Spinner) findViewById(R.id.placesSpinner);
        marketsSpinner = (Spinner) findViewById(R.id.marketsSpinner);

        reloadCitiesButton = (CircleButton) findViewById(R.id.reloadCitiesButton);
        reloadPlacesButton = (CircleButton) findViewById(R.id.reloadPlacesButton);
        reloadMarketsButton = (CircleButton) findViewById(R.id.reloadMarketsButton);


        reloadCitiesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent i = new Intent(MainActivity.this, ListCitiesService.class);
                i.putExtra("root-url", rootURL);
                startService(i);
                citiesProgress = ProgressDialog.show(MainActivity.this, getResources().getString(R.string.loading), getResources().getString(R.string.cities_loading_activity_main), true, true);
                citiesProgress.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        stopService(i);
                    }
                });
            }
        });

        reloadPlacesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent i = new Intent(MainActivity.this, ListPlacesService.class);
                i.putExtra("root-url", rootURL);
                startService(i);
                placesProgress = ProgressDialog.show(MainActivity.this, getResources().getString(R.string.loading), getResources().getString(R.string.places_loading_activity_main), true, true);
                placesProgress.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        stopService(i);
                    }
                });
            }
        });

        reloadMarketsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent i = new Intent(MainActivity.this, ListMarketsService.class);
                i.putExtra("place", place);
                i.putExtra("root-url", rootURL);
                startService(i);
                marketsProgress = ProgressDialog.show(MainActivity.this, getResources().getString(R.string.loading), getResources().getString(R.string.markets_loading_activity_main), true, true);
                marketsProgress.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        stopService(i);
                    }
                });
            }
        });


        reloadPlacesButton.setEnabled(false);
        reloadMarketsButton.setEnabled(false);

        placesSpinner.setRight(View.FOCUS_RIGHT);
        if (savedInstanceState == null) {
            final Intent i = new Intent(MainActivity.this, ListCitiesService.class);
            i.putExtra("root-url", rootURL);
            startService(i);
            citiesProgress = ProgressDialog.show(MainActivity.this, getResources().getString(R.string.loading), getResources().getString(R.string.cities_loading_activity_main), true, true);
            citiesProgress.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    stopService(i);
                }
            });


            int locationPermissionCheck = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            if (locationPermissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_LOCATION);
            }

            mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                    .setFastestInterval(1 * 1000);
        } else {
            city = (City) savedInstanceState.getSerializable("city");
            place = (Place) savedInstanceState.getSerializable("place");
            market = (Market) savedInstanceState.getSerializable("market");
            cities = (ArrayList<City>) savedInstanceState.getSerializable("cities");
            places = (ArrayList<Place>) savedInstanceState.getSerializable("places");
            markets = (ArrayList<Market>) savedInstanceState.getSerializable("markets");
            if (city == null) {
                try {
                    FileInputStream fis = getApplicationContext().openFileInput("cities.list");
                    ObjectInputStream is = new ObjectInputStream(fis);
                    cities = (ArrayList<City>) is.readObject();
                    is.close();
                    fis.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (OptionalDataException e) {
                    e.printStackTrace();
                } catch (StreamCorruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            {
                try {
                    ArrayAdapter<City> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, cities);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    citiesSpinner.setAdapter(adapter);
                    citiesSpinner.setOnItemSelectedListener(null);
                    if (city != null) {
                        int position = adapter.getPosition(city);
                        citiesSpinner.setSelection(position);
                    } else {
                        FileInputStream fis = getApplicationContext().openFileInput("city.selected");
                        ObjectInputStream is = new ObjectInputStream(fis);
                        city = (City) is.readObject();
                        is.close();
                        fis.close();
                        int position = adapter.getPosition(city);
                        citiesSpinner.setSelection(position);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

            if (places == null) {
                try {
                    FileInputStream fis = getApplicationContext().openFileInput("places.list");
                    ObjectInputStream is = new ObjectInputStream(fis);
                    places = (ArrayList<Place>) is.readObject();
                    is.close();
                    fis.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (OptionalDataException e) {
                    e.printStackTrace();
                } catch (StreamCorruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            {
                try {
                    ArrayAdapter<Place> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, places);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    placesSpinner.setAdapter(adapter);
                    placesSpinner.setOnItemSelectedListener(null);
                    if (place != null) {
                        int position = adapter.getPosition(place);
                        placesSpinner.setSelection(position);
                    } else {
                        FileInputStream fis = getApplicationContext().openFileInput("place.selected");
                        ObjectInputStream is = new ObjectInputStream(fis);
                        place = (Place) is.readObject();
                        is.close();
                        fis.close();
                        int position = adapter.getPosition(place);
                        placesSpinner.setSelection(position);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }
            if (markets == null) {
                try {
                    FileInputStream fis = getApplicationContext().openFileInput("markets.list");
                    ObjectInputStream is = new ObjectInputStream(fis);
                    markets = (ArrayList<Market>) is.readObject();
                    is.close();
                    fis.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (OptionalDataException e) {
                    e.printStackTrace();
                } catch (StreamCorruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            {
                ArrayAdapter<Market> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, markets);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                marketsSpinner.setAdapter(adapter);
                int position = adapter.getPosition(market);
                marketsSpinner.setSelection(position);
            }
        }
        citiesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                city = (City) parent.getItemAtPosition(position);
                if (city.getId() == 0) {
                    placesSpinner = (Spinner) findViewById(R.id.placesSpinner);
                    placesSpinner.setAdapter(null);
                    marketsSpinner = (Spinner) findViewById(R.id.marketsSpinner);
                    marketsSpinner.setAdapter(null);
                } else {
                    try {
                        FileOutputStream fos = getApplicationContext().openFileOutput("city.selected", Context.MODE_PRIVATE);
                        ObjectOutputStream os = new ObjectOutputStream(fos);
                        os.writeObject(city);
                        os.close();
                        fos.close();

                        final Intent i = new Intent(MainActivity.this, ListPlacesService.class);
                        i.putExtra("city", city);
                        i.putExtra("root-url", rootURL);
                        startService(i);
                        placesProgress = ProgressDialog.show(MainActivity.this, getResources().getString(R.string.loading), getResources().getString(R.string.places_loading_activity_main), true, true);
                        placesProgress.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                stopService(i);
                            }
                        });
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        placesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                place = (Place) parent.getItemAtPosition(position);
                if (place.getId() == 0) {
                    marketsSpinner = (Spinner) findViewById(R.id.marketsSpinner);
                    marketsSpinner.setAdapter(null);
                } else {
                    try {
                        FileOutputStream fos = getApplicationContext().openFileOutput("place.selected", Context.MODE_PRIVATE);
                        ObjectOutputStream os = new ObjectOutputStream(fos);
                        os.writeObject(place);
                        os.close();
                        fos.close();

                        final Intent i = new Intent(MainActivity.this, ListMarketsService.class);
                        i.putExtra("place", place);
                        i.putExtra("root-url", rootURL);
                        startService(i);
                        marketsProgress = ProgressDialog.show(MainActivity.this, getResources().getString(R.string.loading), getResources().getString(R.string.markets_loading_activity_main), true, true);
                        marketsProgress.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                stopService(i);
                            }
                        });
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        marketsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                market = (Market) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button sacanButton = (Button) findViewById(R.id.imageButton);
        sacanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Place place = (Place) placesSpinner.getSelectedItem();
                if (place == null || place.getId() == 0) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.selec_place_msn_activity_main), Toast.LENGTH_SHORT).show();
                } else {
                    scan(v);
                }
            }
        });
        LocalBroadcastManager.getInstance(this).registerReceiver((citiesReceiver), new IntentFilter("CITIES"));
        LocalBroadcastManager.getInstance(this).registerReceiver((placesReceiver), new IntentFilter("PLACES"));
        LocalBroadcastManager.getInstance(this).registerReceiver((marketsReceiver), new IntentFilter("MARKETS"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    public void suggestMarket(View view) {
        Intent i = new Intent(MainActivity.this, SuggestMarketActivity.class);
        startActivity(i);
    }

    public void fakescan(View view) {
        Intent i = new Intent();
        i.putExtra("SCAN_RESULT", "66666666666");
        onActivityResult(49374, 0, i);
    }

    public void scan(View view) {
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSIONS_REQUEST_CAMERA);
        } else {
            readBarcode();
        }
    }

    private void readBarcode() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt(getResources().getString(R.string.scan_msn_activity_main));
        //integrator.setCameraId(0); // Use a specific camera of the device
        integrator.setBeepEnabled(true);
        integrator.initiateScan(IntentIntegrator.ALL_CODE_TYPES);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            if (data == null) {
                return;
            }
            ArrayList<Market> markets = (ArrayList) getAllItems(marketsSpinner);
            Place place = (Place) placesSpinner.getSelectedItem();
            Market market = (Market) marketsSpinner.getSelectedItem();
            Intent i = new Intent(MainActivity.this, ProductActivity.class);
            String barcode = data.getStringExtra("SCAN_RESULT");
            i.putExtra("barcode", barcode);
            i.putExtra("place", place);
            i.putExtra("market", market);
            i.putExtra("markets", markets);

            startActivity(i);
        }
    }

    @Override
    protected void onStart() {
/*
        mGoogleApiClient.connect();
*/
        super.onStart();
/*        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.mymarket.app.activity/http/host/path")
        );
        AppIndex.AppIndexApi.start(mGoogleApiClient, viewAction);*/
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    /*    // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.mymarket.app.activity/http/host/path")
        );
        AppIndex.AppIndexApi.end(mGoogleApiClient, viewAction);*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    readBarcode();
                } else {
                    Toast.makeText(this, R.string.some_error_occur, Toast.LENGTH_SHORT).show();
                }
                return;
            }

            case PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationRequest = LocationRequest.create()
                            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                            .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                            .setFastestInterval(1 * 1000);
                } else {
                    Toast.makeText(this, R.string.some_error_occur, Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    public List<Object> getAllItems(Spinner spinner) {
        Adapter adapter = spinner.getAdapter();
        int n = adapter.getCount();
        List<Object> items = new ArrayList<Object>();
        for (int i = 0; i < n; i++) {
            Object item = adapter.getItem(i);
            items.add(item);
        }
        return items;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLocationChanged(Location location) {

    }

/*    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        placeFoundByGPS = true;

        if (citySpinnerPosition != 0) {
            citiesSpinner.setSelection(citySpinnerPosition);
            citySpinnerPosition = 0;
        }

        if (placeSpinnerPosition != 0) {
            placesSpinner.setSelection(placeSpinnerPosition);
            placeSpinnerPosition = 0;
        }

        if (marketSpinnerPosition != 0) {
            marketsSpinner.setSelection(marketSpinnerPosition);
            marketSpinnerPosition = 0;
        }

        // marketsSpinner.setSelection(marketSpinnerPosition);
    }*/

/*    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        placeFoundByGPS = false;

    }*/

    @Override
    public void onDialogPositiveClick(android.support.v4.app.DialogFragment dialog) {
        placeFoundByGPS = true;

        if (citySpinnerPosition != 0) {
            citiesSpinner.setSelection(citySpinnerPosition);
            citySpinnerPosition = 0;
        }

        if (placeSpinnerPosition != 0) {
            placesSpinner.setSelection(placeSpinnerPosition);
            placeSpinnerPosition = 0;
        }

        if (marketSpinnerPosition != 0) {
            marketsSpinner.setSelection(marketSpinnerPosition);
            marketSpinnerPosition = 0;
        }
    }

    @Override
    public void onDialogNegativeClick(android.support.v4.app.DialogFragment dialog) {
        placeFoundByGPS = false;

    }

    public class LocationAsyncTask extends AsyncTask<Spinner, Integer, Boolean> implements
            GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

        private Spinner spinner;

        @Override
        protected Boolean doInBackground(Spinner... params) {
            spinner = params[0];
            if (mGoogleApiClient == null) {
                // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
                // See https://g.co/AppIndexing/AndroidStudio for more information.
                mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .addApi(AppIndex.API).build();
            }
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
            return true;
        }

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, MainActivity.this);
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);

            Float cityDistance = null;
            Integer cityPosition = null;

            Float placeDistance = null;
            Integer placePosition = null;

            while (spinner.getAdapter() == null) {
            }

            if (mLastLocation != null && spinner.getAdapter() != null) {
                for (int i = 1; i < spinner.getAdapter().getCount(); i++) {
                    Object item = spinner.getItemAtPosition(i);


                    if (item instanceof City) {
                        City city = (City) item;
                        Location cityLocation = new Location("database");
                        cityLocation.setLatitude(Double.parseDouble(city.getLatitude()));
                        cityLocation.setLongitude(Double.parseDouble(city.getLongitude()));
                        float d = mLastLocation.distanceTo(cityLocation);
                        if (cityDistance == null || cityDistance > d) {
                            cityDistance = d;
                            cityPosition = i;
                        }
                    }

                    if (item instanceof Place) {
                        Place place = (Place) item;
                        Location placeLocation = new Location("database");
                        placeLocation.setLatitude(Double.parseDouble(place.getLatitude()));
                        placeLocation.setLongitude(Double.parseDouble(place.getLongitude()));
                        float d = mLastLocation.distanceTo(placeLocation);
                        if (placeDistance == null || placeDistance > d) {
                            placeDistance = d;
                            placePosition = i;
                        }
                    }
                }

                if (placePosition != null) {
                    placeSpinnerPosition = placePosition;
                    AskLocationDialog dialog = new AskLocationDialog();
                    dialog.setPlace(((Place) spinner.getItemAtPosition(placePosition)).getName());
                   // dialog.show(getFragmentManager(), "AskLocationDialog");

                    Fragment d = dialog;
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.add(d, "AskLocationDialog");
                    transaction.commitAllowingStateLoss();
                }

                if (cityPosition != null) {
                    citySpinnerPosition = cityPosition;
                    AskLocationDialog dialog = new AskLocationDialog();
                    dialog.setPlace(((City) spinner.getItemAtPosition(cityPosition)).getName());
                   // dialog.show(getFragmentManager(), "AskLocationDialog");


                    Fragment d = dialog;
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.add(d, "AskLocationDialog");
                    transaction.commitAllowingStateLoss();
                }
            } else {
                if (citiesSpinner.getAdapter() != null) {
                    citiesSpinner.setSelection(citySpinnerPosition);
                }

                if (placesSpinner.getAdapter() != null) {
                    placesSpinner.setSelection(placeSpinnerPosition);
                }
            }
        }

        @Override
        public void onConnectionSuspended(int i) {
            placeFoundByGPS = false;
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }
}
