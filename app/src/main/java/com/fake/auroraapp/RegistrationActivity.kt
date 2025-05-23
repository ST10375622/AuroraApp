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

        //what will happen when the register button is clicked
        registerButton.setOnClickListener {
            val email = email.text.toString().trim()
            val name = name.text.toString().trim()
            val dob = dob.text.toString().trim()
            val password = password.text.toString().trim()
            val confirmPassword = confirmPassword.text.toString().trim()

            //if the following fields are empty the toast/pop up message will display
            if (email.isBlank() || name.isBlank() || dob.isBlank() || password.isBlank()){
                //pop up will display on the screen
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
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
                    val userId = db.userDao().insertUser(user)
                    runOnUiThread {
                        Toast.makeText(this@RegistrationActivity, "Registration Successful (ID: $userId)", Toast.LENGTH_SHORT).show()

                        //direct the user to the home screen
                        val intent = Intent(this@RegistrationActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }

    //makes the date entry for dob user friendly
    //the date picker will display when the user clicks on the dob field.
    //a calander will appear and the user will be able to click on their dob
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