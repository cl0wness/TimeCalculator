package com.app.timecalculator

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.absoluteValue

class MainActivity : AppCompatActivity() {
    private lateinit var etOne: EditText

    private lateinit var etTwo: EditText

    private lateinit var btnAdd: Button

    private lateinit var btnSub: Button

    private lateinit var tvOperation: TextView

    private lateinit var tvResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etOne = findViewById(R.id.etInputOne)
        etTwo = findViewById(R.id.etInputTwo)
        btnAdd = findViewById(R.id.btnAdd)
        btnSub = findViewById(R.id.btnSub)
        tvOperation = findViewById(R.id.tvOperation)
        tvResult = findViewById(R.id.tvResult)

        btnAdd.setOnClickListener {
            onBtnClick(Operation.ADD)
        }

        btnSub.setOnClickListener {
            onBtnClick(Operation.SUB)
        }
    }

    private fun onBtnClick(oper: Operation) {
        val first = etOne.text.toString()
        val second = etTwo.text.toString()

        // Если строки пустые или невалидные - показываем сообщение
        if (!first.isValidTimeFormat() || first.isEmpty()
            || !second.isValidTimeFormat() || second.isEmpty()
        ) {
            tvResult.text = getString(R.string.enterTimesInCorrectFormat)
        } else { // Иначе считаем
            val firstSecs = convertToSeconds(first)
            val secondSecs = convertToSeconds(second)

            val result = when (oper) {
                Operation.ADD -> {
                    tvOperation.text = resources.getString(R.string.plus) // знак
                    firstSecs + secondSecs // значение
                }

                Operation.SUB -> {
                    tvOperation.text = resources.getString(R.string.minus) // знак
                    firstSecs - secondSecs // значение
                }
            }
            // Если значение отрицательное, дописываем "-" в строку, а конвертируем без знака
            val resultStr = (if (result < 0) "-" else "") + convertToTimeStr(result.absoluteValue)
            tvResult.text = resultStr
        }
    }

    private fun String.isValidTimeFormat(): Boolean {
        val regex = Regex("""^(?:(\d+h)?(\d+m)?(\d+s)?)$""")
        return regex.matches(this)
    }

    private fun convertToSeconds(str: String): Int {
        val regex = Regex("""(\d+)h|(\d+)m|(\d+)s""")
        var totalSeconds = 0

        regex.findAll(str).forEach { match ->
            val hours = match.groups[1]?.value?.toIntOrNull() ?: 0
            val minutes = match.groups[2]?.value?.toIntOrNull() ?: 0
            val seconds = match.groups[3]?.value?.toIntOrNull() ?: 0

            totalSeconds += hours * 3600 + minutes * 60 + seconds
        }

        return totalSeconds
    }

    private fun convertToTimeStr(totalSec: Int): String {
        val hours = totalSec / 3600
        val minutes = (totalSec % 3600) / 60
        val seconds = totalSec % 60

        return buildString {
            if (hours > 0) append("${hours}h")
            if (minutes > 0) append("${minutes}m")
            if (seconds > 0) append("${seconds}s")
            if (hours == 0 && minutes == 0 && seconds == 0) append("${seconds}s")
        }
    }

    enum class Operation {
        ADD,
        SUB
    }
}