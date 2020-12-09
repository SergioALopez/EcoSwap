package com.example.ecoswap.ui.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.ecoswap.R
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.android.synthetic.main.activity_cupon.*

class CuponActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cupon)

        val bundle = intent.extras
        val userId = bundle?.getString("userId")
        val productId = bundle?.getString("productId")
        val points = bundle?.getString("points")
        val quantity = bundle?.getString("quantity")
        val productName = bundle?.getString("productName")
        Toast.makeText(this, "El id es: $productId", Toast.LENGTH_LONG).show()
        val code = "$userId,$productId,$points,$quantity,$productName"
        generateQrCode(code)
    }

    private fun generateQrCode(id: String) {
        val multiFormatWriter = MultiFormatWriter()

        try {
            val bitMatrix = multiFormatWriter.encode(id, BarcodeFormat.QR_CODE, 250, 250)
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.createBitmap(bitMatrix)
            qrImageView.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }

}