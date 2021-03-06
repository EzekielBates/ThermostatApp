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
        power = findViewById(R.id.toggleButton6)
        cool = findViewById(R.id.toggleButton4)

        val t = GetData()
        t.launchDataLoad("http://ztthermostat.tech:5000/thermostat/thermostatsettings/get/1",temperatureInfo,setTemp,currentTemp)

        power.isChecked = temperatureInfo.onoroff
        cool.isChecked = !temperatureInfo.hotorcold

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

        power.setOnClickListener{
            if(!power.isChecked){
                cool.isEnabled = false
                updateSetPower(false,temperatureInfo)
            }
            else if(power.isChecked){
                cool.isEnabled = true
                updateSetPower(true,temperatureInfo)
            }
        }

        cool.setOnClickListener{
            if(!cool.isChecked ){
                updateSetCool(false,temperatureInfo)
            }
            else if(cool.isChecked){
                updateSetCool(true,temperatureInfo)
            }
        }
    }
    //updates the hot/cold setting on the server
    private fun updateSetCool(horc : Boolean,temperatureInfo: TemperatureData){
        val s = SetData()
        temperatureInfo.hotorcold = horc
        s.launchDataLoad(temperatureInfo)
    }
    //updates the on/off setting on the server
    private fun updateSetPower(onoroff : Boolean,temperatureInfo: TemperatureData){
        val s = SetData()
        temperatureInfo.onoroff = onoroff
        s.launchDataLoad(temperatureInfo)
    }
    //update the temperature setting on the server.
    private fun updateSetTemp(textView: TextView, temperatureInfo: TemperatureData){
        val s = SetData()
        textView.text = temperatureInfo.temperature.toString()
        s.launchDataLoad(temperatureInfo)

    }

    /*
    Uses coroutines and spring to update the server with new information from the app.
     */
    class SetData : ViewModel (){

        private val completableJob = Job()
        private val coroutineScope = CoroutineScope(Dispatchers.IO + completableJob)

        fun launchDataLoad(tempInfo:TemperatureData){
            coroutineScope.launch{
                val restTemplate = RestTemplate()

                restTemplate.messageConverters.add(StringHttpMessageConverter())
                var oof = "True"
                var horc = "True"

                if(!tempInfo.onoroff){
                    oof = "False"
                }

                if(!tempInfo.hotorcold){
                    horc = "False"
                }

                //urls string to update data on the server.
                val url = "http://ztthermostat.tech:5000/thermostat/thermostatsettings/set/1?temp=" + tempInfo.temperature.toString() + "&horc=" + horc + "&oof=" + oof + "&htemp=" + tempInfo.housetemp.toString()

                restTemplate.getForObject(url, String::class.java, "Android")

            }
        }
    }
    /*
    Uses couroutines and spring to pull data from the server.
     */
    class GetData : ViewModel() {

        private val completableJob = Job()
        private val coroutineScope = CoroutineScope(Dispatchers.IO + completableJob)

        fun launchDataLoad(url:String,temperatureInfo: TemperatureData,setTemp: TextView,currentTemp: TextView){
            coroutineScope.launch {
                request(url,temperatureInfo)
                update(setTemp,currentTemp,temperatureInfo)
            }
        }

        override fun onCleared() {
            super.onCleared()
            //Canceling a job when the ViewModel is being finished .
            completableJob.cancel()
        }
        //updates the temperatures text on the app
        private fun update(setTemp:TextView, currentTemp:TextView, temperatureInfo: TemperatureData){
            setTemp.text = temperatureInfo.temperature.toString()
            currentTemp.text = temperatureInfo.housetemp.toString()
        }

        private fun request(url: String, temperatureInfo: TemperatureData?){
            // Create a new RestTemplate instance
            val restTemplate = RestTemplate()

            // Add the String message converter
            restTemplate.messageConverters.add(StringHttpMessageConverter())

            // Make the HTTP GET request, marshaling the response to a String
            val result =
                restTemplate.getForObject(url, String::class.java, "Android")
            //Loads the data from the website into the TemperatureData class
            val json = Gson().fromJson(result,TemperatureData::class.java)
            temperatureInfo?.temperature = json.temperature
            temperatureInfo?.hotorcold = json.hotorcold
            temperatureInfo?.housetemp = json.housetemp
            temperatureInfo?.onoroff = json.onoroff

        }

    }
}
