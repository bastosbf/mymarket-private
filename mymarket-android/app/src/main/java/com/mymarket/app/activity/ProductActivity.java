package com.mymarket.app.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.mymarket.app.R;
import com.mymarket.app.model.Market;
import com.mymarket.app.model.Place;
import com.mymarket.app.model.Product;
import com.mymarket.app.model.Search;
import com.mymarket.app.service.ConfirmPriceService;
import com.mymarket.app.service.FindPricesService;
import com.mymarket.app.service.FindProductService;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ProductActivity extends AppCompatActivity {

    private FrameLayout frameLayout;
    private TextView textView1;
    private TextView textView2;
    private ListView listView;
    private Button buttonAction;
    private Button buttonConfirm;
    private ProgressDialog progress;
    private String rootURL;

    private BroadcastReceiver productsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            progress.dismiss();
            receiveList(intent);
        }
    };

    private BroadcastReceiver confirmReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            progress.dismiss();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_product));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        try {
            InputStream is = getBaseContext().getAssets().open("mymarket.properties");
            Properties properties = new Properties();
            properties.load(is);
            rootURL = properties.getProperty("root.url");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent intent = getIntent();
        final Intent i = new Intent(ProductActivity.this, FindPricesService.class);
        i.putExtra("barcode", intent.getStringExtra("barcode"));
        i.putExtra("place", intent.getSerializableExtra("place"));
        i.putExtra("market", intent.getSerializableExtra("market"));
        i.putExtra("markets", intent.getSerializableExtra("markets"));
        i.putExtra("root-url", rootURL);
        startService(i);
        progress = ProgressDialog.show(ProductActivity.this, getResources().getString(R.string.loading), getResources().getString(R.string.products_loading_activity_product), true, true);
        /*progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                stopService(i);
            }
        });*/
        progress.setCancelable(false);
        LocalBroadcastManager.getInstance(this).registerReceiver((productsReceiver), new IntentFilter("PRODUCTS"));
        LocalBroadcastManager.getInstance(this).registerReceiver((confirmReceiver), new IntentFilter("CONFIRM_PRICE"));
        frameLayout = (FrameLayout) findViewById(R.id.productFrame);
        frameLayout.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_product, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up buttonAction, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    protected void receiveList(final Intent intent) {
        setContentView(R.layout.activity_product);
        textView1 = (TextView) findViewById(R.id.textView1);
        textView2 = (TextView) findViewById(R.id.textView2);
        listView = (ListView) findViewById(R.id.listView);
        buttonAction = (Button) findViewById(R.id.button_action);
        buttonConfirm = (Button) findViewById(R.id.button_confirm);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_product));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        final ArrayList<Search> results = (ArrayList<Search>) intent.getSerializableExtra("results");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        if (results == null || results.isEmpty()) {
            if (intent.getBooleanExtra("checkProducts", true)) {
                Intent i = new Intent(ProductActivity.this, FindProductService.class);
                i.putExtra("barcode", intent.getStringExtra("barcode"));
                i.putExtra("place", intent.getSerializableExtra("place"));
                i.putExtra("market", intent.getSerializableExtra("market"));
                i.putExtra("markets", intent.getSerializableExtra("markets"));
                i.putExtra("root-url", rootURL);
                startService(i);
            } else {
                Product product = (Product) intent.getSerializableExtra("productResult");
                final String productName;
                if(product != null && product.getName() != null){
                    productName = product.getName();
                    textView1.setText(productName);
                }else {
                    productName = "";
                    textView1.setText(getResources().getString(R.string.not_found_activity_product));
                }
                String barcode = intent.getStringExtra("barcode");
                textView2.setText(barcode);
                buttonConfirm.setVisibility(View.INVISIBLE);
                buttonAction.setText(getResources().getString(R.string.add_activity_product));
                buttonAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String barcode = String.valueOf(textView2.getText());
                        ArrayList<Market> markets = (ArrayList<Market>) intent.getSerializableExtra("markets");
                        Place place = (Place) intent.getSerializableExtra("place");
                        Market market = (Market) intent.getSerializableExtra("market");

                        Intent i = new Intent(ProductActivity.this, SuggestProductActivity.class);
                        i.putExtra("barcode", barcode);
                        if(productName != "") {
                            i.putExtra("productName", productName);
                        }
                        i.putExtra("place", place);
                        i.putExtra("operation", "suggest-product");
                        i.putExtra("market", market);
                        i.putExtra("markets", markets);

                        startActivity(i);
                    }
                });
                Market selectedMarked = (Market) intent.getSerializableExtra("market");
                TextView marketFrameTitle = (TextView) findViewById(R.id.marketFrameTitle);
                marketFrameTitle.setText(selectedMarked.getName());
            }
        } else {
            List<Map<String, Object>> list = new ArrayList<>();
            Product product = null;
            Market selectedMarked = (Market) intent.getSerializableExtra("market");
            boolean marketFound = false;

            Collections.sort(results, new Comparator<Search>() {
                @Override
                public int compare(Search search1, Search search2) {
                    return search1.getPrice().compareTo(search2.getPrice());
                }
            });

            for (Search result : results) {
                if (product == null) {
                    product = result.getProduct();
                    textView1.setText(product.getName());
                    textView2.setText(product.getBarcode());
                }
                Market market = result.getMarket();
                Date lastUpdate = result.getLastUpdate();
                Map<String, Object> map = new HashMap<>();
                map.put("info", market.getName() + " - " + getResources().getString(R.string.currency_activity_product) + " " + result.getPrice());
                map.put("date", getResources().getString(R.string.last_update_activity_product) + ": " + sdf.format(lastUpdate));

                if (selectedMarked != null && selectedMarked.getName().equals(market.getName())) {
                    marketFound = true;

                    frameLayout = (FrameLayout) findViewById(R.id.productFrame);
                    frameLayout.setBackgroundColor(getResources().getColor(R.color.green_100));

                    TextView marketFrameTitle = (TextView) findViewById(R.id.marketFrameTitle);
                    marketFrameTitle.setText(market.getName());

                    TextView marketFramePrice = (TextView) findViewById(R.id.marketFramePrice);
                    marketFramePrice.setText(getResources().getString(R.string.currency_activity_product) + " " + result.getPrice());
                } else {
                    list.add(map);
                }
            }

            BaseAdapter adapter = new SimpleAdapter(this, list,
                    android.R.layout.simple_list_item_2,
                    new String[]{"info", "date"},
                    new int[]{android.R.id.text1, android.R.id.text2}) {
            };
            listView.setDivider(new ColorDrawable(this.getResources().getColor(R.color.white)));
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int height = size.y;
            listView.setDividerHeight(height / 35);
            listView.setBackgroundColor(getResources().getColor(R.color.white));
            listView.setAdapter(adapter);
            buttonAction.setText(getResources().getString(R.string.update_activity_product));
            final String productName = product.getName();
            buttonAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String barcode = String.valueOf(textView2.getText());
                    ArrayList<Market> markets = (ArrayList<Market>) intent.getSerializableExtra("markets");
                    Place place = (Place) intent.getSerializableExtra("place");
                    Market market = (Market) intent.getSerializableExtra("market");

                    Intent i = new Intent(ProductActivity.this, SuggestProductActivity.class);
                    i.putExtra("barcode", barcode);
                    i.putExtra("productName", productName);
                    i.putExtra("operation", "suggest-price");
                    i.putExtra("place", place);
                    i.putExtra("market", market);
                    i.putExtra("markets", markets);

                    startActivity(i);
                }
            });
            buttonConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progress.setMessage("Sending...");
                    progress.setCancelable(false);
                    progress.show();

                    Intent iConfirmProgress = new Intent("CONFIRM_PROGRESS");
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(iConfirmProgress);

                    String barcode = String.valueOf(textView2.getText());
                    ArrayList<Market> markets = (ArrayList<Market>) intent.getSerializableExtra("markets");
                    Place place = (Place) intent.getSerializableExtra("place");
                    Market market = (Market) intent.getSerializableExtra("market");

                    Intent i = new Intent(ProductActivity.this, ConfirmPriceService.class);
                    i.putExtra("barcode", barcode);
                    i.putExtra("market", market);
                    i.putExtra("root-url", rootURL);
                    i.putExtra("productName", productName);
                    i.putExtra("place", place);

                    i.putExtra("markets", markets);

                    startService(i);
                    buttonConfirm.setEnabled(false);
                }
            });
            Market market = (Market) intent.getSerializableExtra("market");
            if (market.getId() == 0) {
                LinearLayout swipe = (LinearLayout) findViewById(R.id.swipe);
                FrameLayout frameLayout = (FrameLayout) findViewById(R.id.productFrame);
                swipe.removeView(frameLayout);

                buttonAction.setVisibility(View.INVISIBLE);
                buttonConfirm.setVisibility(View.INVISIBLE);
            }

            if (!marketFound && market.getId() != 0) {
                TextView marketFrameTitle = (TextView) findViewById(R.id.marketFrameTitle);
                marketFrameTitle.setText(market.getName());

                //Toast.makeText(this, "Product not found in the market '" + market.getName() + "'", Toast.LENGTH_LONG).show();
                buttonAction.setText(getResources().getString(R.string.add_activity_product));
                buttonAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String barcode = String.valueOf(textView2.getText());
                        ArrayList<Market> markets = (ArrayList<Market>) intent.getSerializableExtra("markets");
                        Place place = (Place) intent.getSerializableExtra("place");
                        Market market = (Market) intent.getSerializableExtra("market");

                        Intent i = new Intent(ProductActivity.this, SuggestProductActivity.class);
                        i.putExtra("barcode", barcode);
                        i.putExtra("place", place);
                        i.putExtra("operation", "suggest-product");
                        i.putExtra("productName", productName);
                        i.putExtra("market", market);
                        i.putExtra("markets", markets);

                        startActivity(i);
                    }
                });
                buttonConfirm.setVisibility(View.INVISIBLE);
            }
        }
        frameLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
