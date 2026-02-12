package com.mintflow.personal.activity

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.gson.Gson
import com.mintflow.personal.R
import com.mintflow.personal.data.PrefsHelper
import com.mintflow.personal.model.User
import com.mintflow.personal.model.Validations


class SignUpActivity : AppCompatActivity() {
    private lateinit var signupBtn: Button
    private lateinit var editTextName: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private var visible = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        signupBtn = findViewById(R.id.signUpBtn)
        val navigateToLoginBtn = findViewById<TextView>(R.id.navigateToLogin)
        val toggleBtn = findViewById<ImageView>(R.id.imageView28)
        editTextName = findViewById(R.id.editTextName)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        val termsAndConditions = findViewById<MaterialCheckBox>(R.id.radioButton1)

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

        navigateToLoginBtn.paintFlags = navigateToLoginBtn.paintFlags or Paint.UNDERLINE_TEXT_FLAG


        signupBtn.setOnClickListener {
            if (editTextName.text.toString().isEmpty()) {
                displayToast("Please enter your name", editTextName)
            } else if (!Validations().isSpaceInBetween(editTextName.text.toString())) {
                displayToast("Please enter your full name", editTextName)
            } else if (editTextEmail.text.toString().isEmpty()) {
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

            } else if (!termsAndConditions.isChecked) {
                termsAndConditions.error = "Please accept the terms and conditions"
                Toast.makeText(this, "Please accept the terms and conditions", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val userList = PrefsHelper.getUsers(this)
                var found: Boolean = false
                userList.forEach { user ->
                    if (editTextEmail.text.toString() == user.email) {
                        Toast.makeText(this, "User Already Signed Up! Log In", Toast.LENGTH_SHORT)
                            .show()
                        navigateToLogin(user.email, user.password)
                        found = true
                        return@forEach
                    }
                }

                if (!found) {
                    val user = User(
                        editTextName.text.toString(),
                        editTextEmail.text.toString(),
                        editTextPassword.text.toString()
                    )
                    userList.add(user)
                    PrefsHelper.saveUsers(this, userList)

                    Toast.makeText(this, "Sign up successful", Toast.LENGTH_SHORT).show()
                    resetText()
                    navigateToLogin(user.email, user.password)
                }

            }
        }

        navigateToLoginBtn.setOnClickListener {
            startActivity(Intent(this, LogInActivity::class.java))
        }


    }

    private fun displayToast(message: String, view: EditText) {
        view.error = message
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun storeUserPref(userJson: String) {
        PrefsHelper.saveUser(this, userJson)
    }

    private fun resetText() {
        editTextName.text.clear()
        editTextEmail.text.clear()
        editTextPassword.text.clear()
    }

    private fun navigateToLogin(email: String, password: String) {
        val intent = Intent(this, LogInActivity::class.java)
        intent.putExtra("email", email)
        intent.putExtra("password", password)
        startActivity(intent)
        finish()
    }
}