package com.fake.auroraapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
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
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Locale
import android.graphics.Color

class HomeActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var toggle: ActionBarDrawerToggle
    private var userId: Int = -1
    private lateinit var viewModel: BudgetViewModel
    private lateinit var adapter: AllExpenseAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var textMonth: TextView
    private lateinit var pieChart: PieChart
    private lateinit var legendContainer: LinearLayout
    private lateinit var textTotalExpenses: TextView

    private var calendar = Calendar.getInstance()
    private  var currentMonth = LocalDate.now().monthValue
    private var currentYear = LocalDate.now().year

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        //retrives the users id so that the name and data can be displayed
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
        textMonth = findViewById(R.id.textCurrentMonth)
        legendContainer = findViewById(R.id.legendContainer)
        textTotalExpenses = findViewById(R.id.textTotalExpenses)
        pieChart = findViewById(R.id.pieChart)

        val btnPrev = findViewById<Button>(R.id.btnPreviousMonth)
        val btnNext = findViewById<Button>(R.id.btnNextMonth)
        val textName = findViewById<TextView>(R.id.textProfileName)
        val textBudget = findViewById<TextView>(R.id.textMonthlyBudget)
        val textLeft = findViewById<TextView>(R.id.textMoneyLeft)

        setSupportActionBar(toolbar)

        //directs the user to the specific screen
        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.Home -> {
                    Toast.makeText(this, "Home Page", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(this, "Progress", Toast.LENGTH_SHORT).show()
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

        //Displays the users name
        lifecycleScope.launch {
            val user = viewModel.getUser(userId)
            user?.let {
                textName.text = "Hello, ${it.name}"
            }
        }

        //displays to the user the amount left and their budget
        //made use of live data
        viewModel.getBudget(userId).observe(this) { budget ->
            budget?.let {
                textBudget.text = "Budget: R ${it.monthlyBudget}"
                textLeft.text = "Left: R ${it.amountLeft}"

            }
        }

        updateMonthText()
        loadExpenses()

        //Changes the date to the previous month
        btnPrev.setOnClickListener {
            currentMonth -= 1
            if (currentMonth < 1) {
                currentMonth = 12
                currentYear -= 1
            }
            updateMonthText()
            loadExpenses()
        }

        //Changes the date to the next month
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

    //Converts the numeric month into a readable string
    //Code Attribution
    //Class LocalDate
    //Oracle (2024)
    private  fun updateMonthText() {
        val monthName = Month.of(currentMonth).getDisplayName(TextStyle.FULL, Locale.getDefault())
        textMonth.text = "$monthName $currentYear"
    }

    private fun loadExpenses() {
        viewModel.getExpensesForMonth(currentMonth, currentYear).observe(this) { expenses ->
            adapter.submitList(expenses)

            val total = expenses.sumOf { it.amount }
            textTotalExpenses.text = "R $total\nYour total Expenses so far"

            setUpPieChart(expenses)
            setupLegend(expenses)
            viewModel.getBudget(userId).observe(this) { budget ->
                budget?.let {
                    val amountLeft = it.monthlyBudget - total
                    findViewById<TextView>(R.id.textMonthlyBudget).text = "Budget: R ${it.monthlyBudget}"
                    findViewById<TextView>(R.id.textMoneyLeft).text = "Left: R $amountLeft"
                }
            }
        }
    }


    /*the visual representation of the graph
             Code Attribution:
             MPAndroidChart Library
             Visual data representation using the MPAndroidChart library (Jahoda, 2024)
             Link: https://github.com/PhilJay/MPAndroidChart*/
    private fun setUpPieChart(expenses: List<Expense>)
    {

        val entries = expenses.groupBy { it.categoryId }
            .map { (categoryId, expenseList) ->
                val categoryName = runBlocking { viewModel.getCategoryName(categoryId) ?: "Unkown" }
                val total = expenseList.sumOf { it.amount }.toFloat()
                PieEntry(total, categoryName)
            }

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.valueTextSize = 20f
        dataSet.valueTextColor = Color.BLACK
        val pieData = PieData(dataSet).apply {
            setValueFormatter(object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return String.format("%.1f%%", value)
                }
            })
        }

        pieChart.data = pieData
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.centerText = "Expenses"
        pieChart.setEntryLabelTextSize(18f)
        pieChart.animateY(1000)
        pieChart.invalidate()
        pieChart.setEntryLabelColor(Color.BLACK)

        pieChart.legend.isEnabled = false
    }

    private fun setupLegend(expenses: List<Expense>) {
        legendContainer.removeAllViews()
        val context = this

        val grouped = expenses.groupBy { it.categoryId }

        grouped.forEach { (categoryId, expenseList) ->
            val total = expenseList.sumOf { it.amount }
            val categoryName = runBlocking { viewModel.getCategoryName(categoryId) ?: "Unknown" }

            val item = TextView(context).apply {
                text = "$categoryName: \n R$total"
                textSize = 18f
                setPadding(8, 8, 8, 8)
            }

            legendContainer.addView(item)
        }

    }
}