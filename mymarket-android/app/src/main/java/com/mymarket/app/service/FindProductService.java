package com.mymarket.app.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.mymarket.app.model.Market;
import com.mymarket.app.model.Place;
import com.mymarket.app.model.Product;
import com.mymarket.app.model.Search;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class FindProductService extends IntentService {

    public FindProductService(){
        super("FindProductService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            //colocar timeout
            String barcode = intent.getStringExtra("barcode");
            Place place = (Place) intent.getSerializableExtra("place");
            URL url = new URL(intent.getStringExtra("root-url") + "/rest/product/get?barcode=" + barcode);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Accept", "application/json");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String response = "";
            String line = null;
            while ((line = in.readLine()) != null) {
                response += line;
            }
            in.close();

            Product productResult = new Product();
            if (!response.isEmpty()) {
                JSONObject json = new JSONObject(response);
                if(json.has("name")) {
                    productResult.setName(json.getString("name"));
                }

                if(json.has("barcode")) {
                    productResult.setBarcode(json.getString("barcode"));
                }
            }

            ArrayList<Market> markets = (ArrayList<Market>) intent.getSerializableExtra("markets");
            Market market = (Market) intent.getSerializableExtra("market");

            Intent i = new Intent("PRODUCTS");
            i.putExtra("productResult", productResult);
            i.putExtra("checkProducts", false);
            i.putExtra("barcode", barcode);
            i.putExtra("markets", markets);
            i.putExtra("place", place);
            i.putExtra("market", market);

            LocalBroadcastManager.getInstance(this).sendBroadcast(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
