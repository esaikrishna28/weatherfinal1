package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weather.airmodel.AirResponse;
import com.example.weather.airmodel.AirService;
import com.example.weather.weatherforecastmodel.NextDaysService;
import com.example.weather.weatherforecastmodel.WeatherForecastResponse;
import com.example.weather.weathermodel.CurrentWeatherResponse;
import com.example.weather.weathermodel.WeatherService;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private EditText txtSearch;
    private ImageView ivSearch;
    private ImageView ivStatus;
    private TextView lblTemperature;
    private TextView lblStatus;
    private TextView lblHumidity;
    private TextView lblCloud;
    private TextView lblWind;
    private TextView lblPressure;
    private RecyclerView rvNextDays;
    private GraphView lcTemperature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Map();
        InitializeChart();
        FetchCurrentWeatherFromApi();
        FetchWeatherForecastFromApi();

        ivSearch.setOnClickListener(v ->{
            FetchCurrentWeatherFromApi();
            FetchWeatherForecastFromApi();
        });
    }

    private void Map() {
        txtSearch = findViewById(R.id.txt_search);
        ivSearch = findViewById(R.id.iv_search);
        ivStatus = findViewById(R.id.iv_status);
        lblTemperature = findViewById(R.id.lbl_temperature);
        lblStatus = findViewById(R.id.lbl_status);
        lblHumidity = findViewById(R.id.lbl_humidity);
        lblCloud = findViewById(R.id.lbl_cloud);
        lblWind = findViewById(R.id.lbl_wind);
        lblPressure = findViewById(R.id.lbl_pressure);
        rvNextDays = findViewById(R.id.rv_next_days);
        lcTemperature = findViewById(R.id.lc_temperature);
    }

    private void InitializeChart() {
        lcTemperature.setTitleColor(getColor(R.color.white));
        lcTemperature.getGridLabelRenderer().setGridColor(getColor(R.color.white));
        lcTemperature.getGridLabelRenderer().setHorizontalLabelsColor(getColor(R.color.white));
        lcTemperature.getGridLabelRenderer().setVerticalLabelsColor(getColor(R.color.white));
        lcTemperature.getGridLabelRenderer().setHighlightZeroLines(false);
    }

    private void CallApi() {
        AirService.airService.AirInformation(50, 50, "e52cee364ac0886a6d8878e7fbd3e679")
                .enqueue(new Callback<AirResponse>() {
                    @Override
                    public void onResponse(Call<AirResponse> call, Response<AirResponse> response) {
                        Toast.makeText(MainActivity.this, response.body().toString(), Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFailure(Call<AirResponse> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void FetchCurrentWeatherFromApi() {

        WeatherService.weatherService.CurrentWeatherInformation(txtSearch.getText().toString(), "metric", "e52cee364ac0886a6d8878e7fbd3e679")
                .enqueue(new Callback<CurrentWeatherResponse>() {
                    @Override
                    public void onResponse(Call<CurrentWeatherResponse> call, Response<CurrentWeatherResponse> response) {
                        CurrentWeatherResponse current = response.body();
                        if (current != null) {
                            UpdateStatus(current);
                        }
                    }
                    @Override
                    public void onFailure(Call<CurrentWeatherResponse> call, Throwable t) {
                        Toast toast = Toast.makeText(MainActivity.this, getString(R.string.no_internet), Toast.LENGTH_LONG);
                        toast.show();
                    }
                });
    }

    private void FetchWeatherForecastFromApi() {

        NextDaysService.nextDaysService.NextDaysForecast(txtSearch.getText().toString(), "metric", "e52cee364ac0886a6d8878e7fbd3e679")
                .enqueue(new Callback<WeatherForecastResponse>() {
                    @Override
                    public void onResponse(Call<WeatherForecastResponse> call, Response<WeatherForecastResponse> response) {
                        WeatherForecastResponse forecast = response.body();
                        if (forecast != null) {
                            UpdateForecast(forecast);
                        }
                    }
                    @Override
                    public void onFailure(Call<WeatherForecastResponse> call, Throwable t) {
                        Toast toast = Toast.makeText(MainActivity.this, getString(R.string.no_internet), Toast.LENGTH_LONG);
                        toast.show();
                    }
                });
    }

    private void UpdateStatus(CurrentWeatherResponse current) {
        Picasso.get().load("http://openweathermap.org/img/wn/" + current.getWeather().get(0).getIcon() + "@2x.png").into(ivStatus);
        lblTemperature.setText(String.format(getString(R.string.degree), current.getMain().getTemp().toString()));
        lblStatus.setText(current.getWeather().get(0).getMain() + " (" +current.getWeather().get(0).getDescription() + ")");
        lblHumidity.setText(String.format(getString(R.string.percentage), current.getMain().getHumidity().toString()));
        lblCloud.setText(String.format(getString(R.string.percentage), current.getClouds().getAll().toString()));
        lblWind.setText(String.format(getString(R.string.meter_per_sec), current.getWind().getSpeed().toString()));
        lblPressure.setText(String.format(getString(R.string.h_pascal), current.getMain().getPressure().toString()));
    }

    private void UpdateForecast(WeatherForecastResponse forecast) {
        List<DailyWeather> dailyWeatherList = new Vector<>();
        for (int i = 0; i < forecast.getCnt(); i++) {
            DailyWeather current = new DailyWeather(forecast.getList().get(i).getDtTxt(),
            "http://openweathermap.org/img/wn/" + forecast.getList().get(i).getWeather().get(0).getIcon() + ".png",
                    forecast.getList().get(i).getMain().getTemp(),
                    forecast.getList().get(i).getWeather().get(0).getMain());
            dailyWeatherList.add(current);
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvNextDays.setLayoutManager(layoutManager);
        rvNextDays.setHasFixedSize(true);
        rvNextDays.setAdapter(new RecyclerViewAdapter(this, dailyWeatherList));

        UpdateTemperatureChart(forecast);
    }

    private void UpdateTemperatureChart(WeatherForecastResponse forecast) {
        DataPoint[] dataPointList = new DataPoint[forecast.getCnt()];
        for (int i = 0; i < forecast.getCnt(); i++)
            dataPointList[i] = new DataPoint(i, forecast.getList().get(i).getMain().getTemp());
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dataPointList);
        series.setColor(getColor(R.color.red_rose));
        lcTemperature.removeAllSeries();
        lcTemperature.setTitle("Temperature");
        lcTemperature.addSeries(series);
    }
}