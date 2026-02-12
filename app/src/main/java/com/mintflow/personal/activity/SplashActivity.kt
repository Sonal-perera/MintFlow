package com.mintflow.personal.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mintflow.personal.data.PrefsHelper
import com.mintflow.personal.model.Category


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (PrefsHelper.getUser(this) != null) {
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        } else {
            PrefsHelper.saveSelectedCountry(this, "USD")
            PrefsHelper.saveSelectedCountrySymbol(this, "$")
            startActivity(Intent(this@SplashActivity, OnBoardingActivity::class.java))
            finish()
        }
        val categoryList = PrefsHelper.loadCategories(this)
        if (categoryList.isEmpty()) {
            categoryList.add(Category("Select Category"))
            categoryList.add(Category("Food"))
            categoryList.add(Category("Transport"))
            categoryList.add(Category("Bills"))
            categoryList.add(Category("Entertainment"))
            categoryList.add(Category("Housing"))
            categoryList.add(Category("Shopping"))
            categoryList.add(Category("Health"))
            categoryList.add(Category("Education"))
            categoryList.add(Category("Personal Care"))
            categoryList.add(Category("Salary"))
            categoryList.add(Category("Savings"))
            categoryList.add(Category("Travel"))
            categoryList.add(Category("Gifts"))
            categoryList.add(Category("Pets"))
            categoryList.add(Category("Debt Payment"))
            categoryList.add(Category("Subscription"))
            PrefsHelper.saveCategories(this, categoryList)
        }

        val monthList = PrefsHelper.loadMonths(this)
        if (monthList.isEmpty()) {
            monthList.add("January")
            monthList.add("February")
            monthList.add("March")
            monthList.add("April")
            monthList.add("May")
            monthList.add("June")
            monthList.add("July")
            monthList.add("August")
            monthList.add("September")
            monthList.add("October")
            monthList.add("November")
            monthList.add("December")
            PrefsHelper.saveMonths(this, monthList)
        }
    }
}