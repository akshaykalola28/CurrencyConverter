package com.finalyearproject.currencyconverter

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_result.*
import java.io.File
import java.io.FileOutputStream
import java.util.*


class ResultActivity : AppCompatActivity() {

    //Define for making Home Button
    var isButtonForHome = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        //set the result in textview
        fromAmount.text = intent.getStringExtra("FROM")
        toAmount.text = intent.getStringExtra("TO")

        initPermission();

        takeImageLayout.setOnClickListener {
            takeScreenshot()
        }

        saveThisButton.setOnClickListener {
            if (isButtonForHome) {
                finish()
            } else {
                takeScreenshot()
            }
        }
    }

    //Take permission to write external storage
    private fun initPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                Log.d(TAG, "Permission is granted");
            } else {
                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
                );
            }
        } else {
            Log.v(TAG, "Permission is granted");
        }
    }

    private fun takeScreenshot() {

        takeImageLayout.visibility = View.GONE
        val now = Date()

        try {
            // define path
            val path =
                Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg"

            //screenshot create
            val v1 = window.decorView.rootView
            v1.isDrawingCacheEnabled = true
            v1.buildDrawingCache(true)
            val bitmap = Bitmap.createBitmap(v1.drawingCache)
            v1.isDrawingCacheEnabled = false

            //save file
            val imageFile = File(path)
            val outputStream = FileOutputStream(imageFile)
            val quality = 100
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            outputStream.flush()
            outputStream.close()

            //set the layout
            Toast.makeText(this, "ScreenShot is Store At Location: $path", Toast.LENGTH_SHORT)
                .show()
            setLayout(bitmap)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private fun setLayout(bitmap: Bitmap) {
        screenshotImage.visibility = View.VISIBLE
        screenshotImage.setImageBitmap(bitmap)
        isButtonForHome = true
        saveThisButton.text = "Home"
    }

    companion object {
        const val TAG = "ResultActivity"
    }
}
