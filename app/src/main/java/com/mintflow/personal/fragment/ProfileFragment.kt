package com.mintflow.personal.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mintflow.personal.R
import com.mintflow.personal.activity.LogInActivity
import com.mintflow.personal.activity.SettingsActivity
import com.mintflow.personal.data.PrefsHelper
import com.mintflow.personal.model.Transaction
import com.mintflow.personal.model.User

class ProfileFragment : Fragment() {
    private lateinit var user: User
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        val context = requireContext()
        val settingsBtn = view.findViewById<ConstraintLayout>(R.id.settings)
        val exportBtn = view.findViewById<ConstraintLayout>(R.id.export)
        val restoreBtn = view.findViewById<ConstraintLayout>(R.id.restore)
        val logoutBtn = view.findViewById<ConstraintLayout>(R.id.logout)
        val name = view.findViewById<TextView>(R.id.textView45)
        val email = view.findViewById<TextView>(R.id.textView46)

        val userJson = PrefsHelper.getUser(context)
        user = Gson().fromJson(userJson, User::class.java)

        name.text = user.fullName
        email.text = user.email

        settingsBtn.setOnClickListener {
            val intent = Intent(context, SettingsActivity::class.java)
            startActivity(intent)
        }
        exportBtn.setOnClickListener {
            exportTransactions(context, PrefsHelper.loadTransactions(context, user.email), user)
        }
        restoreBtn.setOnClickListener {
            val restored = restoreTransactions(context, user)
            overwriteSharedPrefsAfterRestore(context, restored)
            Toast.makeText(context, "Restore successful!", Toast.LENGTH_SHORT).show()

        }

        logoutBtn.setOnClickListener {
            PrefsHelper.removeUser(context)
            val intent = Intent(context, LogInActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }



        return view
    }

    private fun exportTransactions(context: Context, transactions: List<Transaction>, user: User) {
        val gson = Gson()
        val json = gson.toJson(transactions)
        val fileName = user.email + "_transactions_backup.json"

        try {
            context.openFileOutput(fileName, Context.MODE_PRIVATE).use { output ->
                output.write(json.toByteArray())
            }
            Toast.makeText(context, "Exported as a json file!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun restoreTransactions(context: Context, user: User): MutableList<Transaction> {
        val fileName = user.email + "_transactions_backup.json"

        return try {
            val json = context.openFileInput(fileName).bufferedReader().use { it.readText() }
            val gson = Gson()
            val type = object : TypeToken<MutableList<Transaction>>() {}.type
            gson.fromJson(json, type) ?: mutableListOf()
        } catch (e: Exception) {
            Toast.makeText(context, "Restore failed: ${e.message}", Toast.LENGTH_SHORT).show()
            mutableListOf()
        }
    }

    private fun overwriteSharedPrefsAfterRestore(
        context: Context,
        restoredList: List<Transaction>
    ) {
        PrefsHelper.saveTransactions(context, restoredList, user.email)
    }


}