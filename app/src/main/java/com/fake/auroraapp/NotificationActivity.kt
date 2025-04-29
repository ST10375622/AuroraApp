package com.fake.auroraapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
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

class NotificationActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var viewModel: BudgetViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NotificationAdapter
    private lateinit var notificationViewModel: NotificationViewModel
    private lateinit var notificationAdapter: NotificationAdapter

    private var currentCategoryIdForExpense: Int = -1
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_notification)

        drawerLayout = findViewById(R.id.drawer_layout)
        toolbar = findViewById(R.id.toolbar)
        recyclerView = findViewById(R.id.RecyclerNotifications)
        navView = findViewById(R.id.nav_view)
        val imageProfile = findViewById<ImageView>(R.id.imageProfile)
        val textName = findViewById<TextView>(R.id.textProfileName)

        setSupportActionBar(toolbar)

        recyclerView.layoutManager = LinearLayoutManager(this)
        notificationAdapter = NotificationAdapter()
        recyclerView.adapter = notificationAdapter

        userId = intent.getIntExtra("USER_ID", -1)

        if (userId == -1) {
            Toast.makeText(this, "No user ID passed. Please log in again.", Toast.LENGTH_LONG)
                .show()
            finish()
            return
        }

        viewModel = ViewModelProvider(this)[BudgetViewModel::class.java]
        notificationViewModel = ViewModelProvider(this)[NotificationViewModel::class.java]

        notificationViewModel.getAllNotifications().observe(this) { notifications ->
            notificationAdapter.submitList(notifications)
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
                        Toast.makeText(this, "Budget", Toast.LENGTH_SHORT).show()
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
                    }

                    else -> false
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
        }
    }
