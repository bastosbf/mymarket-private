package com.mymarket.app.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
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
import android.widget.Spinner;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.mymarket.app.R;
import com.mymarket.app.model.Market;
import com.mymarket.app.model.Place;
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

public class MainActivity extends AppCompatActivity {

    private Spinner placesSpinner;
    private Spinner marketsSpinner;
    private ProgressDialog placesProgress;
    private ProgressDialog marketsProgress;
    private Button sacanButton;
    private Button reloadPlacesButton;
    private Button reloadMarketsButton;
    private String rootURL;

    private Place place;
    private Market market;
    private ArrayList<Place> places;
    private ArrayList<Market> markets;


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
                    placesSpinner.setSelection(position);

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
                    placesSpinner.setSelection(position);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if (placesProgress != null && placesProgress.isShowing()) {
                placesProgress.dismiss();
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

                    reloadMarketsButton.setEnabled(false);
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
            }
        }
    };

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("place", place);
        outState.putSerializable("market", market);
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
        if(toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_main));
            getSupportActionBar().setHomeButtonEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }


        Intent initResendService = new Intent(MainActivity.this, ResendDataService.class);
        initResendService.putExtra("root-url", rootURL);
        startService(initResendService);


        placesSpinner = (Spinner) findViewById(R.id.placesSpinner);
        marketsSpinner = (Spinner) findViewById(R.id.marketsSpinner);
        sacanButton = (Button) findViewById(R.id.imageButton);
        reloadPlacesButton = (Button) findViewById(R.id.reloadPlacesButton);
        reloadMarketsButton = (Button) findViewById(R.id.reloadMarketsButton);

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


        //reloadPlacesButton.setEnabled(false);
        //reloadMarketsButton.setEnabled(false);

        placesSpinner.setRight(View.FOCUS_RIGHT);
        if (savedInstanceState == null) {
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
        } else {
            place = (Place) savedInstanceState.getSerializable("place");
            market = (Market) savedInstanceState.getSerializable("market");
            places = (ArrayList<Place>) savedInstanceState.getSerializable("places");
            markets = (ArrayList<Market>) savedInstanceState.getSerializable("markets");
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
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt(getResources().getString(R.string.scan_msn_activity_main));
        integrator.setCameraId(0); // Use a specific camera of the device
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
}
