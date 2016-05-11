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
public class SuggestPriceService extends IntentService {

    public SuggestPriceService() {
        super("SuggestPriceService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Market market = (Market) intent.getSerializableExtra("market");
        String barcode = intent.getStringExtra("barcode");
        String price = intent.getStringExtra("price");

        try {
            URL url = new URL(intent.getStringExtra("root-url") + "/rest/collaboration/suggest-price?market=" + market.getId() + "&product=" + barcode + "&price=" + price);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Accept", "application/json");
            InputStream is = connection.getInputStream();
            is.close();

            Intent i = new Intent("SUGGEST_PRICE");
            LocalBroadcastManager.getInstance(this).sendBroadcast(i);
        } catch (Exception e) {
            try {
                e.printStackTrace();
                List<ProductRecord> productRecords = null;
                try {
                    FileInputStream fis = getApplicationContext().openFileInput("productRecords.resend.list");
                    ObjectInputStream objectInputStream = new ObjectInputStream(fis);
                    productRecords = (ArrayList<ProductRecord>) objectInputStream.readObject();
                    objectInputStream.close();
                    fis.close();
                } catch (FileNotFoundException g) {
                }

                if (productRecords == null) {
                    productRecords = new ArrayList<>();
                }

                ProductRecord productRecord = new ProductRecord();
                productRecord.setBarcode(barcode);
                productRecord.setMarket(market);
                productRecord.setPrice(price);

                productRecords.add(productRecord);

                FileOutputStream fos = getApplicationContext().openFileOutput("productRecords.resend.list", Context.MODE_PRIVATE);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                os.writeObject(productRecords);
                os.close();
                fos.close();

                Intent i = new Intent(SuggestPriceService.this, ResendDataService.class);
                i.putExtra("root-url", intent.getStringExtra("root-url"));
                startService(i);

                Intent intentReturn = new Intent("SUGGEST_PRODUCT");
                LocalBroadcastManager.getInstance(this).sendBroadcast(intentReturn);
            } catch (FileNotFoundException f) {
                f.printStackTrace();
            } catch (StreamCorruptedException f) {
                f.printStackTrace();
            } catch (ClassNotFoundException f) {
                f.printStackTrace();
            } catch (OptionalDataException f) {
                f.printStackTrace();
            } catch (IOException f) {
                f.printStackTrace();
            }
        }
    }
}
