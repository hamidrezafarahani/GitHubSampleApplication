package com.example.githubsampleapplication.utils

import android.content.Context
import android.widget.Toast

infix fun Context.log(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

infix fun Context.log(throwable: Throwable) = log("\uD83D\uDE28 Wooops $throwable")
