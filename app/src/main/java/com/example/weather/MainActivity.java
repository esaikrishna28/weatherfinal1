package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.GridLayout;
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

import java.util.List;
import java.util.Vector;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private Animation animFadeZoomOut;
    private Animation animFadeOut;
    private Animation animFadeIn;

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
    private GridLayout glFurtherDetails;
    private TextView lblDescription;
    private TextView lblFeelLikes;
    private TextView lblTempMin;
    private TextView lblTempMax;
    private TextView lblCloudiness;
    private TextView lblHumidityDetail;
    private TextView lblWindDetail;
    private TextView lblPressureDetail;

    private WeatherForecastResponse forecast;

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

        rvNextDays.addOnItemTouchListener(
                new RecyclerItemClickListener(MainActivity.this, rvNextDays, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (lcTemperature.getVisibility() == View.VISIBLE) {
                            lcTemperature.startAnimation(animFadeZoomOut);
                            lcTemperature.setVisibility(View.GONE);
                            glFurtherDetails.setVisibility(View.VISIBLE);
                            ShowWeatherForcastDetail(position);
                        }
                        else {
                            glFurtherDetails.setAnimation(animFadeZoomOut);
                            glFurtherDetails.setVisibility(View.GONE);
                            lcTemperature.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // remain idle
                    }
                })
        );
    }

    private void Map() {
        animFadeZoomOut = AnimationUtils.loadAnimation(this, R.anim.fade_zoom_out_animation);
        animFadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out_animation);
        animFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in_animation);

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
        glFurtherDetails = findViewById(R.id.gl_further_details);
        lblDescription = findViewById(R.id.lbl_description);
        lblFeelLikes = findViewById(R.id.lbl_feel_likes_value);
        lblTempMin = findViewById(R.id.lbl_temp_min_value);
        lblTempMax = findViewById(R.id.lbl_temp_max_value);
        lblCloudiness = findViewById(R.id.lbl_cloud_value);
        lblHumidityDetail = findViewById(R.id.lbl_humidity_detail_value);
        lblWindDetail = findViewById(R.id.lbl_wind_speed_detail_value);
        lblPressureDetail = findViewById(R.id.lbl_pressure_detail_value);
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
                        forecast = response.body();
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
        ivStatus.startAnimation(animFadeIn);
        lblTemperature.setText(String.format(getString(R.string.degree), current.getMain().getTemp().toString()));
        lblTemperature.startAnimation(animFadeIn);
        lblStatus.setText(current.getWeather().get(0).getMain() + " (" +current.getWeather().get(0).getDescription() + ")");
        lblStatus.startAnimation(animFadeIn);
        lblHumidity.setText(String.format(getString(R.string.percentage), current.getMain().getHumidity().toString()));
        lblHumidity.startAnimation(animFadeIn);
        lblCloud.setText(String.format(getString(R.string.percentage), current.getClouds().getAll().toString()));
        lblCloud.startAnimation(animFadeIn);
        lblWind.setText(String.format(getString(R.string.meter_per_sec), current.getWind().getSpeed().toString()));
        lblWind.startAnimation(animFadeIn);
        lblPressure.setText(String.format(getString(R.string.h_pascal), current.getMain().getPressure().toString()));
        lblPressure.startAnimation(animFadeIn);
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

        rvNextDays.startAnimation(animFadeIn);

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
        lcTemperature.startAnimation(animFadeIn);
    }

    private void ShowWeatherForcastDetail(int position) {
        lblDescription.setText(forecast.getList().get(position).getWeather().get(0).getDescription());
        lblFeelLikes.setText(String.format(getString(R.string.degree), forecast.getList().get(position).getMain().getFeelsLike().toString()));
        lblTempMin.setText(String.format(getString(R.string.degree), forecast.getList().get(position).getMain().getTempMin().toString()));
        lblTempMax.setText(String.format(getString(R.string.degree), forecast.getList().get(position).getMain().getTempMax().toString()));
        lblCloudiness.setText(String.format(getString(R.string.percentage), forecast.getList().get(position).getClouds().getAll().toString()));
        lblHumidityDetail.setText(String.format(getString(R.string.percentage), forecast.getList().get(position).getMain().getHumidity().toString()));
        lblWindDetail.setText(String.format(getString(R.string.meter_per_sec), forecast.getList().get(position).getWind().getSpeed().toString()));
        lblPressureDetail.setText(String.format(getString(R.string.h_pascal), forecast.getList().get(position).getMain().getPressure().toString()));
    }
}