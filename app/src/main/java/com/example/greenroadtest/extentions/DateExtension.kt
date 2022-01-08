package com.example.greenroadtest.extentions;

import java.text.SimpleDateFormat
import java.util.*

fun Date.format(): String {

    val output = SimpleDateFormat("dd/MM/yy, HH:mm:ss", Locale.getDefault())
    return output.format(this)
}
