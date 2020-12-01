package com.example.ecoswap.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.ecoswap.R
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_add.*
import kotlin.collections.HashMap

class AddActivity : AppCompatActivity() {
    private lateinit var ref: DatabaseReference
    private lateinit var data: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        ref = Firebase.database.reference.child("Products")
        data = FirebaseDatabase.getInstance().reference.child("Users")

        val bundle = intent.extras
        val email = bundle?.getString("email")
        val name = bundle?.getString("name")
        val id = bundle?.getString("id")
        setup(email ?: "",name ?: "", id ?: "")

        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("name", name)
        prefs.apply()
    }

    private fun setup(email: String, name: String, id: String) {
        //title = "Vendor Dashboard"
        //providerTextView.text = provider

        saveButton.setOnClickListener {
            val id = idTextView.text.toString()
            val name = productTextView.text.toString()
            val quantity = quantityTextView.text.toString()
            val points = pointsTextView.text.toString()

            ref.child("Products").addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postSnapshot in snapshot.children.toString())
                        if (postSnapshot.equals(id)) {
                            Toast.makeText(this@AddActivity, "El ID del producto no esta disponible", Toast.LENGTH_LONG).show()
                            
                        } else {
                            val map: HashMap<String, Any> = HashMap<String, Any>()
                            map["Name"] = name
                            map["Quantity"] = quantity
                            map["Points"] = points
                            ref.child(id).updateChildren(map)
                            Toast.makeText(this@AddActivity, "Producto subido con exito", Toast.LENGTH_LONG).show()
                            break
                        }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

            idTextView.text.clear()
            productTextView.text.clear()
            quantityTextView.text.clear()
            pointsTextView.text.clear()
        }

        returnButton.setOnClickListener {
            val addIntent = Intent(this, VendActivity::class.java).apply {
                putExtra("email", email)
                putExtra("name", name)
            }
            startActivity(addIntent)
        }

    }
}