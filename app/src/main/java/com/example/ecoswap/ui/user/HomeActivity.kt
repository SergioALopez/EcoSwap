package com.example.ecoswap.ui.user

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ecoswap.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_add.*
import kotlinx.android.synthetic.main.activity_home.*

enum class ProviderType {
    BASIC,
    GOOGLE
}

class HomeActivity : AppCompatActivity() {
    //private lateinit var ref: DatabaseReference
    private lateinit var data: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        //ref = Firebase.database.reference.child("Products")
        data = FirebaseDatabase.getInstance().reference.child("Users")

        // Setup
        val bundle = intent.extras
        val email = bundle?.getString("email")
        val name = bundle?.getString("name")
        val id = bundle?.getString("id")
        setup(email ?: "", name ?: "", id ?: "")

        // Guardado de datos
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("name", name)
        prefs.putString("id", id)
        prefs.apply()

        /*
        ref.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                
                TODO("Not yet implemented")
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        */
    }

    private fun setup(email: String, name: String, id: String) {

        //title = "Inicio"
        txtUserEmail.text = email
        txtUserName.text = name
        //providerTextView.text = provider

        data.child(id).addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val points = snapshot.child("Points").value.toString()

                txtUserPoints.text = points
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        logOutButton.setOnClickListener {

            // Borrado de datos
            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()

            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }

        keyCardView.setOnClickListener {
            val points = txtUserPoints.text.toString()
            val new = points.toInt() + 100
            /*
            data.child(id).addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    points = snapshot.child("Points").value.toString()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

             */

            data.child(id).child("Points").setValue(new)
        }

        promotionCardView.setOnClickListener {

        }

        rewardCardView.setOnClickListener {

        }

    }
}