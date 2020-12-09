package com.example.ecoswap.ui.vendor

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.SparseArray
import android.view.SurfaceHolder
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.util.isNotEmpty
import androidx.lifecycle.ViewModelProviders
import com.example.ecoswap.MainAdapter
import com.example.ecoswap.R
import com.example.ecoswap.viewmodel.DeleteViewModel
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_qr_reader.*
import java.lang.Exception

class QrReaderActivity : AppCompatActivity() {

    private val requestCodeCameraPermission = 1001
    private lateinit var cameraSource: CameraSource
    private lateinit var detector: BarcodeDetector

    private lateinit var adapter: MainAdapter
    private val viewModel by lazy { ViewModelProviders.of(this).get(DeleteViewModel::class.java) }
    private lateinit var points: String
    private lateinit var id: String
    private lateinit var quantity: String
    private lateinit var data: DatabaseReference
    private lateinit var ref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_reader)
        data = FirebaseDatabase.getInstance().reference.child("Users")
        ref = Firebase.database.reference.child("Products")

        if (ContextCompat.checkSelfPermission(this@QrReaderActivity,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            askForCameraPermission()
        } else {
            setUpControls()
        }
    }

    private fun setUpControls() {
        detector = BarcodeDetector.Builder(this@QrReaderActivity).build()
        cameraSource = CameraSource.Builder(this@QrReaderActivity, detector).setAutoFocusEnabled(true).build()
        cameraSurfaceView.holder.addCallback(surfaceCallBack)
        detector.setProcessor(processor)
    }

    private fun askForCameraPermission() {
        ActivityCompat.requestPermissions(this@QrReaderActivity, arrayOf(Manifest.permission.CAMERA), requestCodeCameraPermission)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestCodeCameraPermission && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setUpControls()
            } else {
                Toast.makeText(applicationContext, "Permiso Denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val surfaceCallBack = object: SurfaceHolder.Callback {
        override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
            try {
                if (ActivityCompat.checkSelfPermission(
                        this@QrReaderActivity,
                        Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    askForCameraPermission()
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                cameraSource.start(surfaceHolder)
            } catch (e: Exception) {
                Toast.makeText(applicationContext, "Algo malo ocurrio con la camara", Toast.LENGTH_SHORT).show()
            }
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            cameraSource.stop()
        }
    }

    private val processor = object: Detector.Processor<Barcode> {
        override fun release() {

        }

        override fun receiveDetections(detections: Detector.Detections<Barcode>) {
            if (detections != null) {
                val qrCodes: SparseArray<Barcode> = detections.detectedItems
                val code = qrCodes.valueAt(0)
                val res = code.displayValue.toString()
                val array = res.split(",")
                txtScanResult.text = array[4]

                viewModel.updatePointsData(
                    array[0],
                    array[1],
                    array[2],
                    array[3]
                )
            } else {
                txtScanResult.text = ""
            }
        }
    }



}