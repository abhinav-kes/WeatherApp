package com.example.weatherapp

import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.widget.SearchView

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        fetchWeatherData("Hyderabad")
        searchCity()
    }

    private fun searchCity(){
        val searchView= binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(cityName:String){
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response = retrofit.getWeatherData(
            cityName,"092dde3e01fbc5f911e00113c96f4725","metric")
        response.enqueue(object : Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody=response.body()
                if(response.isSuccessful&& responseBody!=null){
                    val temperature =responseBody.main.temp.toString()
                    val humidity =responseBody.main.humidity
                    val windSpeed =responseBody.wind.speed
                    val sunRise =responseBody.sys.sunrise.toLong()
                    val sunSet =responseBody.sys.sunset.toLong()
                    val sealevel =responseBody.main.pressure
                    val condition= responseBody.weather.firstOrNull()?.main?:"unknown"
                    val maxTemperature=responseBody.main.temp_max.toString()
                    val minTemperature=responseBody.main.temp_min.toString()

                    binding.temperature.text = "$temperature °C"
                    binding.condition.text="$condition"
                    binding.maxTemp.text="Max Temp: $maxTemperature °C"
                    binding.minTemp.text="Min Temp: $minTemperature °C"
                    binding.humidity.text="$humidity%"
                    binding.windspeed.text="$windSpeed m/s"
                    binding.sunrise.text="${time(sunRise)}"
                    binding.sunset.text="${time(sunSet)}"
                    binding.condition.text="$condition"
                    binding.sea.text="$sealevel"
                    binding.weather.text="$condition"

                    binding.day.text=dayName(System.currentTimeMillis())
                    binding.date.text=date()
                    binding.cityname.text="$cityName"

                    changeImagesAcctoWeather(condition)

                }

            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })


    }

    private fun changeImagesAcctoWeather(condition: String) {
        when(condition){
            "Partly Clouds","Clouds","Overcast","Mist","Foggy"->{
                binding.root.setBackgroundResource(R.drawable.cloud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }

            "Clear Sky","Sunny","Clear"->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain","Thunder","Thunder Storm"->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Light Snow","Moderate Snow","Heavy Snow","Blizzard"->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }

            else->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }



        }
        binding.lottieAnimationView.playAnimation()
    }

    private fun date(): String {
        val sdf= SimpleDateFormat("dd MMMM YYYY", Locale.getDefault())
        return sdf.format(Date());
    }

    private fun time(timestamp: Long): String {
        val sdf= SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp*1000));
    }

    fun dayName(timestamp: Long): String{
        val sdf= SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date());
    }
}

