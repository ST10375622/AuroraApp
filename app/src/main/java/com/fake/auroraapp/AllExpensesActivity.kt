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
import java.time.format.TextStyle
import java.util.Locale

class AllExpensesActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var toggle: ActionBarDrawerToggle
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

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        toolbar = findViewById(R.id.toolbar)
        recyclerView = findViewById(R.id.RecyclerAllExpenses)
        textMonth = findViewById(R.id.textCurrentMonth)
        val btnPrev = findViewById<Button>(R.id.btnPreviousMonth)
        val btnNext = findViewById<Button>(R.id.btnNextMonth)
        val btnBackToReport = findViewById<Button>(R.id.btnBackToReport)
        val textName = findViewById<TextView>(R.id.textProfileName)

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
                    Toast.makeText(this, "coming soon", Toast.LENGTH_SHORT).show()
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

        btnBackToReport.setOnClickListener {
            Toast.makeText(this, "Monthly Reports", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MonthlyReportActivity::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
            finish()
            true
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