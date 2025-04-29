package com.fake.auroraapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Month
import java.util.Locale
import java.time.format.TextStyle

class MonthlyReportActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var toggle: ActionBarDrawerToggle
    private var userId: Int = -1
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


        userId = intent.getIntExtra("USER_ID", -1)

        if (userId == -1) {
            Toast.makeText(this, "No user ID passed. Please log in again.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        viewModel = ViewModelProvider(this)[BudgetViewModel::class.java]
        adapter = MonthlyReportAdapter()

        recyclerView = findViewById(R.id.recyclerMonthlyExpenses)
        textMonth = findViewById(R.id.textCurrentMonth)
        textTotalSpent = findViewById(R.id.txtTotalSpent)
        textTopCategory = findViewById(R.id.txtTopCategory)
        textTransactionCount = findViewById(R.id.txtTransactionCount)
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        toolbar = findViewById(R.id.toolbar)

        val textName = findViewById<TextView>(R.id.textProfileName)
        val btnPrev = findViewById<Button>(R.id.btnPreviousMonth)
        val btnNext = findViewById<Button>(R.id.btnNextMonth)
        val btnAllExpenses = findViewById<Button>(R.id.btnAllExpenses)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        setSupportActionBar(toolbar)

        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.Home -> {
                    Toast.makeText(this, "coming soon", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.Budget -> {
                    Toast.makeText(this, "Budget Page", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, BudgetActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.Notification -> {
                    Toast.makeText(this, "coming soon", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.Reports -> {
                    Toast.makeText(this, "Monthly Reports", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MonthlyReportActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.Progress -> {
                    Toast.makeText(this, "Progress Screen", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, ProgressActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.Profile -> {
                    Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, ProfileActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    finish()
                    true
                } else -> false
            }.also {
                //closes the navigation when a choice has been made
                drawerLayout.closeDrawer(GravityCompat.START)
            }
        }

        lifecycleScope.launch {
            val user = viewModel.getUser(userId)
            user?.let {
                textName.text = "Hello, ${it.name}"
            }
        }

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

        btnAllExpenses.setOnClickListener {
            Toast.makeText(this, "All Expenses", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, AllExpensesActivity::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
            finish()
            true
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