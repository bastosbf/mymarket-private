package com.mymarket.app.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.mymarket.app.model.ProductRecord;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by egaldino on 26/04/16.
 */
public class ResendDataService extends IntentService {

    List<ProductRecord> productRecords;

    private int secondsToWait = 0;


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public ResendDataService() {
        super("ResendDataService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Thread.sleep(secondsToWait * 1000);

            FileInputStream fis = getApplicationContext().openFileInput("productRecords.resend.list");
            ObjectInputStream objectInputStream = new ObjectInputStream(fis);
            productRecords = (ArrayList<ProductRecord>) objectInputStream.readObject();
            objectInputStream.close();
            fis.close();

            if (productRecords != null && !productRecords.isEmpty()) {
                for (ProductRecord productRecord : productRecords) {
                    URL url;
                    if (productRecord.getName() != null) {
                        url = new URL(intent.getStringExtra("root-url") + "/rest/collaboration/suggest-product?market=" + productRecord.getMarket().getId() + "&barcode=" + productRecord.getBarcode() + "&name=" + productRecord.getName() + "&price=" + productRecord.getPrice());
                    } else {
                        url = new URL(intent.getStringExtra("root-url") + "/rest/collaboration/suggest-price?market=" + productRecord.getMarket().getId() + "&product=" + productRecord.getBarcode() + "&price=" + productRecord.getPrice());
                    }
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestProperty("Accept", "application/json");
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String response = "";
                    String line = null;
                    while ((line = in.readLine()) != null) {
                        response += line;
                    }
                    in.close();
                    int code = connection.getResponseCode();
                    if (code == 200 || code == 204) {
                        productRecords.remove(productRecord);
                        FileOutputStream fos = getApplicationContext().openFileOutput("productRecords.resend.list", Context.MODE_PRIVATE);
                        ObjectOutputStream os = new ObjectOutputStream(fos);
                        os.writeObject(productRecords);
                        os.close();
                        fos.close();
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (OptionalDataException e) {
            e.printStackTrace();
        } catch (Exception e) {
            switch (secondsToWait) {
                case 0:
                    secondsToWait = 5;
                    break;
                case 5:
                    secondsToWait = 10;
                    break;
                case 10:
                    secondsToWait = 60;
                    break;
            }
            onHandleIntent(intent);
        }
    }
}
