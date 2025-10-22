package com.example.calculator

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.calculator.databinding.ActivityBmiactivityBinding
import com.google.android.material.textfield.TextInputEditText
import android.view.View


class BMIActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBmiactivityBinding
    private var activeField: TextInputEditText? = null
    private var PREFS_NAME = "prefs"
    private var KEY_ONE = "one"
    private var KEY_TWO = "two"
    private var KEY_THREE = "three"

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityBmiactivityBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        window.apply {
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            navigationBarColor = Color.TRANSPARENT
        }


        localPrefs()

        val activeColor = ColorStateList.valueOf(Color.parseColor("#00ADB5"))
        binding.bmi.backgroundTintList = activeColor

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

        binding.equlBtn.setOnClickListener {
            val wTXT = binding.weightField.text.toString()
            val hTXT = binding.heightField.text.toString()

            if (wTXT.isEmpty() ||  hTXT.isEmpty()){
                Toast.makeText(this, "One or more inputfields are empty!", LENGTH_SHORT).show()
            }else{
                val w = wTXT.toDouble()
                val h = hTXT.toDouble()
                val bmi = w /((h/100.0)*(h/100.0))
                val bmiRounded = "%.2f".format(bmi)
                binding.editTwo.setText(bmiRounded)

                val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                prefs.edit().apply{
                    putString(KEY_ONE, bmiRounded)
                    putString(KEY_TWO,  wTXT)
                    putString(KEY_THREE, hTXT)
                    apply()
                }
            }
        }

        binding.acBtn.setOnClickListener {
            weight.setText("")
            height.setText("")
            binding.editTwo.setText("0")
        }

        binding.clrBtn.setOnClickListener {
            activeField?.let { field ->
                val currentText = field.text?.toString() ?: ""
                if (currentText.isNotEmpty()) {
                    field.setText(currentText.dropLast(1))
                    field.setSelection(field.text?.length ?: 0) // move cursor to end
                }
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

    private fun localPrefs(){
        val  prefs =getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val txt = prefs.getString(KEY_ONE, null)
        val txt1 = prefs.getString(KEY_TWO, null)
        val txt2 = prefs.getString(KEY_THREE, null)

        if (txt != null || txt1 != null || txt2 !=null){
            binding.editTwo.setText(txt)
            binding.weightField.setText(txt1)
            binding.heightField.setText(txt2)
        }

    }

}