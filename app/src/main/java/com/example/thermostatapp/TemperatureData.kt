package com.example.thermostatapp

data class TemperatureData(var setTemp:Int=71, var currentTemp:Int=69, var heat:Boolean=false,
                            var cool:Boolean=true, var power:Boolean=false){
}
