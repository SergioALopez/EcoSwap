package com.example.ecoswap.ui.admin

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ecoswap.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*

class AdminActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        val bundle = intent.extras
        val email = bundle?.getString("email")
        val name = bundle?.getString("name")
        //val provider = bundle?.getString("provider")
        setup(email ?: "", name ?: "Mi pantalla")

        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("name", name)
        //prefs.putString("provider", provider)
        prefs.apply()
    }

    private fun setup(email: String, name: String) {
        //title = "Admin Dashboard"
        txtUserEmail.text = email
        txtUserName.text = name
        //providerTextView.text = provider

        logOutButton.setOnClickListener {

            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()

            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }
    }
}