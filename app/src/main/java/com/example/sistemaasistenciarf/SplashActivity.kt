package com.example.sistemaasistenciarf

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import com.example.sistemaasistenciarf.auth.AuthFragment
import androidx.appcompat.app.AppCompatDelegate
import kotlin.jvm.java


@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, AuthFragment::class.java)
            startActivity(intent)
            finish()
        }, 5000)

        // ⚠️ Forzar tema claro
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        hideSystemUI()
    }

}
