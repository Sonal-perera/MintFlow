package com.mintflow.personal.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mintflow.personal.model.Transaction
import androidx.core.content.edit
import com.mintflow.personal.model.Budget
import com.mintflow.personal.model.CategoryBudget
import com.mintflow.personal.model.Category
import com.mintflow.personal.model.User

object PrefsHelper {
    private const val PREF_NAME = "mint_flow_prefs"
    private const val USER = "user"
    private const val USERS = "users"
    private const val MONTHS = "months"
    private const val CATEGORIES = "categories"
    private const val CATEGORY_BUDGET = "categoryBudget"
    private const val BUDGET = "monthlyBudget"
    private const val TRANSACTIONS = "transactions"
    private const val SELECTED_COUNTRY = "selected_country"
    private const val BUDGET_NOTIFY = "budget_notify"
    private const val REMINDER_NOTIFY = "reminder_notify"
    private const val SELECTED_COUNTRY_SYMBOL = "selected_country_symbol"

    fun saveUser(context: Context, user: String) { //save the logged user
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit() { putString(USER, user) }
    }

    fun saveUsers(context: Context, userList: List<User>) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit() {
            val gson = Gson()
            val json = gson.toJson(userList)
            putString(USERS, json)
        }
    }

    fun saveSelectedCountry(context: Context, countryCode: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit() { putString(SELECTED_COUNTRY, countryCode) }
    }

    fun saveBudgetNotificationStatus(context: Context, status: Boolean) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit() { putBoolean(BUDGET_NOTIFY, status) }
    }

    fun saveReminderStatus(context: Context, status: Boolean) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit() { putBoolean(REMINDER_NOTIFY, status) }
    }

    fun saveSelectedCountrySymbol(context: Context, countrySymbol: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit() { putString(SELECTED_COUNTRY_SYMBOL, countrySymbol) }
    }

    fun saveTransactions(
        context: Context,
        transactionList: List<Transaction>,
        userEmail: String
    ) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit() {
            val gson = Gson()
            val json = gson.toJson(transactionList)
            putString(TRANSACTIONS + "_" + userEmail, json)
        }
    }

    fun saveCategoryBudgets(
        context: Context,
        categoryBudgets: List<CategoryBudget>,
        userEmail: String
    ) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit() {
            val json = Gson().toJson(categoryBudgets)
            putString(CATEGORY_BUDGET + "_" + userEmail, json)
        }
    }

    fun saveBudget(context: Context, monthlyBudget: List<Budget>, userEmail: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit() {
            val json = Gson().toJson(monthlyBudget)
            putString(BUDGET + "_" + userEmail, json)
        }
    }

    fun saveCategories(context: Context, categoryList: List<Category>) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit() {
            val gson = Gson()
            val json = gson.toJson(categoryList)
            putString(CATEGORIES, json)
        }
    }

    fun saveMonths(context: Context, monthList: List<String>) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit() {
            val gson = Gson()
            val json = gson.toJson(monthList)
            putString(MONTHS, json)
        }
    }

    fun updateTransaction(context: Context, updatedTxn: Transaction, userEmail: String) {
        val transactions = loadTransactions(context, userEmail).toMutableList()
        val index = transactions.indexOfFirst { it.id == updatedTxn.id }
        if (index != -1) {
            transactions[index] = updatedTxn
            saveTransactions(context, transactions, userEmail)
        }
    }

    fun updateAllBudgetSpent(context: Context, userEmail: String) {
        val budgets = loadCategoryBudgets(context, userEmail)

        val allTxns = loadTransactions(context, userEmail)

        val cal = android.icu.util.Calendar.getInstance()

        budgets.forEach { budget ->
            val spent = allTxns
                .asSequence()
                .filter { txn ->
                    if (txn.type != "Expense" || txn.category != budget.category) return@filter false

                    cal.time = txn.date
                    val txnMonth = cal.get(android.icu.util.Calendar.MONTH)      // 0–11
                    val txnYear = cal.get(android.icu.util.Calendar.YEAR)

                    txnMonth == budget.month && txnYear == budget.year
                }
                .sumOf { it.price }

            budget.spent = spent
        }

        saveCategoryBudgets(context, budgets, userEmail)
    }

    fun getNotificationStatus(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(BUDGET_NOTIFY, false)
    }

    fun getReminderStatus(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(REMINDER_NOTIFY, false)
    }

    fun getSelectedCountry(context: Context): String? {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(SELECTED_COUNTRY, null)
    }

    fun getSelectedCountrySymbol(context: Context): String? {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(SELECTED_COUNTRY_SYMBOL, null)
    }

    fun getUser(context: Context): String? {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(USER, null)
    }

    fun getUsers(context: Context): MutableList<User> {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString(USERS, null)
        val type = object : TypeToken<MutableList<User>>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()
    }

    fun loadTransactions(context: Context, userEmail: String): MutableList<Transaction> {  //done
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString(TRANSACTIONS + "_" + userEmail, null)
        val type = object : TypeToken<MutableList<Transaction>>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()
    }

    fun loadCategories(context: Context): MutableList<Category> {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString(CATEGORIES, null)
        val type = object : TypeToken<MutableList<Category>>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()
    }

    fun loadMonths(context: Context): MutableList<String> {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString(MONTHS, null)
        val type = object : TypeToken<MutableList<String>>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()
    }


    fun loadCategoryBudgets(context: Context, userEmail: String): MutableList<CategoryBudget> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(CATEGORY_BUDGET + "_" + userEmail, null)
        val type = object : TypeToken<MutableList<CategoryBudget>>() {}.type
        return Gson().fromJson(json, type) ?: mutableListOf()
    }

    fun loadBudgets(context: Context, userEmail: String): MutableList<Budget> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(BUDGET + "_" + userEmail, null)
        val type = object : TypeToken<MutableList<Budget>>() {}.type
        return Gson().fromJson(json, type) ?: mutableListOf()
    }


    fun removeUser(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit() { remove(USER) }
    }

    fun clearAllData(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()

    }

}