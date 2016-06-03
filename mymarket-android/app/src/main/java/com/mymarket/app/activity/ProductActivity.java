package com.mymarket.app.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import java.text.NumberFormat;
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
    private Button suggestProductNameButton;
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
        if (toolbar != null) {
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
        suggestProductNameButton = (Button) findViewById(R.id.suggestProductNameButton);
        buttonAction = (Button) findViewById(R.id.button_action);
        buttonConfirm = (Button) findViewById(R.id.button_confirm);
        frameLayout = (FrameLayout) findViewById(R.id.productFrame);
        TextView marketFrameLastUpdate = (TextView) findViewById(R.id.marketFrameLastUpdate);
        TextView marketFrameLastUpdateLabel = (TextView) findViewById(R.id.marketFrameLastUpdateLabel);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
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
                if (product != null && product.getName() != null) {
                    productName = product.getName();
                    textView1.setText(productName);
                    suggestProductNameButton.setVisibility(View.VISIBLE);
                    suggestProductNameButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                            builder.setTitle(getResources().getString(R.string.product_name_suggestion_label));

                            // Set up the input
                            final EditText input = new EditText(getApplicationContext());
                            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
                            builder.setView(input);

                            // Set up the buttons
                            builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String suggestionProductName = input.getText().toString();
                                }
                            });
                            builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            builder.show();
                        }
                    });
                } else {
                    productName = "";
                    textView1.setText(getResources().getString(R.string.not_found_activity_product));
                    suggestProductNameButton.setVisibility(View.INVISIBLE);
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
                        if (productName != "") {
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
                map.put("info", market.getName() + " - " + NumberFormat.getCurrencyInstance().format(result.getPrice()));
                map.put("date", getResources().getString(R.string.last_update_activity_product) + ": " + sdf.format(lastUpdate));

                if (selectedMarked != null && selectedMarked.getName().equals(market.getName())) {
                    marketFound = true;
                    frameLayout.setBackgroundColor(getResources().getColor(R.color.green_100));

                    TextView marketFrameTitle = (TextView) findViewById(R.id.marketFrameTitle);
                    marketFrameTitle.setText(market.getName());

                    TextView marketFramePrice = (TextView) findViewById(R.id.marketFramePrice);
                    marketFramePrice.setText(NumberFormat.getCurrencyInstance().format(result.getPrice()));

                    marketFrameLastUpdate.setVisibility(View.VISIBLE);
                    marketFrameLastUpdateLabel.setVisibility(View.VISIBLE);
                    marketFrameLastUpdate.setText(sdf.format(lastUpdate));
                } else {
                    list.add(map);
                }
            }

            BaseAdapter adapter = new SimpleAdapter(this, list,
                    android.R.layout.simple_list_item_2,
                    new String[]{"info", "date"},
                    new int[]{android.R.id.text1, android.R.id.text2}) {

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    HashMap<String, String> result = (HashMap<String, String>) getItem(position);
                    View view = super.getView(position, convertView, parent);
                    RelativeLayout twoLineListItem = (RelativeLayout) view;

                    TextView text1 = (TextView) twoLineListItem.findViewById(android.R.id.text1);
                    RelativeLayout.LayoutParams layoutParamsText1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    text1.setLayoutParams(layoutParamsText1);
                    text1.setTextColor(getResources().getColor(R.color.black));

                    TextView text2 = (TextView) twoLineListItem.findViewById(android.R.id.text2);
                    text2.setTextColor(getResources().getColor(R.color.grey_700));

                    twoLineListItem.setBackgroundColor(getResources().getColor(R.color.blue_100));
                    return twoLineListItem;
                }
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
            frameLayout.setVisibility(View.VISIBLE);

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
                marketFrameLastUpdate.setVisibility(View.GONE);
                marketFrameLastUpdateLabel.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
