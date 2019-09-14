package com.example.calculatorapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.min

class MainActivity : AppCompatActivity() {
    lateinit var buttons: Array<Button>
    val operations: Set<Char> = setOf('+', '-', '*', '/')

    enum class State {
        START, FIRST_OPERAND, SIGN, SECOND_OPERAND, RESULT
    }

    var state = State.START;

    val lengthBound = 15

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        buttons = arrayOf(
            buttonNumber0, buttonNumber1, buttonNumber2,
            buttonNumber3, buttonNumber4, buttonNumber5, buttonNumber6,
            buttonNumber7, buttonNumber8, buttonNumber9, buttonReset,
            buttonPlus, buttonMinus, buttonDivide, buttonEquals, buttonMultiply
        )
        buttons.forEach { b ->
            b.setOnClickListener { _ ->
                updateLabel(b.text[0])
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("EXPRESSION", calcResult.text.toString())
        outState.putSerializable("STATE", state)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        state = savedInstanceState.getSerializable("STATE") as State
        calcResult.text = savedInstanceState.getString("EXPRESSION")
    }

    @SuppressLint("SetTextI18n")
    fun updateLabel(symbol: Char) {
        if (symbol.isDigit()) {
            if (state == State.START || state == State.RESULT) {
                calcResult.text = symbol.toString()
                if(symbol != '0'){
                    state = State.FIRST_OPERAND
                }
            } else if (calcResult.text.length < lengthBound) {
                calcResult.append(symbol.toString())
                if (state == State.SIGN) {
                    state = State.SECOND_OPERAND
                }
            }
        } else if (symbol == 'C') {
            calcResult.setText("0")
            state = State.START
        } else if (operations.contains(symbol)) {
            if (state == State.SIGN) {
                calcResult.text = getNumberString(0, calcResult.text.toString()) + " " +
                        symbol + " "
            } else if (state == State.FIRST_OPERAND) {
                calcResult.append(" $symbol ")
                state = State.SIGN
            }
        } else if (symbol == '=') {
            val result = getResult()
            calcResult.setText(result.substring(0, min(lengthBound, result.length)))
            state = State.RESULT
        }

    }

    fun getResult(): String {
        //parsing the expression
        val text = calcResult.text.replace(Regex(" "), "")
        var pointer = 0
        val firstOperand = getNumberString(pointer, text)
        pointer += firstOperand.length
        if (pointer + 1 >= text.length) {
            return firstOperand
        }
        val sign = text[pointer]
        pointer++
        val secondOperand = getNumberString(pointer, text)
        return evaluate(firstOperand.toLong().toDouble(), secondOperand.toLong().toDouble(), sign).toString()
    }

    fun getNumberString(pos: Int, text: String): String {
        //searching for an integer

        var pointer = pos
        while (pointer < text.length && text[pointer].isDigit()) {
            pointer++
        }
        return text.substring(pos, pointer)
    }

    fun evaluate(firstOperand: Double, secondOperand: Double, operation: Char): Double {
        when (operation) {
            '+' -> return firstOperand + secondOperand
            '-' -> return firstOperand - secondOperand
            '*' -> return firstOperand * secondOperand
            '/' -> return firstOperand / secondOperand
        }
        return 0.0
    }


}
