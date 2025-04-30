package com.fake.auroraapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch
import java.time.LocalDate

class ProfileActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var toggle: ActionBarDrawerToggle
    private var userId: Int = -1
    private lateinit var viewModel: BudgetViewModel
    private lateinit var treeImageView: ImageView
    private lateinit var treeStageLabel: TextView
    private lateinit var streakLabel: TextView

    private val expenseviewModel: ExpenseViewModel by viewModels()
    private  var currentMonth = LocalDate.now().monthValue
    private var currentYear = LocalDate.now().year

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        //retrives the user id
        userId = intent.getIntExtra("USER_ID", -1)
        if (userId == -1) {
            Toast.makeText(this, "No user ID passed. Please log in again.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        viewModel = ViewModelProvider(this)[BudgetViewModel::class.java]

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        toolbar = findViewById(R.id.toolbar)
        treeImageView = findViewById(R.id.treeImageView)
        treeStageLabel = findViewById(R.id.treeStageLabel)
        streakLabel = findViewById(R.id.streakLabel)

        val textName = findViewById<TextView>(R.id.textProfileName)

        setSupportActionBar(toolbar)

        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        //navigates to the different screens
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
                    Toast.makeText(this, "Notifications screen", Toast.LENGTH_SHORT).show()
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
                } else -> false
            }.also {
                //closes the navigation when a choice has been made
                drawerLayout.closeDrawer(GravityCompat.START)
            }
        }

        //displays the users name
        lifecycleScope.launch {
            val user = viewModel.getUser(userId)
            user?.let {
                textName.text = "Hello, ${it.name}"
            }
        }

        expenseviewModel.dailyStreak.observe(this, Observer { streak ->
            val days = streak?.currentStreak ?: 0
            streakLabel.text = "🔥 $days-day streak!"
        })

        expenseviewModel.loadStreak(userId)

        expenseviewModel.getTransactionCount(userId).observe(this, Observer { transactionCount ->
            updateTreeStageBasedOnTransactions(transactionCount)
        })
    }

    //the image of the tree will change based off of the number of transactions
    //this means that the user is entering all their expenses in order to grow their tree
    private fun updateTreeStageBasedOnTransactions(transactionCount: Int) {
        val (label, drawableRes) = when {
            transactionCount < 5 -> "Seed" to R.mipmap.tree_seed_foreground
            transactionCount < 10 -> "Sprout" to R.mipmap.tree_sprout_foreground
            transactionCount < 20 -> "Young Tree" to R.mipmap.tree_young_round
            transactionCount < 30 -> "Mature Tree" to R.mipmap.tree_mature_foreground
            else -> "Full Tree" to R.mipmap.tree_full_foreground
        }
        treeStageLabel.text = "Your Tree Stage: $label"
        treeImageView.setImageResource(drawableRes)
    }

}