package com.example.calculator

import android.R.attr.navigationBarColor
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.example.calculator.databinding.FragmentBmiBinding
import com.google.android.material.textfield.TextInputEditText

class BmiFragment : Fragment() {

    private lateinit var binding: FragmentBmiBinding
    private var activeField: TextInputEditText? = null
    private var PREFS_NAME = "prefs"
    private var KEY_ONE = "one"
    private var KEY_TWO = "two"
    private var KEY_THREE = "three"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBmiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().window.apply {
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            navigationBarColor = Color.TRANSPARENT
        }

        localPrefs()

        val activeColor = ColorStateList.valueOf(Color.parseColor("#00ADB5"))

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

        weight.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) activeField = weight }
        height.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) activeField = height }

        val buttons = listOf(
            binding.zeroBtn, binding.oneBtn, binding.twoBtn,
            binding.threeBtn, binding.fourBtn, binding.fiveBtn,
            binding.sixBtn, binding.sevenBtn, binding.eightBtn, binding.nineBtn
        )

        buttons.forEach { btn ->
            btn.setOnClickListener { insertText(btn.text.toString()) }
        }

        binding.equlBtn.setOnClickListener {
            val wTXT = binding.weightField.text.toString()
            val hTXT = binding.heightField.text.toString()

            if (wTXT.isEmpty() || hTXT.isEmpty()) {
                Toast.makeText(requireContext(), "One or more input fields are empty!", LENGTH_SHORT).show()
            } else {
                equal(wTXT, hTXT)
            }
        }

        binding.acBtn.setOnClickListener { ac() }
        binding.clrBtn.setOnClickListener { clear() }
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
        val  prefs =requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val txt = prefs.getString(KEY_ONE, null)
        val txt1 = prefs.getString(KEY_TWO, null)
        val txt2 = prefs.getString(KEY_THREE, null)

        if (txt != null || txt1 != null || txt2 !=null){
            binding.editTwo.setText(txt)
            binding.weightField.setText(txt1)
            binding.heightField.setText(txt2)
        }

    }

    private fun ac(){
        binding.weightField.setText("")
        binding.heightField.setText("")
        binding.editTwo.setText("0")
    }

    private fun clear(){
        activeField?.let { field ->
            val currentText = field.text?.toString() ?: ""
            if (currentText.isNotEmpty()) {
                field.setText(currentText.dropLast(1))
                field.setSelection(field.text?.length ?: 0) // move cursor to end
            }
        }
    }

    private fun equal(w: String, h: String){
        val weight = w.toDouble()
        val height = h.toDouble()
        val bmi = weight /((height/100.0)*(height/100.0))
        val bmiRounded = "%.2f".format(bmi)
        binding.editTwo.setText(bmiRounded)

        val prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().apply{
            putString(KEY_ONE, bmiRounded)
            putString(KEY_TWO,  w)
            putString(KEY_THREE, h)
            apply()
        }
    }
}

