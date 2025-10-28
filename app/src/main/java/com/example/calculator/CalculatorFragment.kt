package com.example.calculator

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import com.example.calculator.databinding.ActivityMainBinding
import com.example.calculator.databinding.FragmentCalculatorBinding
import com.google.android.material.button.MaterialButton
import kotlin.text.append


class CalculatorFragment : Fragment() {

    private lateinit var binding: FragmentCalculatorBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentCalculatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().window.apply {
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            navigationBarColor = Color.TRANSPARENT
        }

        val fieldOne = binding.editOne
        val fieldTwo = binding.editTwo

        val clrBtn = binding.clr
        val dltBtn = binding.dlt
        val equalBtn = binding.equlBtn

        // Display pressed buttons
        val listener = { button: MaterialButton ->
            fieldTwo.append(button.text)
        }

        val buttons = listOf(
            binding.zeroBtn, binding.oneBtn, binding.twoBtn,
            binding.threeBtn, binding.fourBtn, binding.fiveBtn,
            binding.sixBtn, binding.sevenBtn, binding.eightBtn, binding.nineBtn,
            binding.plusBtn, binding.minusBtn, binding.mul, binding.div, binding.modBtn, binding.dotBtn
        )

        buttons.forEach { btn -> btn.setOnClickListener { listener(btn) } }

        // Delete last character
        dltBtn.setOnClickListener {
            val text = fieldTwo.text.toString()
            if (text.isNotEmpty()) {
                fieldTwo.setText(text.dropLast(1))
                fieldTwo.setSelection(fieldTwo.text!!.length)
            }
        }

        // Clear all
        clrBtn.setOnClickListener {
            fieldOne.setText("")
            fieldTwo.setText("")
        }

        // Equal button
        equalBtn.setOnClickListener {
            val expression = fieldTwo.text.toString()
            try {
                val formattedExpr = expression.replace("×", "*").replace("÷", "/")
                val result = eval(formattedExpr)
                fieldOne.setText(expression)
                fieldTwo.setText(result.toString())
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