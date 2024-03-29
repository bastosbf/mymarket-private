package com.mymarket.app.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mymarket.app.R;
import com.mymarket.app.service.SuggestMarketService;
import com.mymarket.app.utils.URLUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SuggestMarketActivity extends AppCompatActivity {

    private EditText editText1;
    private EditText editText2;
    private EditText editText3;
    private ProgressDialog progress;
    private String rootURL;

    private BroadcastReceiver suggestionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            sendMessage();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggest_market);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_suggest_market));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationIcon(null);
        }


        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            FrameLayout toolbarSpace = (FrameLayout) findViewById(R.id.toolbarSpace);

            LinearLayout layout = (LinearLayout) findViewById(R.id.activity_main);
            layout.removeView(toolbarSpace);
            layout.removeView(toolbar);
        }

        try {
            InputStream is = getBaseContext().getAssets().open("mymarket.properties");
            Properties properties = new Properties();
            properties.load(is);
            rootURL = properties.getProperty("root.url");
        } catch (IOException e) {
            e.printStackTrace();
        }
        LocalBroadcastManager.getInstance(this).registerReceiver((suggestionReceiver), new IntentFilter("SUGGEST_MARKET"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_market, menu);
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
        editText1 = (EditText) findViewById(R.id.editText1);
        editText2 = (EditText) findViewById(R.id.editText2);
        editText3 = (EditText) findViewById(R.id.editText3);

        String name = URLUtils.removeEmptySpaces(String.valueOf(editText1.getText()));
        String place = URLUtils.removeEmptySpaces(String.valueOf(editText2.getText()));
        String city = URLUtils.removeEmptySpaces(String.valueOf(editText3.getText()));

        if(name.isEmpty()) {
            Toast.makeText(SuggestMarketActivity.this, getResources().getString(R.string.empty_name_activity_suggest_market), Toast.LENGTH_LONG).show();
            return;
        }

        if(place.isEmpty()) {
            Toast.makeText(SuggestMarketActivity.this, getResources().getString(R.string.empty_place_activity_suggest_market), Toast.LENGTH_LONG).show();
            return;
        }

        if(city.isEmpty()) {
            Toast.makeText(SuggestMarketActivity.this, getResources().getString(R.string.empty_city_activity_suggest_market), Toast.LENGTH_LONG).show();
            return;
        }

        final Intent i = new Intent(SuggestMarketActivity.this, SuggestMarketService.class);
        i.putExtra("marketName", name);
        i.putExtra("marketPlace", place);
        i.putExtra("marketCity", city);
        i.putExtra("root-url", rootURL);

        progress = ProgressDialog.show(SuggestMarketActivity.this, getResources().getString(R.string.loading), getResources().getString(R.string.suggest_loading_activity_suggest_market), true, true);
        progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                stopService(i);
            }
        });

        startService(i);
    }

    private void sendMessage() {
        progress.dismiss();
        Toast.makeText(SuggestMarketActivity.this, getResources().getString(R.string.msn_service_suggest_market), Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
