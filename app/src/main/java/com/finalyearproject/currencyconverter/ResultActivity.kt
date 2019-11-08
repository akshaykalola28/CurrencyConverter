package com.finalyearproject.currencyconverter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_result.*

class ResultActivity : AppCompatActivity() {

    var isButtonForHome = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        fromAmount.text = intent.getStringExtra("FROM")
        toAmount.text = intent.getStringExtra("TO")
    }
}
