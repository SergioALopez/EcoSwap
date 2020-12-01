package com.example.ecoswap.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ecoswap.Product
import com.google.firebase.database.*
import java.util.jar.Attributes

class Repo {
    private lateinit var database: DatabaseReference
    private lateinit var userDatabase: DatabaseReference

    fun getProductData():LiveData<MutableList<Product>> {
        val mutableData = MutableLiveData<MutableList<Product>>()
        database = FirebaseDatabase.getInstance().reference.child("Products")
        database.addValueEventListener(object: ValueEventListener {
            val listData = mutableListOf<Product>()

            override fun onDataChange(snapshot: DataSnapshot) {
                if (listData.isEmpty()){}
                else{
                    listData.clear()
                }
                for (document in snapshot.children) {
                    val id = document.key.toString()
                    val name = document.child("Name").value.toString()
                    val points = document.child("Points").value.toString()
                    val quantity = document.child("Quantity").value.toString()
                    val product = Product(name!!, points!!, quantity!!, id!!)
                    listData.add(product)
                }
                mutableData.value = listData
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        return mutableData
    }

    fun updatePoints(userId: String, productId: String, points: String, quantity: String){
        userDatabase = FirebaseDatabase.getInstance().reference.child("Users")
        database = FirebaseDatabase.getInstance().reference.child("Products")

        database.child(productId).child("Quantity").setValue(quantity)
        userDatabase.child(userId).child("Points").setValue(points)
    }

    fun deleteData(id: String) {
        database = FirebaseDatabase.getInstance().reference.child("Products")
        database.child(id).removeValue()
    }
}