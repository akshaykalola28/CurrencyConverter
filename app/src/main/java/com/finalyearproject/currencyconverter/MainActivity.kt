package com.finalyearproject.currencyconverter

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.finalyearproject.currencyconverter.services.Service
import com.finalyearproject.currencyconverter.services.ServiceBuilder
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    var fromCurrencyCode: String = ""
    var toCurrencyCode: String = ""
    private var amount: Double? = null
    var mDialog: ProgressDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initSpinners()
        checkNowButton.setOnClickListener {
            if (getValidData()) {
                mDialog = ProgressDialog(this)
                mDialog?.setMessage("Please Wait...")
                mDialog?.setCanceledOnTouchOutside(false)
                mDialog?.show()
                getCurrentRate()
            }
        }
    }

    private fun getValidData(): Boolean {
        var isValid = false
        val amountString = amountValue.text.toString()

        if (fromCurrencyCode.equals(toCurrencyCode)) {
            Toast.makeText(this, "Select Different Currency", Toast.LENGTH_SHORT).show()
        } else if (amountString == "") {
            Toast.makeText(this, "Enter Amount", Toast.LENGTH_SHORT).show()
        } else {
            isValid = true
        }
        amount = amountString.toDouble()
        return isValid
    }

    private fun initSpinners() {
        ArrayAdapter.createFromResource(
            this,
            R.array.currency_array,
            android.R.layout.simple_spinner_item
        ).also { arrayAdapter ->
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            fromSpinner.adapter = arrayAdapter
            toSpinner.adapter = arrayAdapter
        }

        val stringArray = resources.getStringArray(R.array.currency_array)

        fromSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                fromCurrencyCode = stringArray[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        toSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                toCurrencyCode = stringArray[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    private fun getCurrentRate() {
        val exchangeRateService = ServiceBuilder.buildService(Service::class.java)
        val requestCall =
            exchangeRateService.getExchangeRate("$fromCurrencyCode,$toCurrencyCode")
        requestCall.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val rates = response.body()?.get("rates")?.asJsonObject
                    val fromCurrencyRate = rates!!.get(fromCurrencyCode)!!.asDouble
                    val toCurrencyRate = rates.get(toCurrencyCode).asDouble

                    Toast.makeText(
                        this@MainActivity,
                        "$fromCurrencyRate | $toCurrencyRate",
                        Toast.LENGTH_SHORT
                    ).show()
                    convertCurrency(fromCurrencyRate, toCurrencyRate)
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                mDialog?.dismiss()
            }
        })
    }

    private fun convertCurrency(fromCurrencyRate: Double, toCurrencyRate: Double) {
        val resultAmount = (toCurrencyRate / fromCurrencyRate) * amount!!

        mDialog?.dismiss()

        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra("FROM", "$fromCurrencyCode $amount")
        intent.putExtra("TO", "$toCurrencyCode $resultAmount")
        startActivity(intent)
    }
}
