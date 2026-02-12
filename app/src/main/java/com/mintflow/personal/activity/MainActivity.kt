package com.mintflow.personal.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mintflow.personal.R
import com.mintflow.personal.data.PrefsHelper
import com.mintflow.personal.fragment.BudgetFragment
import com.mintflow.personal.fragment.HomeFragment
import com.mintflow.personal.fragment.ProfileFragment
import com.mintflow.personal.fragment.TransactionFragment

class MainActivity : AppCompatActivity() {
    private var toggle = true

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val main = findViewById<ConstraintLayout>(R.id.main)
        val homeNav = findViewById<TextView>(R.id.home_nav)
        val transactionNav = findViewById<TextView>(R.id.transaction_nav)
        val budgetNav = findViewById<TextView>(R.id.budget_nav)
        val profileNav = findViewById<TextView>(R.id.profile_nav)
        val addBtn = findViewById<ImageButton>(R.id.addBtn)
        val addIncomeBtn = findViewById<ImageButton>(R.id.incomeBtn)
        val addExpenseBtn = findViewById<ImageButton>(R.id.expenseBtn)
        val summaryBtn = findViewById<ImageButton>(R.id.transactionBtn)
        val fragmentViewId = R.id.fragmentContainerView
        val intent = intent
        val from = intent.getStringExtra("from")

        checkForPermission()
        switchOnAlertsByDefault()
        if (from != null) {
            if (from == "createBudget") {
                changeFragment(
                    budgetNav,
                    transactionNav,
                    homeNav,
                    profileNav,
                    BudgetFragment(),
                    fragmentViewId
                )
            } else if (from == "settings") {
                changeFragment(
                    profileNav,
                    budgetNav,
                    transactionNav,
                    homeNav,
                    ProfileFragment(),
                    fragmentViewId
                )
            }
        } else {
            changeFragment(
                homeNav,
                transactionNav,
                budgetNav,
                profileNav,
                HomeFragment(),
                fragmentViewId
            )
        }

        homeNav.setOnClickListener {
            changeFragment(
                homeNav,
                transactionNav,
                budgetNav,
                profileNav,
                HomeFragment(),
                fragmentViewId
            )
        }
        transactionNav.setOnClickListener {
            changeFragment(
                transactionNav,
                homeNav,
                budgetNav,
                profileNav,
                TransactionFragment(),
                fragmentViewId
            )
        }
        budgetNav.setOnClickListener {
            changeFragment(
                budgetNav,
                transactionNav,
                homeNav,
                profileNav,
                BudgetFragment(),
                fragmentViewId
            )
        }
        profileNav.setOnClickListener {
            changeFragment(
                profileNav,
                budgetNav,
                transactionNav,
                homeNav,
                ProfileFragment(),
                fragmentViewId
            )
        }

        addBtn.setOnClickListener {
            if (toggle) {
                startAnimation(addBtn, addIncomeBtn, addExpenseBtn, summaryBtn, main)
                toggle = false
            } else {
                reverseAnimation(addBtn, addIncomeBtn, addExpenseBtn, summaryBtn, main)
                toggle = true
            }
        }

        addIncomeBtn.setOnClickListener {
            reverseAnimation(addBtn, addIncomeBtn, addExpenseBtn, summaryBtn, main)
            val i = Intent(this, IncomeExpenseActivity::class.java)
            i.putExtra("type", "Income")
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(i)
                finish()
            }, 230)

        }
        addExpenseBtn.setOnClickListener {
            reverseAnimation(addBtn, addIncomeBtn, addExpenseBtn, summaryBtn, main)
            val ii = Intent(this, IncomeExpenseActivity::class.java)
            ii.putExtra("type", "Expense")

            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(ii)
                finish()
            }, 230)
        }
        summaryBtn.setOnClickListener {
            reverseAnimation(addBtn, addIncomeBtn, addExpenseBtn, summaryBtn, main)
            val i = Intent(this, SummaryActivity::class.java)
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(i)
            }, 220)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
//                checkForPermission()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkForPermission() {
        if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                101
            )
        }

    }

    @SuppressLint("UseCompatTextViewDrawableApis")
    private fun changeFragment(
        selectedView: TextView,
        other1: TextView,
        other2: TextView,
        other3: TextView,
        fragment: androidx.fragment.app.Fragment,
        fragmentViewId: Int
    ) {
        selectedView.compoundDrawableTintList =
            ColorStateList.valueOf(ContextCompat.getColor(this, R.color.primary))
        other1.compoundDrawableTintList =
            ColorStateList.valueOf(ContextCompat.getColor(this, R.color.light_grey))
        other2.compoundDrawableTintList =
            ColorStateList.valueOf(ContextCompat.getColor(this, R.color.light_grey))
        other3.compoundDrawableTintList =
            ColorStateList.valueOf(ContextCompat.getColor(this, R.color.light_grey))
        if (supportFragmentManager.findFragmentById(fragmentViewId)?.javaClass != fragment.javaClass) {
            supportFragmentManager.beginTransaction()
                .replace(fragmentViewId, fragment)
                .commit()
        }
    }

    private fun startAnimation(
        addBtn: ImageButton,
        addIncomeBtn: ImageButton,
        addExpenseBtn: ImageButton,
        transactionBtn: ImageButton,
        main: ConstraintLayout
    ) {
        rotate45(addBtn)
        moveIncomeBtn(addIncomeBtn)
        moveExpenseBtn(addExpenseBtn)
        moveTransactionBtn(transactionBtn)
        changeBackgroundToBlue(main)
    }

    private fun reverseAnimation(
        addBtn: ImageButton,
        addIncomeBtn: ImageButton,
        addExpenseBtn: ImageButton,
        transactionBtn: ImageButton,
        main: ConstraintLayout
    ) {
        rotateBack(addBtn)
        moveBack(addIncomeBtn)
        moveBack(addExpenseBtn)
        moveBack(transactionBtn)
        reverseBackgroundColor(main)
    }

    private fun rotate45(view: ImageButton) {
        view.animate()
            .rotation(45f)
            .setDuration(300)
            .start()
    }

    private fun rotateBack(view: ImageButton) {
        view.animate()
            .rotation(0f)
            .setDuration(300)
            .start()
    }

    private fun moveIncomeBtn(view: ImageButton) {
        view.animate()
            .translationX(-130f)
            .translationY(-100f)
            .setDuration(300)
            .start()
    }

    private fun moveExpenseBtn(view: ImageButton) {
        view.animate()
            .translationX(130f)
            .translationY(-100f)
            .setDuration(300)
            .start()
    }

    private fun moveTransactionBtn(view: ImageButton) {
        view.animate()
            .translationY(-200f)
            .setDuration(300)
            .start()
    }

    private fun moveBack(view: ImageButton) {
        view.animate()
            .translationX(0f)
            .translationY(0f)
            .setDuration(300)
            .start()
    }

    private fun changeBackgroundToBlue(main: ConstraintLayout) {
        main.setBackgroundColor(ContextCompat.getColor(this, R.color.light_blue))
    }

    private fun reverseBackgroundColor(main: ConstraintLayout) {
        main.setBackgroundColor(ContextCompat.getColor(this, R.color.default_white_background))
    }

    private fun switchOnAlertsByDefault() {
        PrefsHelper.saveBudgetNotificationStatus(this, true)
    }
}