package com.fake.auroraapp

import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
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
import androidx.lifecycle.lifecycleScope
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class ProgressActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var monthText: TextView
    private lateinit var spentAmountText: TextView
    private lateinit var budgetProgressBar: ProgressBar
    private lateinit var expensesProgressBar: ProgressBar
    private lateinit var labelStatus: TextView
    private var userId: Int = -1
    private val calendar = Calendar.getInstance()
    private val monthlyBudget = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_progress)

        userId = intent.getIntExtra("USER_ID", -1)

        if (userId == -1) {
            Toast.makeText(this, "No user ID passed. Please log in again.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        toolbar = findViewById(R.id.toolbar)
        monthText = findViewById(R.id.textCurrentMonth)
        spentAmountText = findViewById(R.id.textAmountSpent)
        budgetProgressBar = findViewById(R.id.budgetProgressBar)
        expensesProgressBar = findViewById(R.id.expenseProgressBar)
        labelStatus = findViewById(R.id.labelStatus)

        val textName = findViewById<TextView>(R.id.textProfileName)
        val db = AppDatabase.getDatabase(this)
        val userDao = db.userDao()

        lifecycleScope.launch {
            val user = userDao.getUserById(userId)
            user?.let {
                textName.text = "Hello ${it.name}"
            }
        }

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
                    val intent = Intent(this, AllExpensesActivity::class.java)
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


        updateMonthText()
        loadMonthlyProgress()

        findViewById<Button>(R.id.btnPreviousMonth).setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            updateMonthText()
            loadMonthlyProgress()
        }

        findViewById<Button>(R.id.btnNextMonth).setOnClickListener {
            calendar.add(Calendar.MONTH,  1)
            updateMonthText()
            loadMonthlyProgress()
        }



    }

    private fun updateMonthText(){
        val sdf = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        monthText.text = sdf.format(calendar.time)
    }

    private fun loadMonthlyProgress() {
        val db = AppDatabase.getDatabase(this)
        val expenseDao = db.expenseDao()

        val sdfMonth = SimpleDateFormat("MM", Locale.getDefault())
        val sdfYear = SimpleDateFormat("yyyy", Locale.getDefault())

        val selectedMonth = sdfMonth.format(calendar.time)
        val selectedYear = sdfYear.format(calendar.time)

        lifecycleScope.launch {
            val expensesList = expenseDao.getExpensesByMonthNow(selectedMonth, selectedYear)
            val totalExpenses = expensesList.sumOf {it.amount}

            spentAmountText.text = "R ${totalExpenses.toInt()}"

            val percentageSpent = (totalExpenses / monthlyBudget * 100).coerceAtMost(100.00).toInt()

            budgetProgressBar.progress = 100
            expensesProgressBar.progress = percentageSpent

            if (percentageSpent <= 100) {
                labelStatus.text = "On Track"
            } else {
                labelStatus.text = "Over budget"
            }
        }
    }
}