package com.example.locationsample.utils

import java.text.SimpleDateFormat
import java.util.*


fun Date.format(pattern: String): String{
    val simpleDateFormat = SimpleDateFormat(pattern)
    return simpleDateFormat.format(this)
}