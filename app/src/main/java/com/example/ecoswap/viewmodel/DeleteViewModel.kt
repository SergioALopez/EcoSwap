package com.example.ecoswap.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ecoswap.Product
import com.example.ecoswap.domain.Repo

class DeleteViewModel: ViewModel() {
    private val repo = Repo()

    fun fetchProductData(): LiveData<MutableList<Product>> {
        val mutableData = MutableLiveData<MutableList<Product>>()
        repo.getProductData().observeForever { productList ->
            mutableData.value = productList
        }
        return mutableData
    }

    fun deleteProductData(id: String) {
        repo.deleteData(id)
    }

    fun updatePointsData(userId: String, productId: String, points: String, quantity: String) {
        repo.updatePoints(userId, productId, points, quantity)
    }
}