package com.c15.jujaweather;

import android.accounts.NetworkErrorException;
import android.app.usage.NetworkStatsManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.NetPermission;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    private File file;              //To save data on device
    private Gson gson = new Gson();        //Parser for Json to WeatherData
    private Reader reader = null;
    boolean updated;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        file = new File(getFilesDir()+"/sdata.json");
        if (!file.exists()) try {
             updated = false;
             file.createNewFile();      //first run, and in case app data is cleared
            InputStream inputStream = getResources().openRawResource(R.raw.sdata);
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buf = new byte[512];
            while (true) {
                int len = inputStream.read(buf);
                if (len == -1) {
                    break;
                }
                outputStream.write(buf, 0, len);
            }

            inputStream.close();
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Thread(new Runnable() {     //Network request should not be within main thread
            @Override
            public void run() {
                updated = updateWeather();    //get data and write to file {sdata.json}
            }
        }).start();       //run new thread

        try {
            reader = new FileReader(file);
            displayData();
        } catch (FileNotFoundException e) {
            Toast t = Toast.makeText(this, "File not found!", Toast.LENGTH_SHORT);
            t.setGravity(Gravity.TOP,0,0);
            t.show();
            e.printStackTrace();
        }

    }

    public void displayData(){
        //JsonReader jsonReader = new JsonReader(reader);
        // = new WeatherData();
        WeatherData weatherData = gson.fromJson(reader, WeatherData.class);
        //Only one city specified in URL, cityIndex always 0
        int cityIndex = 0;
        //ImageView weatherImage = findViewById(R.id.weather_image);
        TextView temp_current_value = findViewById(R.id.temperatureText);
        TextView temperatureUnitTxt = findViewById(R.id.temperature_main_unit);
        TextView descriptionTxt = findViewById(R.id.weather_description);
        TextView humidityTxt = findViewById(R.id.humidity_value);
        TextView airPressureTxt= findViewById(R.id.air_pressure_value);
        TextView visibilityTxt = findViewById(R.id.visibility_value);
        TextView tempMinTxt= findViewById(R.id.temp_min_value);
        TextView tempMaxTxt = findViewById(R.id.temp_max_value);
        TextView windSpeedTxt = findViewById(R.id.wind_speed_value);
        TextView windAngleTxt = findViewById(R.id.wind_angle_value);
        TextView cloudCoverTxt = findViewById(R.id.cloud_cover_value);
String val; float f;//To hold values before bing displayed

        val = String.valueOf(weatherData.main.temp - 273.15f);
        temp_current_value.setText(String.valueOf(val));
        descriptionTxt.setText(weatherData.weather[cityIndex].description);
        val = weatherData.main.humidity + "%";
        humidityTxt.setText(val);
        val = weatherData.main.pressure + " mb";
        airPressureTxt.setText(val);
        val = weatherData.visibility + " m";
        visibilityTxt.setText(val);
        val = (weatherData.main.temp_min - 273.15f) + "\u00B0C\t";
        tempMinTxt.setText(val);
        val = (weatherData.main.temp_max - 273.15f) + "\u00B0C";
        tempMaxTxt.setText(val);
        val = weatherData.wind.speed + " kph\t";
        windSpeedTxt.setText(val);
        val = weatherData.wind.deg + "\u00B0";
        windAngleTxt.setText(val);
        val = weatherData.clouds.all + "%";
        cloudCoverTxt.setText(val);
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

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_refresh:{
                /*new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (updateWeather())
                        displayData();
                    }
                }).start();*/
                Toast.makeText(this, "Very very problematic part\nnot implemented yet", Toast.LENGTH_SHORT).show();
                return true;
            }
            case R.id.action_exit:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean updateWeather() {

        try {
            URL url = new URL(getString(R.string.data_url));
            URLConnection connection = url.openConnection();
            InputStream in = connection.getInputStream();
            FileOutputStream outputStream = new FileOutputStream(file);

            byte[] buf = new byte[512];
            while (true) {
                int len = in.read(buf);
                if (len == -1) {
                    break;
                }
                outputStream.write(buf, 0, len);
            }

            in.close();
            outputStream.flush();
            outputStream.close();
            return true;
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }

    }
}
