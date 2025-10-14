package com.example.calculator

import android.os.Bundle
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val fieldOne = findViewById<EditText>(R.id.editOne)
        val fieldTwo  =  findViewById<EditText>(R.id.editTwo)

        val zeroBtn  = findViewById<MaterialButton>(R.id.zeroBtn)
        val btnOne =  findViewById<MaterialButton>(R.id.oneBtn)
        val btnTwo =  findViewById<MaterialButton>(R.id.twoBtn)
        val btnThree =  findViewById<MaterialButton>(R.id.threeBtn)
        val btnFour =  findViewById<MaterialButton>(R.id.fourBtn)
        val btnFive =  findViewById<MaterialButton>(R.id.fiveBtn)
        val btnSix =  findViewById<MaterialButton>(R.id.sixBtn)
        val btnSeven =  findViewById<MaterialButton>(R.id.sevenBtn)
        val btnEight =  findViewById<MaterialButton>(R.id.eightBtn)
        val btnNine =  findViewById<MaterialButton>(R.id.nineBtn)

        val plusBtn = findViewById<MaterialButton>(R.id.plusBtn)
        val minusBtn = findViewById<MaterialButton>(R.id.minusBtn)
        val mulBtn = findViewById<MaterialButton>(R.id.mul)
        val divBtn = findViewById<MaterialButton>(R.id.div)
        val modBtn = findViewById<MaterialButton>(R.id.modBtn)


        val clrBtn = findViewById<MaterialButton>(R.id.clr)
        val dltBtn = findViewById<MaterialButton>(R.id.dlt)

        val equalBtn = findViewById<MaterialButton>(R.id.equlBtn)

        val listner = {button: MaterialButton ->
            fieldTwo.append(button.text)
        }

        zeroBtn.setOnClickListener { listner(zeroBtn) }
        btnOne.setOnClickListener { listner(btnOne) }
        btnTwo.setOnClickListener { listner(btnTwo) }
        btnThree.setOnClickListener { listner(btnThree) }
        btnFour.setOnClickListener { listner(btnFour) }
        btnFive.setOnClickListener { listner(btnFive) }
        btnSix.setOnClickListener { listner(btnSix) }
        btnSeven.setOnClickListener { listner(btnSeven) }
        btnEight.setOnClickListener { listner(btnEight) }
        btnNine.setOnClickListener { listner(btnNine) }

        plusBtn.setOnClickListener { listner(plusBtn) }
        minusBtn.setOnClickListener { listner(minusBtn) }
        mulBtn.setOnClickListener { listner(mulBtn) }
        divBtn.setOnClickListener { listner(divBtn) }
        modBtn.setOnClickListener { listner(modBtn) }

        dltBtn.setOnClickListener {
            val text = fieldTwo.text.toString()
            if (text.isNotEmpty()) {
                fieldTwo.setText(text.substring(0, text.length - 1))
                fieldTwo.setSelection(fieldTwo.text.length) // keeps cursor at end
            }
        }

        clrBtn.setOnClickListener {
            fieldTwo.setText("")
            fieldOne.setText("")
        }

// Handle "=" button click
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
                fieldTwo.setText("=${result.toString()}")

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