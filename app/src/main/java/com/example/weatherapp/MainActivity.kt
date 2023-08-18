package com.example.weatherapp

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.ScriptGroup.Binding
import android.util.Log
import android.widget.SearchView
import com.example.weatherapp.databinding.ActivityMainBinding
import org.json.JSONObject.NULL
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//6ff860233b437df8f96234a0c35b4632
class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("Nagpur")
        searchcity()
    }

    private fun searchcity() {
        val searchview = binding.searchView
        searchview.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
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

    private fun fetchWeatherData(cityName:String) {
      val retrofit = Retrofit.Builder()
          .addConverterFactory(GsonConverterFactory.create())
          .baseUrl("https://api.openweathermap.org/data/2.5/")
          .build().create(ApiInterface::class.java)
        val response= retrofit.getWeatherData(cityName,"6ff860233b437df8f96234a0c35b4632","metric")
        response.enqueue(object : Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody =response.body()
                if(response.isSuccessful && responseBody!=null){
                    val temprature = responseBody.main.temp
                    binding.temp.text= "$temprature °C"
                    val humi = responseBody.main.humidity
                    binding.Humidity.text="$humi %"
                    val wind =responseBody.wind.speed
                    binding.wind.text = "$wind m/s"
                    val sunRise = responseBody.sys.sunrise
                    binding.sunrise.text ="$sunRise"
                    val sunSet = responseBody.sys.sunset
                    binding.sunset.text ="$sunSet"
                    val sealevel = responseBody.main.pressure
                    binding.sea.text ="$sealevel hPa"
                    val condition = responseBody.weather.firstOrNull()?.main
                    binding.weather.text = condition
                    val maxTemp = responseBody.main.temp_max
                    binding.MaxTemp.text = "Max Temp: $maxTemp °C"
                    val minTemp = responseBody.main.temp_max
                    binding.MinTemp.text = "Max Temp: $minTemp °C"
                    //Log.d("TAG","onResponse $temprature")
                    binding.condition.text = condition
                    binding.cityName.text="$cityName"
                    binding.Day.text = dayName(System.currentTimeMillis())
                    binding.date.text = date()

                    if (condition != null)
                    {
                        changeImgWeather(condition)
                    }
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun changeImgWeather(condition: String) {
        when(condition){
            "Haze","Mist","Foggy","Partly Clouds","Clouds","Overcast"->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Clear sky","Sunny","Clear"->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Light Rain", "Drizzle", "Moderate Rain","Showers", "Heavy Rain"->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Light Snow","Moderate Snow","Heavy Snow","Snow","Blizzard"->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            else->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
        }
        binding.lottieAnimationView.playAnimation()
    }

    private fun date(): String {
        val sdf =SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))

    }

    fun dayName(timestamp: Long):String{
        val sdf =SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
}