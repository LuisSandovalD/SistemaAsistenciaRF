package com.example.sistemaasistenciarf.auth

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.sistemaasistenciarf.R

@SuppressLint("CustomSplashScreen")
class LoginFragment : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.fragment_login)


    }
}
