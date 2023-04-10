package com.example.weatherapp.alert

import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.example.weatherapp.R
import com.example.weatherapp.databinding.AlertWindowBinding

class AlertWindowManger( private val context: Context,private val description: String) {

    private lateinit var windowManager: WindowManager
    private lateinit var view: View
    private lateinit var binding: AlertWindowBinding

    fun initializeWindowManager() {

        // Inflate UI and Use View Binding
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = inflater.inflate(R.layout.alert_window, null)
        binding = AlertWindowBinding.bind(view)

        // Set Data
        binding.textDescription.text = description
        binding.btnDismiss.setOnClickListener {
            closeWindowManger()
            closeService()
        }

        // Initialize View
        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val width = (context.resources.displayMetrics.widthPixels * 0.85).toInt()
        val params = WindowManager.LayoutParams(
            width,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
            },
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON ,
            PixelFormat.TRANSPARENT
        )
        params.y=-(context.resources.displayMetrics.heightPixels * 0.80).toInt()
        windowManager.addView(view, params)
    }



    private fun closeWindowManger() {
        try {
            (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).removeView(view)
            view.invalidate()
            (view.parent as ViewGroup).removeAllViews()
        } catch (e: Exception) {
            Log.d("Error", e.toString())
        }
    }

    private fun closeService() {
        context.stopService(Intent(context, AlertService::class.java))
    }
}