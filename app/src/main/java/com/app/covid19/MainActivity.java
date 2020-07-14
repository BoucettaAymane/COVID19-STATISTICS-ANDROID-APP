package com.app.covid19;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.app.covid19.ConnectivityUtil.isOnline;

public class MainActivity extends AppCompatActivity {

    private TextView txtTotalRec, txtNewRec, txtPrRec, txtTotalDeath, txtNewDeath, txtDateCurrent,
            txtPrDeath, txtTotalConf, txtNewConf, txtPrConf, txtWordRec, txtWordDeath, txtWordConfirm;

    private String API_URL = "https://api.covid19api.com/summary";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtTotalRec = findViewById(R.id.txtTotalRecovered);
        txtNewRec = findViewById(R.id.txtNewRecovered);
        txtPrRec = findViewById(R.id.txtPrRecovered);
        txtTotalDeath = findViewById(R.id.txtTotalDeath);
        txtNewDeath = findViewById(R.id.txtNewDeath);
        txtPrDeath = findViewById(R.id.txtPrDeath);
        txtTotalConf = findViewById(R.id.txtTotalConfirmed);
        txtNewConf = findViewById(R.id.txtNewConfirmed);
        txtPrConf = findViewById(R.id.txtPrConfirmed);

        txtWordRec = findViewById(R.id.txtWordRecover);
        txtWordDeath = findViewById(R.id.txtWordDeath);
        txtWordConfirm = findViewById(R.id.txtWordConfirmed);

        txtDateCurrent = findViewById(R.id.currentDate);

        new GetTodayData().execute();

    }

    public class GetTodayData extends AsyncTask<String, String, String> {

        //Cache
        File httpCacheDirectory = new File(getBaseContext().getCacheDir(), "http-cache");
        int cacheSize = 10 * 1024 * 1024; // 10 MB
        Cache cache = new Cache(httpCacheDirectory, cacheSize);

        //OkHttpClient
        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new ConnectivityUtil.CacheInterceptor())
                .cache(cache)
                .build();

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... strings) {

            if (isOnline(MainActivity.this)) {
                Request request = new Request.Builder()
                        .url(API_URL)
                        .cacheControl(CacheControl.FORCE_NETWORK)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    return response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                Request request = new Request.Builder()
                        .url(API_URL)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    return response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            try {
                JSONObject res = new JSONObject(response);

                JSONObject global = res.getJSONObject("Global");

                String PrRec = "", PrDeath = "", PrConf = "";

                txtWordConfirm.setText(global.getString("TotalConfirmed"));
                txtWordDeath.setText(global.getString("TotalDeaths"));
                txtWordRec.setText(global.getString("TotalRecovered"));

                String currentDate = res.getString("Date");
                currentDate = currentDate.replace("T", " ");
                currentDate = currentDate.replace("Z", " ");

                txtDateCurrent.setText(currentDate);

                JSONArray countries = res.getJSONArray("Countries");
                for (int i = 0; i < countries.length(); i++) {
                    JSONObject object = countries.getJSONObject(i);
                    if (object.getString("CountryCode").equals("MA")) {

                        PrRec = pourcentage(Double.parseDouble(object.getString("TotalRecovered")), Double.parseDouble(object.getString("TotalConfirmed")));
                        PrDeath = pourcentage(Double.parseDouble(object.getString("TotalDeaths")), Double.parseDouble(object.getString("TotalConfirmed")));
                        PrConf = pourcentage(Double.parseDouble(object.getString("NewConfirmed")), Double.parseDouble(object.getString("TotalConfirmed")));

                        txtTotalRec.setText(object.getString("TotalRecovered"));
                        txtNewRec.setText(object.getString("NewRecovered"));

                        txtTotalDeath.setText(object.getString("TotalDeaths"));
                        txtNewDeath.setText(object.getString("NewDeaths"));

                        txtTotalConf.setText(object.getString("TotalConfirmed"));
                        txtNewConf.setText(object.getString("NewConfirmed"));
                    }
                }

                txtPrRec.setText(PrRec);
                txtPrDeath.setText(PrDeath);
                txtPrConf.setText(PrConf);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private String pourcentage(Double t1, Double t2) {
        Double res = round((t1 / t2) * 100, 2);
        return res.toString() + " %";
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }


}