package com.example.thermostatapp

data class TemperatureData(var temperature:Int=70, var housetemp:Int=69, var hotorcold:Boolean=false,
                             var onoroff:Boolean=true){
}
