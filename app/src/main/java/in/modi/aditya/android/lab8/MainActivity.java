package in.modi.aditya.android.lab8;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    private String city = null;
    private final String PREFERENCES = "META";
    private final String apiKey = "399ae8a478baa65313edf04851b19076";
    private final String urlTemplate = "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s";

    RelativeLayout activityBackgroundRelativeLayout = null;
    ImageView weatherTypeImageView = null;
    TextView citconTextView = null, temperatureTextView = null, weatherTypeTextView = null, humidityTextView = null, pressureTextView = null, windSpeedTextView = null, minTemperatureTextView = null, maxTemperatureTextView = null;

    private String getStringResourceByName(String parameter) {
        int resId = getResources().getIdentifier(parameter, "string", getPackageName());
        return getString(resId);
    }

    private String[] parseParametersFromJson(JSONObject jsonData) throws JSONException {
        String parameters[] = new String[9];
        parameters[0] = "" + jsonData.getString("name") + ", " + jsonData.getJSONObject("sys").getString("country");
        parameters[1] = "" + jsonData.getJSONArray("weather").getJSONObject(0).getInt("id");
        parameters[2] = "" + Math.round((jsonData.getJSONObject("main").getDouble("temp") - 273.15) * 100D) / 100D;
        parameters[3] = "" + jsonData.getJSONObject("main").getDouble("humidity") + "%";
        parameters[4] = "" + Math.round((jsonData.getJSONObject("main").getDouble("temp_min") - 273.15) * 100D) / 100D;
        parameters[5] = "" + jsonData.getJSONObject("main").getDouble("pressure");
        parameters[6] = "" + Math.round((jsonData.getJSONObject("main").getDouble("temp_max") - 273.15) * 100D) / 100D;
        parameters[7] = "" + jsonData.getJSONObject("wind").getDouble("speed");
        parameters[8] = "" + jsonData.getJSONArray("weather").getJSONObject(0).getString("description");
        return parameters;
    }

    private String[] callOpenWeather(String city) {
        String parameters[] = null;
        try {
            InputStream inputStream = null;
            String finalURL = String.format(urlTemplate, city, apiKey);

            HttpURLConnection connection = (HttpURLConnection) new URL(finalURL).openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            StringBuffer rawData = new StringBuffer();
            inputStream = connection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while ((line = br.readLine()) != null)
                rawData.append(line + "\n");

            inputStream.close();
            connection.disconnect();
            JSONObject jsonData = new JSONObject(new String(rawData));

            parameters = parseParametersFromJson(jsonData);
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "City not found!\nTry another City?", Toast.LENGTH_SHORT).show();
        } catch (UnknownHostException e) {
            Toast.makeText(this, "Cannot Connect to the Server!\nTry again Later?", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Unexpected error occurred!!\nTry again Later?", Toast.LENGTH_SHORT).show();
        } finally {
            return parameters;
        }
    }

    private void setParameters(String parameters[]) {
        if (parameters == null) {
            citconTextView.setText("Data N/A");
            temperatureTextView.setText("Data N/A");
            weatherTypeTextView.setText("Data N/A");
            humidityTextView.setText("Data N/A");
            pressureTextView.setText("Data N/A");
            windSpeedTextView.setText("Data N/A");
            minTemperatureTextView.setText("Data N/A");
            maxTemperatureTextView.setText("Data N/A");
        }
        int weatherCode = Integer.parseInt(parameters[1]);
        if (weatherCode >= 200 && weatherCode < 300) {
            activityBackgroundRelativeLayout.setBackgroundResource(R.drawable.thunderstorm_back);
            weatherTypeImageView.setImageResource(R.drawable.thunderstorm_icon);
            parameters[1] = "Thunderstorm";
        } else if (weatherCode >= 300 && weatherCode < 400) {
            activityBackgroundRelativeLayout.setBackgroundResource(R.drawable.drizzle_back);
            weatherTypeImageView.setImageResource(R.drawable.drizzle_icon);
            parameters[1] = "Drizzle";
        } else if (weatherCode >= 500 && weatherCode < 600) {
            activityBackgroundRelativeLayout.setBackgroundResource(R.drawable.rainy_back);
            weatherTypeImageView.setImageResource(R.drawable.rainy_icon);
            parameters[1] = "Rain";
        } else if (weatherCode >= 600 && weatherCode < 700) {
            activityBackgroundRelativeLayout.setBackgroundResource(R.drawable.snowy_back);
            weatherTypeImageView.setImageResource(R.drawable.snowy_icon);
            parameters[1] = "Snow";
        } else if (weatherCode >= 700 && weatherCode < 800) {
            activityBackgroundRelativeLayout.setBackgroundResource(R.drawable.atmosphere_back);
            weatherTypeImageView.setImageResource(R.drawable.atmosphere_icon);
            parameters[1] = parameters[8];
        } else if (weatherCode == 800) {
            activityBackgroundRelativeLayout.setBackgroundResource(R.drawable.clear_sky_back);
            weatherTypeImageView.setImageResource(R.drawable.clear_sky_icon);
            parameters[1] = "Clear Sky";
        } else if (weatherCode >= 800 && weatherCode < 900) {
            activityBackgroundRelativeLayout.setBackgroundResource(R.drawable.cloudy_back);
            weatherTypeImageView.setImageResource(R.drawable.cloudy_icon);
            parameters[1] = "Clouds";
        }

        citconTextView.setText(String.format(getStringResourceByName("main_city"), parameters[0]));
        weatherTypeTextView.setText(String.format(getStringResourceByName("main_weatherType"), parameters[1]));
        temperatureTextView.setText(String.format(getStringResourceByName("main_temperature"), parameters[2]));
        humidityTextView.setText(String.format(getStringResourceByName("main_humudity"), parameters[3]));
        minTemperatureTextView.setText(String.format(getStringResourceByName("main_minTemperature"), parameters[4]));
        pressureTextView.setText(String.format(getStringResourceByName("main_pressure"), parameters[5]));
        maxTemperatureTextView.setText(String.format(getStringResourceByName("main_maxTemperature"), parameters[6]));
        windSpeedTextView.setText(String.format(getStringResourceByName("main_windSpeed"), parameters[7]));

        Toast.makeText(this, "Updated!", Toast.LENGTH_SHORT).show();
    }

    public void changeCity() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter New City");
        builder.setIcon(R.drawable.change_city_icon);

        final EditText inputCityEditText = new EditText(this);
        builder.setView(inputCityEditText);

        builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                city = inputCityEditText.getText().toString().toLowerCase();

                String parameters[] = callOpenWeather(city);
                if (parameters != null) {
                    setParameters(parameters);
                    SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("city", city);
                    editor.commit();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.changeCity:
                changeCity();
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_main);

        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String parameters[] = callOpenWeather(city);
                if (parameters != null) {
                    setParameters(parameters);
                }
                pullToRefresh.setRefreshing(false);
            }
        });

        try {
            SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
            city = sharedPreferences.getString("city", "surat");
        } catch (Exception e) {
            city = "surat";
        }

        /*https://stackoverflow.com/questions/6343166/how-do-i-fix-android-os-networkonmainthreadexception*/
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        activityBackgroundRelativeLayout = (RelativeLayout) findViewById(R.id.activityBackgroundRelativeLayout);
        activityBackgroundRelativeLayout.setBackgroundResource(R.drawable.drizzle_back);

        citconTextView = (TextView) findViewById(R.id.citconTextView);
        weatherTypeImageView = (ImageView) findViewById(R.id.weatherTypeImageView);
        weatherTypeTextView = (TextView) findViewById(R.id.weatherTypeTextView);
        temperatureTextView = (TextView) findViewById(R.id.temperatureTextView);
        humidityTextView = (TextView) findViewById(R.id.humidityTextView);
        pressureTextView = (TextView) findViewById(R.id.pressureTextView);
        windSpeedTextView = (TextView) findViewById(R.id.windSpeedTextView);
        minTemperatureTextView = (TextView) findViewById(R.id.minTemperatureTextView);
        maxTemperatureTextView = (TextView) findViewById(R.id.maxTemperatureTextView);

        String parameters[] = callOpenWeather(city);
        if (parameters != null) {
            setParameters(parameters);
        }
    }
}
