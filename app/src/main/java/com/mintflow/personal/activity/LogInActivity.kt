package com.mintflow.personal.activity

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.mintflow.personal.R
import com.mintflow.personal.data.PrefsHelper
import com.mintflow.personal.model.User
import com.mintflow.personal.model.Validations

class LogInActivity : AppCompatActivity() {
    private var visible = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_log_in)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val loginBtn = findViewById<Button>(R.id.loginBtn)
        val navigateToSignUpBtn = findViewById<TextView>(R.id.navigateToSignUp)
        val editTextEmail = findViewById<EditText>(R.id.editTextEmail2)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword2)
        val toggleBtn = findViewById<ImageView>(R.id.imageView26)
        val intent = intent

        val json = PrefsHelper.getUser(this)
        val userObject = Gson().fromJson(json, User::class.java)

        editTextEmail.setText(intent.getStringExtra("email"))
        editTextPassword.setText(intent.getStringExtra("password"))

        toggleBtn.setOnClickListener {
            if (visible) {
                toggleBtn.setImageResource(R.drawable.eye_slash_regular)
                editTextPassword.transformationMethod = null
                visible = false
            } else {
                toggleBtn.setImageResource(R.drawable.eye_regular)
                editTextPassword.transformationMethod =
                    android.text.method.PasswordTransformationMethod.getInstance()
                visible = true
            }
        }
        navigateToSignUpBtn.paintFlags = navigateToSignUpBtn.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        loginBtn.setOnClickListener {
            if (editTextEmail.text.toString().isEmpty()) {
                displayToast("Please enter your email", editTextEmail)
            } else if (!Validations().isEmailValid(editTextEmail.text.toString())) {
                displayToast("Please enter a valid email", editTextEmail)
            } else if (editTextPassword.text.toString().isEmpty()) {
                displayToast("Please enter your password", editTextPassword)
            } else if (!Validations().isPasswordValid(editTextPassword.text.toString())) {
                displayToast(
                    "Minimum eight characters,\n" +
                            "at least one uppercase letter, \n" +
                            "one lowercase letter, \n" +
                            "one number \n" +
                            "and one special character", editTextPassword
                )

            } else {
                val userList = PrefsHelper.getUsers(this)
                var found: Boolean = false
                userList.forEach { user ->
                    if (editTextEmail.text.toString() == user.email && editTextPassword.text.toString() == user.password) {
                        PrefsHelper.saveUser(this, Gson().toJson(user))
                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT)
                            .show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                        found = true
                        return@forEach
                    }
                }

                if (!found) {
                    Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        navigateToSignUpBtn.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun displayToast(message: String, view: EditText) {
        view.error = message
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}