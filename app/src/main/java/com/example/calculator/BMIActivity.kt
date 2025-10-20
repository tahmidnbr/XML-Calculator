package com.example.calculator

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.calculator.databinding.ActivityBmiactivityBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class BMIActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBmiactivityBinding
    private var activeField: TextInputEditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityBmiactivityBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        val defaultColor = ColorStateList.valueOf(Color.parseColor("#EEEEEE"))
        val activeColor = ColorStateList.valueOf(Color.parseColor("#00ADB5"))

        val calculatorButton = binding.calculator
        val bmiButton = binding.bmi
        val convButton = binding.conv
        val binaryButton = binding.binary

        calculatorButton.backgroundTintList = defaultColor
        bmiButton.backgroundTintList = activeColor
        convButton.backgroundTintList = defaultColor
        binaryButton.backgroundTintList = defaultColor

        binding.calculator.setOnClickListener {
            startActivity(Intent(this@BMIActivity, MainActivity::class.java))
        }

        val weight = binding.weightField
        val height = binding.heightField
        val fieldTwo  =  binding.editTwo
        fieldTwo.showSoftInputOnFocus = false

        weight.apply {
            isFocusable = true
            isFocusableInTouchMode = true
            showSoftInputOnFocus = false
            isCursorVisible = true
        }

        height.apply {
            isFocusable = true
            isFocusableInTouchMode = true
            showSoftInputOnFocus = false
            isCursorVisible = true
        }


        weight.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) activeField = weight
        }

        height.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) activeField = height
        }


        val buttons = listOf(
            binding.zeroBtn, binding.oneBtn, binding.twoBtn,
            binding.threeBtn, binding.fourBtn, binding.fiveBtn,
            binding.sixBtn, binding.sevenBtn, binding.eightBtn, binding.nineBtn
        )

        for (btn in buttons) {
            btn.setOnClickListener {
                insertText(btn.text.toString())
            }
        }
    }

    private fun insertText(value: String) {
        activeField?.let { field ->
            val current = field.text?.toString() ?: ""
            val cursorPos = field.selectionStart
            val newText = current.substring(0, cursorPos) + value + current.substring(cursorPos)
            field.setText(newText)
            field.setSelection(cursorPos + value.length)
        }
    }
}