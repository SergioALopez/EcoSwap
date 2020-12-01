package com.example.ecoswap

import android.content.Context
import android.view.LayoutInflater
import android.view.OrientationEventListener
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_home.view.*
import kotlinx.android.synthetic.main.item_row.view.*

class MainAdapter(private val context: Context, private val listener: OnItemClickListener): RecyclerView.Adapter<MainAdapter.MainViewHolder>() {

    private var dataList = mutableListOf<Product>()

    fun setListData(data: MutableList<Product>) {
        dataList = data
    }

    fun getIdOnList(position: Int): String {
        return dataList[position].id
    }

    fun getProductQuantity(position: Int): String {
        return dataList[position].quantity
    }

    fun rewardAvailable(position: Int, points: Int): Boolean {
        val value = dataList[position].points.toInt()
        return points >= value
    }

    fun updateQuantityValue(position: Int, quantity: Int): String {
        val value = dataList[position].quantity.toInt()
        return (value - 1).toString()
    }

    fun updatePointsValue(position: Int, points: Int): String {
        val value = dataList[position].points.toInt()
        return (points - value).toString()
    }

    fun deleteListData(position: Int) {
        dataList.removeAt(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_row, parent, false)
        return MainViewHolder(view)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val product = dataList[position]
        holder.bindView(product)
    }

    override fun getItemCount(): Int {
        return if (dataList.size > 0) {
            dataList.size
        } else {
            0
        }
    }

    inner class MainViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener {

        fun bindView(product: Product) {
            itemView.txtName.text = "Nombre prod: ${product.name}"
            itemView.txtQuantity.text = "Cantidad disponible: ${product.quantity}"
            itemView.txtProductPoints.text = "Cantidad de puntos: ${product.points}"
            itemView.txtId.text = product.id
        }

        init {
            itemView.setOnClickListener(this)
            itemView.imageDelete.setOnClickListener {
                val position = adapterPosition
                if(position != RecyclerView.NO_POSITION) {
                    listener.onDeleteClick(position)
                }
            }
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if(position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }

    }

    interface OnItemClickListener{
        fun onItemClick(position: Int) {}

        fun onDeleteClick(position: Int){}
    }
}