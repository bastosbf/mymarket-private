package com.mymarket.app.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.mymarket.app.model.Market;
import com.mymarket.app.model.ProductRecord;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class SuggestNameService extends IntentService {

    public SuggestNameService() {
        super("SuggestNameService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String barcode = intent.getStringExtra("barcode");
        String name = intent.getStringExtra("name");

        try {
            URL url = new URL(intent.getStringExtra("root-url") + "/rest/collaboration/suggest-name?product=" + barcode + "&name=" + name);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Accept", "application/json");
            InputStream is = connection.getInputStream();
            is.close();

            Intent i = new Intent("SUGGEST_NAME");
            LocalBroadcastManager.getInstance(this).sendBroadcast(i);
        } catch (Exception e) {

        }
    }
}
