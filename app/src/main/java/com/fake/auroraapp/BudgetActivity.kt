package com.fake.auroraapp

import android.net.Uri
import  android.os.Bundle
import android.text.InputType
import android.widget.Adapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
import java.io.File
import java.io.FileOutputStream


class BudgetActivity : AppCompatActivity(), ExpenseImagePicker {

    private lateinit var viewModel: BudgetViewModel
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>
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

        val imageProfile = findViewById<ImageView>(R.id.imageProfile)
        val textName = findViewById<TextView>(R.id.textProfileName)
        val textBudget = findViewById<TextView>(R.id.textMonthlyBudget)
        val textLeft = findViewById<TextView>(R.id.textMoneyLeft)
        val btnSetBudget = findViewById<Button>(R.id.btSetBudget)
        val pieChart = findViewById<PieChart>(R.id.pieChartDaily)
        val addCategory = findViewById<FloatingActionButton>(R.id.AddCategory)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerCategories)

        categoryAdapter = CategoryAdapter(viewModel, this, this)
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

    override fun pickImageForCategory(categoryId: Int) {
        currentCategoryIdForExpense = categoryId
        imagePickerLauncher.launch("image/*")
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
        val input = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        }
        AlertDialog.Builder(this)
            .setTitle("Set Monthly Budget")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val amount = input.text.toString().toDoubleOrNull() ?: return@setPositiveButton
                val budget = Budget(userId, monthlyBudget = amount, amountLeft = amount)
                viewModel.insertOrUpdateBudget(budget)
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
