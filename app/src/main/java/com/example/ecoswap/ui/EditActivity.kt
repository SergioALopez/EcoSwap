package com.example.ecoswap.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.ecoswap.R
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_add.*
import kotlinx.android.synthetic.main.activity_add.idTextView
import kotlinx.android.synthetic.main.activity_add.pointsTextView
import kotlinx.android.synthetic.main.activity_add.productTextView
import kotlinx.android.synthetic.main.activity_add.quantityTextView
import kotlinx.android.synthetic.main.activity_add.returnButton
import kotlinx.android.synthetic.main.activity_add.saveButton
import kotlinx.android.synthetic.main.activity_edit.*

class EditActivity : AppCompatActivity() {

    private lateinit var ref: DatabaseReference
    private lateinit var data: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        ref = Firebase.database.reference.child("Products")
        data = FirebaseDatabase.getInstance().reference.child("Users")

        val bundle = intent.extras
        val email = bundle?.getString("email")
        val name = bundle?.getString("name")
        val id = bundle?.getString("id")
        setup(email ?: "",name ?: "", id!!)

        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("name", name)
        prefs.apply()


    }

    private fun setup(email: String, name: String, id: String) {
        //title = "Vendor Dashboard"
        //providerTextView.text = provider
        if (id.isEmpty()){
            idTextView.isEnabled = true
        } else {
            idTextView.setText(id)
            idTextView.isEnabled = false
        }

        ref.child(id).addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val id = snapshot.key.toString()
                val prodName = snapshot.child("Name").value.toString()
                val quantity = snapshot.child("Quantity").value.toString()
                val points = snapshot.child("Points").value.toString()

                idTextView.setText(id)
                productTextView.setText(prodName)
                quantityTextView.setText(quantity)
                pointsTextView.setText(points)
                }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        saveButton.setOnClickListener {
            val id = idTextView.text.toString()
            val name = productTextView.text.toString()
            val quantity = quantityTextView.text.toString()
            val points = pointsTextView.text.toString()

            ref.child("Products").addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postSnapshot in snapshot.children.toString())
                        if (name.isEmpty() && quantity.isEmpty() && points.isEmpty()) {
                            Toast.makeText(this@EditActivity, "Debe llenar todos los espacios", Toast.LENGTH_LONG).show()

                        } else {
                            val map: HashMap<String, Any> = HashMap<String, Any>()
                            map["Name"] = name
                            map["Quantity"] = quantity
                            map["Points"] = points
                            ref.child(id).updateChildren(map)
                            Toast.makeText(this@EditActivity, "Producto subido con exito", Toast.LENGTH_LONG).show()
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
            onBackPressed()
            /*
            val addIntent = Intent(this, VendActivity::class.java).apply {
                putExtra("email", email)
                putExtra("name", name)
            }
            startActivity(addIntent)

             */
        }

    }
}