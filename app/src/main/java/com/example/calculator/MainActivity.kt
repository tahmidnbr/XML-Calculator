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
import com.example.calculator.databinding.ActivityMainBinding
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.apply {
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            navigationBarColor = Color.TRANSPARENT
        }

        val activeColor = ColorStateList.valueOf(Color.parseColor("#00ADB5"))

        binding.calculator.backgroundTintList = activeColor

        binding.bmi.setOnClickListener {
            startActivity(Intent(this@MainActivity, BMIActivity::class.java))
        }

        val fieldOne = binding.editOne
        val fieldTwo  =  binding.editTwo

        val clrBtn = binding.clr
        val dltBtn = binding.dlt
        val equalBtn = binding.equlBtn

        //Display pressed buttons
        val listner = {button: MaterialButton ->
            fieldTwo.append(button.text)
        }

        val buttons = listOf(
            binding.zeroBtn, binding.oneBtn, binding.twoBtn,
            binding.threeBtn, binding.fourBtn, binding.fiveBtn,
            binding.sixBtn, binding.sevenBtn, binding.eightBtn, binding.nineBtn,
            binding.plusBtn, binding.minusBtn, binding.mul, binding.div, binding.modBtn, binding.dotBtn
        )

        for(btn in buttons){
            btn.setOnClickListener { listner(btn) }
        }

        //Clear last element
        dltBtn.setOnClickListener {
            val text = fieldTwo.text.toString()
            if (text.isNotEmpty()) {
                fieldTwo.setText(text.substring(0, text.length - 1))
                fieldTwo.setSelection(fieldTwo.text!!.length) // keeps cursor at end
            }
        }

        //Clear all elements both text fields
        clrBtn.setOnClickListener {
            fieldTwo.setText("")
            fieldOne.setText("")
        }


        //Operation of equal btn
        equalBtn.setOnClickListener {
            val expression = fieldTwo.text.toString()

            try {
                // Replace special symbols with actual operators
                val formattedExpr = expression
                    .replace("×", "*")
                    .replace("÷", "/")

                // Evaluate expression safely
                val result = eval(formattedExpr)

                // Move equation up and show result down
                fieldOne.setText(expression)
                fieldTwo.setText("${result}")

            } catch (e: Exception) {
                fieldTwo.setText("Error")
            }
        }


    }

    private fun eval(expr: String): Double {
        return object : Any() {
            var pos = -1
            var ch = 0

            fun nextChar() {
                ch = if (++pos < expr.length) expr[pos].code else -1
            }

            fun eat(charToEat: Int): Boolean {
                while (ch == ' '.code) nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < expr.length) throw RuntimeException("Unexpected: " + expr[pos])
                return x
            }

            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    when {
                        eat('+'.code) -> x += parseTerm()
                        eat('-'.code) -> x -= parseTerm()
                        else -> return x
                    }
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    when {
                        eat('*'.code) -> x *= parseFactor()
                        eat('/'.code) -> x /= parseFactor()
                        else -> return x
                    }
                }
            }

            fun parseFactor(): Double {
                if (eat('+'.code)) return parseFactor()
                if (eat('-'.code)) return -parseFactor()

                var x: Double
                val startPos = pos
                if (eat('('.code)) {
                    x = parseExpression()
                    eat(')'.code)
                } else if ((ch >= '0'.code && ch <= '9'.code) || ch == '.'.code) {
                    while ((ch >= '0'.code && ch <= '9'.code) || ch == '.'.code) nextChar()
                    x = expr.substring(startPos, pos).toDouble()
                } else {
                    throw RuntimeException("Unexpected: " + ch.toChar())
                }

                return x
            }
        }.parse()
    }

}