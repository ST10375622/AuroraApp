package com.fake.auroraapp

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import  android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.InputType
import android.widget.Adapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import com.fake.auroraapp.Category
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.Legend.LegendForm
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class BudgetActivity : AppCompatActivity(), ExpenseImagePicker {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var viewModel: BudgetViewModel
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>
    private lateinit var takePictureLauncher: ActivityResultLauncher<Intent>
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var cameraImageUri: Uri
    private var selectedReceiptUri: Uri? = null
    private var currentCategoryIdForExpense: Int = -1
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_budget)

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
        val imageProfile = findViewById<ImageView>(R.id.imageProfile)
        val textName = findViewById<TextView>(R.id.textProfileName)
        val textBudget = findViewById<TextView>(R.id.textMonthlyBudget)
        val textLeft = findViewById<TextView>(R.id.textMoneyLeft)
        val btnSetBudget = findViewById<Button>(R.id.btSetBudget)
        val pieChart = findViewById<PieChart>(R.id.pieChartDaily)
        val addCategory = findViewById<FloatingActionButton>(R.id.AddCategory)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerCategories)

        setSupportActionBar(toolbar)

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
                    Toast.makeText(this, "Notification screen", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(this, "coming soon", Toast.LENGTH_SHORT).show()
                    true
                } else -> false
            }.also {
                //closes the navigation when a choice has been made
                drawerLayout.closeDrawer(GravityCompat.START)
            }
        }

        categoryAdapter = CategoryAdapter(viewModel, this, this, userId)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = categoryAdapter

        lifecycleScope.launch {
            val user = viewModel.getUser(userId)
            user?.let {
                textName.text = "Hello, ${it.name}"
            }
        }

        imagePickerLauncher = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            if (uri != null && currentCategoryIdForExpense != -1) {
                val internalPath = copyImageToInternalStorage(uri)
                if (internalPath != null) {
                    showAddExpenseDialog(currentCategoryIdForExpense, internalPath)
                } else {
                    Toast.makeText(this, "Failed to save receipt", Toast.LENGTH_SHORT).show()
                }
            }
        }

        cameraLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val photo = result.data!!.extras?.get("data") as? Bitmap
                photo?.let {
                    val savedPath = saveBitmapToInternalStorage(it)
                    if (savedPath != null && currentCategoryIdForExpense != -1) {
                        showAddExpenseDialog(currentCategoryIdForExpense, savedPath)
                    } else {
                        Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        viewModel.getBudget(userId).observe(this) { budget ->
            budget?.let {
                textBudget.text = "Budget: R ${it.monthlyBudget}"
                textLeft.text = "Left: R ${it.amountLeft}"

            }
        }

        viewModel.getCategories(userId).observe(this) { categories ->
            categoryAdapter.submitList(categories)

            val categoryMap = categories.associate {it.id to it.name}

            viewModel.getAllExpenses().observe(this) { expenses ->
                updatePieChart(pieChart, expenses, categoryMap)
            }
        }

        btnSetBudget.setOnClickListener {
            showBudgetDialog()
        }

        addCategory.setOnClickListener {
            showAddCategoryDialog()
        }
    }

    private fun saveBitmapToInternalStorage(bitmap: Bitmap): String? {
        return try {
            val fileName = "camera_image_${System.currentTimeMillis()}.jpg"
            val file = File(filesDir, fileName)
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun openCameraForCategory(categoryId: Int) {
        currentCategoryIdForExpense = categoryId
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraLauncher.launch(cameraIntent)
    }


    override fun pickImageForCategory(categoryId: Int) {
        val options = arrayOf("Take Photo", "Choose from Gallery")
        AlertDialog.Builder(this)
            .setTitle("Add Receipt")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openCameraForCategory(categoryId)
                    1 -> {
                        currentCategoryIdForExpense = categoryId
                        imagePickerLauncher.launch("image/*")
                    }
                }
            }
            .show()
    }




    private fun updatePieChart(pieChart: PieChart, expenses: List<Expense>, categoryMap: Map<Int, String>) {

        val groupedExpenses = expenses.groupBy { it.categoryId }

        val entries = groupedExpenses.map {  (categoryId, expenseList) ->
            PieEntry(
                expenseList.sumOf { it.amount }.toFloat(), ""
            )
        }

        val labelsForLegend = groupedExpenses.map { (categoryId, _) ->
            categoryMap[categoryId] ?: "Unkown"
        }

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()

        dataSet.setDrawValues(true)
        dataSet.valueFormatter = PercentFormatter(pieChart)

        val data = PieData(dataSet)
        data.setValueTextSize(14f)
        data.setValueFormatter(PercentFormatter(pieChart))

        pieChart.data = data
        pieChart.setUsePercentValues(true)
        pieChart.setDrawEntryLabels(false)

        val legend = pieChart.legend
        legend.isEnabled = true
        legend.textSize = 14f
        legend.form = Legend.LegendForm.CIRCLE

        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.xEntrySpace = 100f

        legend.setCustom(
            labelsForLegend.mapIndexed { index, label ->
                LegendEntry(label, Legend.LegendForm.CIRCLE, 10f, 2f, null, dataSet.colors[index])
            }
        )
        pieChart.invalidate()

    }

    private fun showBudgetDialog() {

        val dialogView = layoutInflater.inflate(R.layout.dialog_set_budget, null)
        val monthlyBudgetInput = dialogView.findViewById<EditText>(R.id.editMonthlyBudget)
        val minimumBudgetInput = dialogView.findViewById<EditText>(R.id.editMinimumBudget)

        AlertDialog.Builder(this)
            .setTitle("Set Budget")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val monthly = monthlyBudgetInput.text.toString().toDoubleOrNull()
                val minimum = minimumBudgetInput.text.toString().toDoubleOrNull()

                if(monthly != null && minimum != null) {
                   viewModel.updateAmountLeft(userId, monthly, minimum)
                } else {
                    Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showAddCategoryDialog() {
        val input = EditText(this)
        AlertDialog.Builder(this)
            .setTitle("New Category")
            .setView(input)
            .setPositiveButton("Add") { _, _ ->
                val name = input.text.toString()
                if (name.isNotBlank()) {
                    viewModel.insertCategory(Category(userId = userId, name = name))
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showAddExpenseDialog(categoryId: Int, imageUri: String? = null) {
        categoryAdapter.submitList(categoryAdapter.currentList)
    }

    private fun copyImageToInternalStorage(uri: Uri): String? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val fileName = "receipt_${System.currentTimeMillis()}.jpg"
            val file = File(filesDir, fileName)
            val outputStream = FileOutputStream(file)

            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()

            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
