package com.fake.auroraapp

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class RegistrationActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registration)

        db = AppDatabase.getDatabase(this)

        val email = findViewById<EditText>(R.id.etEmail)
        val name = findViewById<EditText>(R.id.etName)
        val dob = findViewById<EditText>(R.id.etDOB)
        val password = findViewById<EditText>(R.id.etPassword)
        val confirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val registerButton = findViewById<Button>(R.id.btnRegister)

        //date picker will be displayed
        //will make it user friendly for the user
        dob.setOnClickListener{
            showDatePicker(dob)
        }

        registerButton.setOnClickListener {
            val email = email.text.toString()
            val name = name.text.toString()
            val dob = dob.text.toString()
            val password = password.text.toString()
            val confirmPassword = confirmPassword.text.toString()

            if (email.isBlank() || name.isBlank() || dob.isBlank() || password.isBlank()){
                //pop up will display on the screen
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //checks if the user already exists
            lifecycleScope.launch {
                val existingUser = db.userDao().getUserByEmail(email)
                if (existingUser != null) {
                    runOnUiThread {
                        Toast.makeText(this@RegistrationActivity, "Email already exists", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val user = User(email = email, name = name, dob = dob, password = password)
                    db.userDao().insertUser(user)
                    runOnUiThread {
                        Toast.makeText(this@RegistrationActivity, "Registration Successful", Toast.LENGTH_SHORT).show()

                        //direct the user to the home screen
                        val intent = Intent(this@RegistrationActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }

    private fun showDatePicker(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(
            this,
            {_, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = " ${selectedDay.toString().padStart(2, '0')}/" +
                        "${(selectedMonth + 1).toString().padStart(2, '0')}/" +
                        "$selectedYear"
                editText.setText(formattedDate)
            },
            year, month, day
        )
        datePicker.show()
    }
}