package com.fake.auroraapp

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

class AllExpensesActivity : AppCompatActivity() {

    private var userId: Int = -1
    private lateinit var viewModel: BudgetViewModel
    private lateinit var adapter: AllExpenseAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var textMonth: TextView

    private  var currentMonth = LocalDate.now().monthValue
    private var currentYear = LocalDate.now().year

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_expenses)

        userId = intent.getIntExtra("USER_ID", -1)

        if (userId == -1) {
            Toast.makeText(this, "No user ID passed. Please log in again.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        viewModel = ViewModelProvider(this)[BudgetViewModel::class.java]
        adapter = AllExpenseAdapter()

        recyclerView = findViewById(R.id.RecyclerAllExpenses)
        textMonth = findViewById(R.id.textCurrentMonth)
        val btnPrev = findViewById<Button>(R.id.btnPreviousMonth)
        val btnNext = findViewById<Button>(R.id.btnNextMonth)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        updateMonthText()
        loadExpenses()

        btnPrev.setOnClickListener {
            currentMonth -= 1
            if (currentMonth < 1) {
                currentMonth = 12
                currentYear -= 1
            }
            updateMonthText()
            loadExpenses()
        }

        btnNext.setOnClickListener {
            currentMonth += 1
            if (currentMonth > 12) {
                currentMonth = 1
                currentYear += 1
            }
            updateMonthText()
            loadExpenses()
        }
    }

    private  fun updateMonthText() {
        val monthName = Month.of(currentMonth).getDisplayName(TextStyle.FULL, Locale.getDefault())
        textMonth.text = "$monthName $currentYear"
    }

    private fun loadExpenses() {
        viewModel.getExpensesForMonth(currentMonth, currentYear).observe(this) { expenses ->
            adapter.submitList(expenses)
        }
    }
}