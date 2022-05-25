package com.project.carunlock.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import com.project.carunlock.R
import com.project.carunlock.databinding.ActivityMainBinding
import com.project.carunlock.view.fragments.*
import com.project.carunlock.viewmodel.CarUnlockViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        val model:CarUnlockViewModel by viewModels()

        changeFragment(StudyFragment())

        model.setCons(binding.bottomBar)
        model.setGuide(binding.guideline1)
        model.hideBottomNav()

        binding.study.setOnClickListener {
            changeFragment(StudyFragment())
            model.showBottomNav()
        }

        binding.stats.setOnClickListener {
            changeFragment(StatsFragment())
            model.showBottomNav()
        }

        binding.settings.setOnClickListener {
            changeFragment(SettingsFragment())
            model.showBottomNav()
        }

    }

    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainerView, fragment)
            setReorderingAllowed(true)
            addToBackStack(null) // name can be null
            commit()
        }
    }

}