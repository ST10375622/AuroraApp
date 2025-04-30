package com.fake.auroraapp

import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.Button
import android.widget.*
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
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
    private lateinit var labelStatus: TextView
    private lateinit var barChart: BarChart
    private var userId: Int = -1
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_progress)

        userId = intent.getIntExtra("USER_ID", -1)

        if (userId == -1) {
            Toast.makeText(this, "No user ID passed. Please log in again.", Toast.LENGTH_LONG)
                .show()
            finish()
            return
        }

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        toolbar = findViewById(R.id.toolbar)
        monthText = findViewById(R.id.textCurrentMonth)
        spentAmountText = findViewById(R.id.textAmountSpent)
        labelStatus = findViewById(R.id.labelStatus)
        barChart = findViewById(R.id.barChart)

        val textName = findViewById<TextView>(R.id.textProfileName)
        val db = AppDatabase.getDatabase(this)
        val userDao = db.userDao()

        //displays the user name
        lifecycleScope.launch {
            val user = userDao.getUserById(userId)
            user?.let {
                textName.text = "Hello ${it.name}"
            }
        }

        setSupportActionBar(toolbar)

        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // navigates to the different screens
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.Home -> {
                    Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    finish()
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
                    Toast.makeText(this, "Notification", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, NotificationActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    finish()
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
                }

                else -> false
            }.also {
                //closes the navigation when a choice has been made
                drawerLayout.closeDrawer(GravityCompat.START)
            }
        }


        updateMonthText()
        loadMonthlyProgress()

        //goes to the previous month
        findViewById<Button>(R.id.btnPreviousMonth).setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            updateMonthText()
            loadMonthlyProgress()
        }

        //goes to the following month
        findViewById<Button>(R.id.btnNextMonth).setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            updateMonthText()
            loadMonthlyProgress()
        }


    }

    private fun updateMonthText() {
        val sdf = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        monthText.text = sdf.format(calendar.time)
    }

    //handles the monthly progress
    private fun loadMonthlyProgress() {
        val db = AppDatabase.getDatabase(this)
        val budgetDao = db.budgetDao()
        val expenseDao = db.expenseDao()

        //simplifies the date
        //date format
        val sdfMonth = SimpleDateFormat("MM", Locale.getDefault())
        val sdfYear = SimpleDateFormat("yyyy", Locale.getDefault())

        val selectedMonth = sdfMonth.format(calendar.time)
        val selectedYear = sdfYear.format(calendar.time)

        /*the visual representation of the graph
         Code Attribution:
         MPAndroidChart Library
         Visual data representation using the MPAndroidChart library (Jahoda, 2024)
         Link: https://github.com/PhilJay/MPAndroidChart*/
        lifecycleScope.launch {
            val budget = budgetDao.getBudgetValue(userId)
            val monthlyBudgetAmount = budget?.monthlyBudget ?: 0.0

            val totalExpenses = expenseDao.getTotalSpent(selectedMonth, selectedYear) ?: 0.0

            spentAmountText.text = "R ${totalExpenses.toInt()}"

            if (monthlyBudgetAmount > 0) {
                val percentageSpent = (totalExpenses / monthlyBudgetAmount) * 100

                if (percentageSpent <= 100) {
                    labelStatus.text = "On Track"
                    labelStatus.setTextColor(
                        ContextCompat.getColor(
                            this@ProgressActivity,
                            android.R.color.holo_green_dark
                        )
                    )
                } else {
                    labelStatus.text = "Over Budget"
                    labelStatus.setTextColor(
                        ContextCompat.getColor(
                            this@ProgressActivity,
                            android.R.color.holo_red_dark
                        )
                    )
                }
                setupBarChart(monthlyBudgetAmount.toFloat(), totalExpenses.toFloat())
            } else {
                labelStatus.text = "No Budget set"
                setupBarChart(0f, 0f)
            }
        }
    }

    /*the visual representation of the graph
         Code Attribution:
         MPAndroidChart Library
         Visual data representation using the MPAndroidChart library (Jahoda, 2024)
         Link: https://github.com/PhilJay/MPAndroidChart*/
    private fun setupBarChart(budgetAmount: Float, expensesAmount: Float) {
        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(0f, budgetAmount))
        entries.add(BarEntry(1f, expensesAmount))

        val barDataSet = BarDataSet(entries, "Monthly Progress")
        barDataSet.colors = listOf(
            ContextCompat.getColor(this, android.R.color.holo_blue_dark),
            ContextCompat.getColor(this, android.R.color.holo_red_dark)
        )
        barDataSet.valueTextSize = 16f

        val data = BarData(barDataSet)
        data.barWidth = 0.5f

        barChart.data = data
        barChart.description.isEnabled = false
        barChart.legend.isEnabled = false
        barChart.setFitBars(true)

        val xAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(listOf("Budget", "Expenses"))
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.textSize = 16f

        val yAxis = barChart.axisLeft
        yAxis.textSize = 16f
        yAxis.textColor = ContextCompat.getColor(this, android.R.color.black)
        barChart.axisRight.isEnabled = false

        val legend = barChart.legend
        legend.textSize = 16f

        barChart.axisLeft.axisMinimum = 0f
        barChart.axisRight.isEnabled = false

        barChart.animateY(1000)
        barChart.invalidate()
    }
}
