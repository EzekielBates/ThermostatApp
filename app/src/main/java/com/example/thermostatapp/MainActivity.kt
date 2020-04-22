package com.example.thermostatapp

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.web.client.RestTemplate



class MainActivity : AppCompatActivity() {

    private lateinit var setTemp: TextView
    private lateinit var currentTemp: TextView
    private lateinit var increase: Button
    private lateinit var decrease: Button
    private lateinit var power: ToggleButton
    private lateinit var cool: ToggleButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val temperatureInfo = TemperatureData()

        setTemp = findViewById(R.id.setTemp)
        currentTemp = findViewById(R.id.currentTemp)
        power = findViewById(R.id.toggleButton4)
        cool = findViewById(R.id.toggleButton6)

        var t = GetData()
        t.launchDataLoad("http://192.168.1.66:8000/thermostat/thermostatsettings/get/1",temperatureInfo,setTemp,currentTemp,power,cool)

        increase = findViewById(R.id.increase)
        increase.setOnClickListener{
            if(temperatureInfo.temperature < 100) {
                temperatureInfo.temperature++
                updateSetTemp(setTemp, temperatureInfo)
            }
        }

        decrease = findViewById(R.id.decrease)
        decrease.setOnClickListener {
            if(temperatureInfo.temperature > 50) {
                temperatureInfo.temperature--
                updateSetTemp(setTemp, temperatureInfo)
            }
        }
    }

    private fun updateSetTemp(textView: TextView, temperatureInfo: TemperatureData){
        textView.text = temperatureInfo.temperature.toString()
    }

    class GetData : ViewModel() {

        private val completableJob = Job()
        private val coroutineScope = CoroutineScope(Dispatchers.IO + completableJob)

        fun launchDataLoad(url:String,temperatureInfo: TemperatureData,setTemp: TextView,currentTemp: TextView,power:ToggleButton,cool:ToggleButton){
            coroutineScope.launch {
                request(url,temperatureInfo)
                update(setTemp,currentTemp,temperatureInfo,power,cool)
            }
        }

        override fun onCleared() {
            super.onCleared()
            //Canceling a job when the ViewModel is being finished .
            completableJob.cancel()
        }

        fun update(setTemp:TextView,currentTemp:TextView,temperatureInfo: TemperatureData,power: ToggleButton,cool: ToggleButton){
            setTemp.text = temperatureInfo.temperature.toString()
            currentTemp.text = temperatureInfo.housetemp.toString()

        }

        fun request(url: String, temperatureInfo: TemperatureData?){
            // Create a new RestTemplate instance
            // Create a new RestTemplate instance
            val restTemplate = RestTemplate()

            // Add the String message converter
            // Add the String message converter
            restTemplate.messageConverters.add(StringHttpMessageConverter())

            // Make the HTTP GET request, marshaling the response to a String
            // Make the HTTP GET request, marshaling the response to a String
            val result =
                restTemplate.getForObject(url, String::class.java, "Android")
            var json = Gson().fromJson(result,TemperatureData::class.java)
            temperatureInfo?.temperature = json.temperature
            temperatureInfo?.hotorcold = json.hotorcold
            temperatureInfo?.housetemp = json.housetemp
            temperatureInfo?.onoroff = json.onoroff

        }

    }
}
