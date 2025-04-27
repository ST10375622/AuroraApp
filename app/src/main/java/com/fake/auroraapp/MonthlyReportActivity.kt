package com.fake.auroraapp

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Month
import java.util.Locale
import java.time.format.TextStyle

class MonthlyReportActivity : AppCompatActivity() {

    private lateinit var viewModel: BudgetViewModel
    private lateinit var adapter: MonthlyReportAdapter
    private lateinit var recyclerView: RecyclerView

    private lateinit var textMonth: TextView
    private lateinit var textTotalSpent: TextView
    private lateinit var textTopCategory: TextView
    private lateinit var textTransactionCount: TextView

    private var currentMonth = LocalDate.now().monthValue
    private var currentYear = LocalDate.now().year

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monthly_report)

        viewModel = ViewModelProvider(this)[BudgetViewModel::class.java]
        adapter = MonthlyReportAdapter()

        recyclerView = findViewById(R.id.recyclerMonthlyExpenses)
        textMonth = findViewById(R.id.textCurrentMonth)
        textTotalSpent = findViewById(R.id.txtTotalSpent)
        textTopCategory = findViewById(R.id.txtTopCategory)
        textTransactionCount = findViewById(R.id.txtTransactionCount)


        val btnPrev = findViewById<Button>(R.id.btnPreviousMonth)
        val btnNext = findViewById<Button>(R.id.btnNextMonth)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        updateMonthText()
        loadMonthlyReport()

        btnPrev.setOnClickListener {
            currentMonth -= 1
            if (currentMonth < 1) {
                currentMonth = 12
                currentYear -= 1
            }
            updateMonthText()
            loadMonthlyReport()
        }

        btnNext.setOnClickListener {
            currentMonth += 1
            if (currentMonth > 12) {
                currentMonth = 1
                currentYear += 1
            }
            updateMonthText()
            loadMonthlyReport()
        }
    }

    private fun updateMonthText() {
        val monthName = Month.of(currentMonth).getDisplayName(TextStyle.FULL, Locale.getDefault())
        textMonth.text = "$monthName $currentYear"
    }

    private fun loadMonthlyReport() {
        viewModel.getExpensesForMonth(currentMonth, currentYear).observe(this) { expense ->
            adapter.submitList(expense)
        }

        lifecycleScope.launch {
            val total = viewModel.getTotalSpent(currentMonth, currentYear)
            val topCategory = viewModel.getTopSpendingCategory(currentMonth, currentYear)
            val transactionCount = viewModel.getTransactionCount(currentMonth, currentYear)

            textTotalSpent.text = "Total Spent: R ${total.toInt()}"
            textTopCategory.text = "Top Category: R ${topCategory?.category ?: "-"} (R ${topCategory?.total?.toInt() ?: 0})"
            textTransactionCount.text = "Transactions: $transactionCount"
        }
    }
}