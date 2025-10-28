package com.example.calculator

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.calculator.databinding.ActivityMainBinding
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // keep this if implemented
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set default fragment
        replaceFragment(CalculatorFragment())

        // Bottom navigation item selection
        binding.topNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_cal -> replaceFragment(CalculatorFragment())
                R.id.nav_bmi -> replaceFragment(BmiFragment()) // <-- FIXED
                //R.id.nav_convert -> replaceFragment(SearchFragment())
                //R.id.nav_binary -> replaceFragment(BinaryFragment())// <-- FIXED
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.layout, fragment)
            .commit()
    }

}