package com.example.paintapp

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.paintapp.databinding.ActivityMainBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import yuku.ambilwarna.AmbilWarnaDialog
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var fileName: String? = null
    private var defaultColor: Int = 0

    private val path: File =
        File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath + "/myPaintings")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        askPermission()

        val dataFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val date:String = dataFormat.format(Date())

        fileName = "$path/image_$date.png"

        if (!path.exists()){
            path.mkdirs()
        }

        defaultColor = ContextCompat.getColor(this@MainActivity, R.color.black)

        seekBarListener()

        binding.btnEraser.setOnClickListener {
           binding.signatureView.clearCanvas()
        }

        binding.btnSave.setOnClickListener {
            if (!binding.signatureView.isBitmapEmpty){
                saveImage()
            }
        }

        binding.btnColor.setOnClickListener {
            openColorPicker()
        }
    }

    private fun saveImage() {
        Log.d("##################", "saveImage: $fileName nima bu akalar")
        val file = File(fileName)
        val bitmap:Bitmap = binding.signatureView.signatureBitmap
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG,0,bos)
        val bitmapData: ByteArray = bos.toByteArray()
        val fos = FileOutputStream(file)
        fos.write(bitmapData)
        fos.flush()
        fos.close()
        Toast.makeText(this@MainActivity, "Painting Saved", Toast.LENGTH_SHORT).show()
    }


    private fun seekBarListener() {
        binding.penSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(p0: SeekBar?, i: Int, p2: Boolean) {
                binding.textPenSize.text = "${i}dp"
                binding.signatureView.penSize = i.toFloat()
                binding.penSize.max = 50
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                //todo
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                //todo
            }

        })
    }


    private fun openColorPicker() {
        val warnDialog = AmbilWarnaDialog(
            this@MainActivity,
            defaultColor,
            object : AmbilWarnaDialog.OnAmbilWarnaListener {
                override fun onCancel(dialog: AmbilWarnaDialog?) {

                }

                override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                    defaultColor = color
                    binding.signatureView.penColor = color
                }
            })
        warnDialog.show()
    }

    private fun askPermission() {
        Dexter.withContext(this).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                Toast.makeText(this@MainActivity, "Granted !!!", Toast.LENGTH_SHORT).show()
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: MutableList<PermissionRequest>?,
                p1: PermissionToken?
            ) {
                p1!!.continuePermissionRequest()
            }
        }).check()
    }

}