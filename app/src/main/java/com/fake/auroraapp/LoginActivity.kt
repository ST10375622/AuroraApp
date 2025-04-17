package com.fake.auroraapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        db = AppDatabase.getDatabase(this)


        val emailFeild = findViewById<EditText>(R.id.etLoginEmail)
        val passwordFeild = findViewById<EditText>(R.id.etLoginPassword)
        val LoginButton = findViewById<Button>(R.id.btnLogin)

        LoginButton.setOnClickListener {
            val email = emailFeild.text.toString()
            val password = passwordFeild.text.toString()

            if(email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val user = db.userDao().login(email, password)

                if (user != null) {
                    runOnUiThread {
                        Toast.makeText(this@LoginActivity, "Login Successful", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.putExtra("USER_NAME", user.name)
                        startActivity(intent)
                        finish()
                    }
                }else {
                    runOnUiThread {
                        Toast.makeText(this@LoginActivity, "Invalid Email or password", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}