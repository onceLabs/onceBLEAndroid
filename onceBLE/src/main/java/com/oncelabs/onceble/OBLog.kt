package com.oncelabs.onceble

import android.util.Log

class OBLog(enabled : Boolean){
    val _enabled = enabled

    fun log(string: String){
       if(_enabled){
           Log.i("onceBLE", string)
       }
    }
}