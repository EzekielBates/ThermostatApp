package com.example.thermostatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.ToggleButton

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

        val temperatureInfo = TemperatureData() //Replaced after pulling from database functionality

        setTemp = findViewById(R.id.setTemp)
        setTemp.text = temperatureInfo.setTemp.toString()

        currentTemp = findViewById(R.id.currentTemp)
        currentTemp.text = temperatureInfo.currentTemp.toString()

        increase = findViewById(R.id.increase)
        increase.setOnClickListener{
            if(temperatureInfo.setTemp < 100) {
                temperatureInfo.setTemp++
                updateSetTemp(setTemp, temperatureInfo)
            }
        }

        decrease = findViewById(R.id.decrease)
        decrease.setOnClickListener {
            if(temperatureInfo.setTemp > 50) {
                temperatureInfo.setTemp--
                updateSetTemp(setTemp, temperatureInfo)
            }
        }
    }

    fun updateSetTemp(textView: TextView, temperatureInfo: TemperatureData){
        textView.text = temperatureInfo.setTemp.toString()
    }


}
