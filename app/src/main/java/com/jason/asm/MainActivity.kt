package com.jason.asm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        test()
    }

    private fun test() {
        Log.e("xulinchao", "test1")
    }

    private fun testString(): String {
        val result = 5 / 0
        return "Hello World"
    }
}