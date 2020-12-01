package com.example.ecoswap.ui.user

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecoswap.MainAdapter
import com.example.ecoswap.R
import com.example.ecoswap.ui.EditActivity
import com.example.ecoswap.viewmodel.DeleteViewModel
import kotlinx.android.synthetic.main.activity_delete.*
import kotlinx.android.synthetic.main.activity_home.*

class PromotionActivity : AppCompatActivity(), MainAdapter.OnItemClickListener {
    private lateinit var adapter: MainAdapter
    private val viewModel by lazy { ViewModelProviders.of(this).get(DeleteViewModel::class.java) }
    private lateinit var points: String
    private lateinit var id: String
    private lateinit var quantity: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promotion)
        adapter = MainAdapter(this, this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        observeData()

        val bundle = intent.extras
        val email = bundle?.getString("email")
        val name = bundle?.getString("name")
        id = bundle?.getString("id").toString()
        points = bundle?.getString("points").toString()
        setup(email ?: "",name ?: "", id ?: "", points ?: "")

        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("name", name)
        prefs.apply()

    }

    private fun setup(email: String, name: String, id: String, points: String) {

    }

    override fun onItemClick(position: Int) {
        quantity = adapter.getProductQuantity(position)
        if (quantity != "0") {
            if (adapter.rewardAvailable(position, points.toInt())) {
                Toast.makeText(this, "El item $position puede consumirlo", Toast.LENGTH_SHORT)
                    .show()

                viewModel.updatePointsData(
                    id,
                    adapter.getIdOnList(position),
                    adapter.updatePointsValue(position, points.toInt()),
                    adapter.updateQuantityValue(position, quantity.toInt())
                )
            } else {
                Toast.makeText(
                    this,
                    "No tiene los puntos suficientes para este premio",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(
                this,
                "Lo sentimos, se agotaron existencias de este cupon",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun observeData() {
        viewModel.fetchProductData().observe(this, Observer {
            adapter.setListData(it)
            adapter.notifyDataSetChanged()
        })
    }
}