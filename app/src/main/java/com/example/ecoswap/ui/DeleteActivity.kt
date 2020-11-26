package com.example.ecoswap.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecoswap.viewmodel.DeleteViewModel
import com.example.ecoswap.MainAdapter
import com.example.ecoswap.R
import kotlinx.android.synthetic.main.activity_delete.*

class DeleteActivity : AppCompatActivity(), MainAdapter.OnItemClickListener {

    private lateinit var adapter: MainAdapter
    private val viewModel by lazy { ViewModelProviders.of(this).get(DeleteViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete)

        adapter = MainAdapter(this, this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        observeData()

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

    }

    override fun onItemClick(position: Int) {
        Toast.makeText(this, "Item $position", Toast.LENGTH_SHORT).show()
        val id = adapter.getIdOnList(position)
        val editIntent = Intent(this, EditActivity::class.java).apply {
            putExtra("id", id)
        }
        startActivity(editIntent)
    }

    override fun onDeleteClick(position: Int) {
        Toast.makeText(this, "Delete item $position", Toast.LENGTH_SHORT).show()
        viewModel.deleteProductData(adapter.getIdOnList(position))
        adapter.deleteListData(position)
        adapter.notifyItemRemoved(position)
    }

    fun observeData() {
        viewModel.fetchProductData().observe(this, Observer {
            adapter.setListData(it)
            adapter.notifyDataSetChanged()
        })
    }
}