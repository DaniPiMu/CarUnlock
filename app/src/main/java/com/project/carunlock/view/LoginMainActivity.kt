package com.project.carunlock.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.project.carunlock.R
import com.project.carunlock.databinding.ActivityLoginMainBinding
import com.project.carunlock.view.fragments.LoginScreenFragment

class LoginMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_login_main)
    }

    override fun onStart() {
        super.onStart()
        supportFragmentManager.beginTransaction().apply {
            replace(binding.fragmentContainerView2.id, LoginScreenFragment())
            setReorderingAllowed(true)
            addToBackStack(null)
            commit()
        }
    }
}