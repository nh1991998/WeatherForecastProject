package com.example.weatherforecastproject;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsoluteLayout;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.adapter.ViewPagerAdapter;
import com.example.adapter.WeatherAdapter;
import com.example.fragment.list;
import com.example.getlocation.Location;
import com.example.getlocation.ShowLocation;
import com.example.getweather.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity {

    private TextView tempTxt;
    private TextView descriptionTxt;
    private TextView windTxt;
    private TextView cloudTxt;
    private TextView humidityTxt;
    private ImageView icon;
    private AbsoluteLayout layout;

    private WeatherAdapter adapter;
    private List<Weather> list;
    private String cityName;
    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
        initUI();
        getCurrentWeatherDataByLocation();
        //GetEvery3HoursData("Bim Son");

    }

    public void initUI(){
        tempTxt = findViewById(R.id.tempTxt);
        descriptionTxt = findViewById(R.id.descriptionTxt);
        windTxt = findViewById(R.id.windTxt);
        cloudTxt = findViewById(R.id.cloudTxt);
        humidityTxt = findViewById(R.id.humidityTxt);
        icon = findViewById(R.id.icon);
        layout = findViewById(R.id.layoutWeather);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager = findViewById(R.id.viewPager);
        list = new ArrayList<>();


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menus, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {

            case R.id.action_search:
                searchByCityName();
                break;
            default:break;
        }

        return super.onOptionsItemSelected(item);
    }
    private void searchByCityName() {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle(" \uD83D\uDD0D Enter City Name");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setMaxLines(1);
        input.setSingleLine(true);
        alert.setView(input);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String result = input.getText().toString();
                if (!result.isEmpty()) {
                    fetchUpdateOnSearched(result);
                }
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Cancelled
            }
        });
        alert.show();
    }
    private void fetchUpdateOnSearched(String cityName) {
        getCurrentWeatherDataByCity(cityName);
    }
    private void requestPermission(){
        ActivityCompat.requestPermissions(this,new String[]{ACCESS_FINE_LOCATION},1);
    }


    public void setImage(String image){
        ActionBar actionBar = getSupportActionBar();
        switch (image) {
            case "01d":
            case "01n":
                icon.setImageResource(R.drawable.ic_weather_clear_sky);
                actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F3644F")));
                layout.setBackgroundColor(getResources().getColor(R.color.color_clear_and_sunny));
                break;

            case "02d":
            case "02n":
                icon.setImageResource(R.drawable.ic_weather_few_cloud);
                actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#9ABA71")));
                //toolbar.setBackgroundResource(R.color.color_partly_cloudy);
                layout.setBackgroundColor(getResources().getColor(R.color.color_partly_cloudy));
                break;

            case "03d":
            case "03n":
                icon.setImageResource(R.drawable.ic_weather_scattered_clouds);
                //toolbar.setBackgroundResource(R.color.color_gusty_winds);
                actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#8EB7EB")));
                layout.setBackgroundColor(getResources().getColor(R.color.color_gusty_winds));
                break;

            case "04d":
            case "04n":
                icon.setImageResource(R.drawable.ic_weather_broken_clouds);
                //toolbar.setBackgroundResource(R.color.color_cloudy_overnight);
                actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#856799")));
                layout.setBackgroundColor(getResources().getColor(R.color.color_cloudy_overnight));
                break;

            case "09d":
            case "09n":
                icon.setImageResource(R.drawable.ic_weather_shower_rain);
               // toolbar.setBackgroundResource(R.color.color_hail_stroms);
                actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#A46B58")));
                layout.setBackgroundColor(getResources().getColor(R.color.color_hail_stroms));
                break;

            case "10d":
            case "10n":
                icon.setImageResource(R.drawable.ic_weather_rain);
               // toolbar.setBackgroundResource(R.color.color_heavy_rain);
                actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#67BCB7")));
                layout.setBackgroundColor(getResources().getColor(R.color.color_heavy_rain));
                break;

            case "11d":
            case "11n":
                icon.setImageResource(R.drawable.ic_weather_thunderstorm);
              //  toolbar.setBackgroundResource(R.color.color_thunderstroms);
                actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#B4A699")));
                layout.setBackgroundColor(getResources().getColor(R.color.color_thunderstroms));
                break;

            case "13d":
            case "13n":
                icon.setImageResource(R.drawable.ic_weather_snow);
              //  toolbar.setBackgroundResource(R.color.color_snow);
                actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#8D9C9F")));
                layout.setBackgroundColor(getResources().getColor(R.color.color_snow));
                break;

            case "15d":
            case "15n":
                icon.setImageResource(R.drawable.ic_weather_mist);
               // toolbar.setBackgroundResource(R.color.color_mix_snow_and_rain);
                actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#D870A5")));
                layout.setBackgroundColor(getResources().getColor(R.color.color_mix_snow_and_rain));
                break;

        }
    }
    private String getTime(Long milliTime) {
        Date currentDate = new Date(milliTime);
        SimpleDateFormat df = new SimpleDateFormat("E, dd.MM.yyyy - hh:mm a");
        String date = df.format(currentDate);
        return date;
    }

    public void getCurrentWeatherDataByLocation(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        ShowLocation showLocation = new ShowLocation(this);
        Location location = showLocation.getLocation();
        final Weather weather = new Weather();
        String url = "https://api.openweathermap.org/data/2.5/weather?lat="+ location.getLatitude().intValue()+ "&lon="+ location.getLongitude().intValue() +"&units=metric&appid=e2ba10b74ca98acdbfcebc388c21fabf";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            ActionBar actionBar = getSupportActionBar();
                            cityName = jsonObject.getString("name");
                            String country = jsonObject.getJSONObject("sys").getString("country");
                            actionBar.setTitle(cityName + ", " + country);

                            JSONArray jsonArrayWeather = jsonObject.getJSONArray("weather");
                            JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                            descriptionTxt.setText(jsonObjectWeather.getString("main"));

                            setImage(jsonObjectWeather.getString("icon"));

                            JSONObject jsonObjectMain = jsonObject.getJSONObject("main");
                            tempTxt.setText(jsonObjectMain.getInt("temp") +" °C");
                            humidityTxt.setText("Humidity: " + jsonObjectMain.getString("humidity") +"%");

                            JSONObject jsonObjectWind = jsonObject.getJSONObject("wind");
                            windTxt.setText("Wind: " + jsonObjectWind.getString("speed") +"m/s");

                            JSONObject jsonObjectClouds = jsonObject.getJSONObject("clouds");
                            cloudTxt.setText("Cloud: " + jsonObjectClouds.getString("all")+"%");

                            GetEvery3HoursData(cityName);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(stringRequest);
    }

    public void getCurrentWeatherDataByCity(String city){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        final Weather weather = new Weather();
        String url = "https://api.openweathermap.org/data/2.5/weather?q="+ city+"&units=metric&appid=e2ba10b74ca98acdbfcebc388c21fabf";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            ActionBar actionBar = getSupportActionBar();
                            cityName = jsonObject.getString("name");
                            String country = jsonObject.getJSONObject("sys"). getString("country");
                            actionBar.setTitle(cityName + ", " + country);

                            JSONArray jsonArrayWeather = jsonObject.getJSONArray("weather");
                            JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                            descriptionTxt.setText(jsonObjectWeather.getString("main"));

                            setImage(jsonObjectWeather.getString("icon"));

                            JSONObject jsonObjectMain = jsonObject.getJSONObject("main");
                            tempTxt.setText(jsonObjectMain.getInt("temp") +" °C");
                            humidityTxt.setText("Humidity: " + jsonObjectMain.getString("humidity") +"%");

                            JSONObject jsonObjectWind = jsonObject.getJSONObject("wind");
                            windTxt.setText("Wind: " + jsonObjectWind.getString("speed") +" m/s");

                            JSONObject jsonObjectClouds = jsonObject.getJSONObject("clouds");
                            cloudTxt.setText("Cloud: " + jsonObjectClouds.getString("all")+"%");

                            GetEvery3HoursData(cityName);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(stringRequest);
    }
    private List<Weather> GetEvery3HoursData(String city) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "https://api.openweathermap.org/data/2.5/forecast?q="+city+"&units=metric&appid=e2ba10b74ca98acdbfcebc388c21fabf";
        final List<Weather> list = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    Weather weather;
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonObjectCity = jsonObject.getJSONObject("city");
                    String name = jsonObjectCity.getString("name");

                    JSONArray jsonArray = jsonObject.getJSONArray("list");
                    for(int i=0;i<jsonArray.length();i++){
                        weather = new Weather();
                        weather.setName(name);
                        JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                        String date = jsonObjectList.getString("dt_txt");
//                        if(!date.contains("00:00:00")){
//                            continue;
//                        }
                        weather.setDate(date);
                        //weather.setDate(getTime(Long.parseLong(jsonObjectList.getString("dt"))));


                        JSONObject jsonObjectTemp = jsonObjectList.getJSONObject("main");
                        weather.setTemp_max(jsonObjectTemp.getInt("temp_max")+"");
                        weather.setTemp_min(jsonObjectTemp.getInt("temp_min")+"");
                        weather.setHumidity(jsonObjectTemp.getString("humidity"));

                        JSONObject jsonObjectWind = jsonObjectList.getJSONObject("wind");
                        weather.setWind(jsonObjectWind.getString("speed"));

                        JSONObject jsonObjectClouds = jsonObjectList.getJSONObject("clouds");
                        weather.setClouds(jsonObjectClouds.getString("all"));


                        JSONArray jsonArrayWeather = jsonObjectList.getJSONArray("weather");
                        JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                        weather.setStatus(jsonObjectWeather.getString("description"));
                        weather.setIcon(jsonObjectWeather.getString("icon"));
                        list.add(weather);
                    }
                    addTabs(viewPager,list);
                    //addTabs(viewPager,list);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        } );
        requestQueue.add(stringRequest);
        return list;
    }
    private void addTabs(ViewPager viewPager,List<Weather> list) {
        ArrayList<Weather> al_list = new ArrayList<>(list.size());
        al_list.addAll(list);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        ArrayList<Weather> temp_list = new ArrayList<>();
        Fragment fragment;
        String temp = "";

        for(Weather weather : al_list){
            if(!getDate(weather.getDate()).equals(temp)){
                if(!temp_list.isEmpty()){
                    fragment = new list();

                    Bundle args = new Bundle();
                    args.putSerializable("list",temp_list);
                    fragment.setArguments(args);
                    adapter.addFrag(fragment, "ONE");
                }
                temp = getDate(weather.getDate());
                temp_list = new ArrayList<>();
                temp_list.add(weather);
            }else{
                temp_list.add(weather);
            }
        }
        viewPager.setAdapter(adapter);
    }

    public String getDate(String text){
        return text.split(" ")[0];
    }
}
