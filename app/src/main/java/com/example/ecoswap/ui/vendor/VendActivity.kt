package com.example.ecoswap.ui.vendor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ecoswap.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.txtUserEmail
import kotlinx.android.synthetic.main.activity_vend.*

class VendActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vend)

        val bundle = intent.extras
        val email = bundle?.getString("email")
        val name = bundle?.getString("name")
        //val provider = bundle?.getString("provider")
        setup(email ?: "", name ?: "Mi Pantalla")

        val prefs = getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("name", name)
        //prefs.putString("provider", provider)
        prefs.apply()
    }

    private fun setup(email: String, name: String) {
        //title = "Vendor Dashboard"
        txtUserEmail.text = email
        txtUserName.text = name
        //providerTextView.text = provider

        logOutBtn.setOnClickListener {

            val prefs =
                getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()

            FirebaseAuth.getInstance().signOut()
            val authIntent = Intent(this, AuthActivity::class.java)
            startActivity(authIntent)
        }

        addCardView.setOnClickListener {
            val addIntent = Intent(this, AddActivity::class.java).apply {
                putExtra("email", email)
                putExtra("name", name)
            }
            startActivity(addIntent)
        }

        deleteCardView.setOnClickListener {
            val deleteIntent = Intent(this, DeleteActivity::class.java).apply {
                putExtra("email", email)
                putExtra("name", name)
            }
            startActivity(deleteIntent)
        }

        qrCardView.setOnClickListener {
            val qrIntent = Intent(this, QrReaderActivity::class.java)
            startActivity(qrIntent)
        }
    }
}