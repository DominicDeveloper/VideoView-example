package com.asadbek.videoplayer

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class MainActivity2 : AppCompatActivity() {
    lateinit var videoView:VideoView
    lateinit var btnChoose:Button
    lateinit var btnUpload:Button
    var videoUri:Uri? = null // telefonda joylashgan videoning uri manzili
    lateinit var mediaController: MediaController
    lateinit var storageReference: StorageReference
    lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        videoView = findViewById(R.id.videoView)
        btnChoose = findViewById(R.id.btnChooseVideo)
        btnUpload = findViewById(R.id.btnUploadVideo)

        storageReference = FirebaseStorage.getInstance().getReference()
        databaseReference = FirebaseDatabase.getInstance().getReference("videos")

        mediaController = MediaController(this)

        // mediaController ni videoview bilan bog`lash
        videoView.setMediaController(mediaController)
        videoView.start()


        // videoni tanlash
        btnChoose.setOnClickListener {
            requestPermission()
        }
        //videoni yuborish
        btnUpload.setOnClickListener {
            processVideoUploading()
        }
    }

    private fun processVideoUploading() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Videoni yuborish")
        progressDialog.show()
        // uploader - video qanday nomda va formatda saqlanishi
        val uploader = storageReference.child("videos/"+System.currentTimeMillis()+"."+getExtenstion())
        // video ni uri bo`yicha firebasega yuborish
        uploader.putFile(videoUri!!)
            .addOnSuccessListener {
                // video yuborilib bo`lgandan so`ng realtimedatabase ga fayl modelini yuborish
                uploader.downloadUrl.addOnSuccessListener {
                    val key = databaseReference.push().key!!
                    val videoFile = VideoFile(key,it.toString())
                    databaseReference.child(key).setValue(videoFile)
                        .addOnSuccessListener {
                            progressDialog.dismiss()
                            Toast.makeText(this, "Yuborildi!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            progressDialog.dismiss()
                            Toast.makeText(this, "Video yuborilmadi!", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnProgressListener {
                val pros:Float = ((100*it.bytesTransferred)/it.totalByteCount).toFloat()
                progressDialog.setMessage("Yuklandi: "+pros.toInt()+"%")

            }
    }

    private fun getExtenstion():String{
        var mimeTypeMap:MimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(videoUri!!))!!
    }
    // TIRAMSi - api version 33 ga teng yoki katta bo`lganda
    private fun requestStoragePermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_MEDIA_VIDEO),101)
        }else{
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.READ_MEDIA_VIDEO),101)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101){
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getVideo()
            }else{
                Toast.makeText(this, "Xotirani o`qishga ruxsat bering!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getVideo() {
        val intent = Intent()
        intent.setType("video/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(intent,303)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 303 && resultCode == RESULT_OK){
            videoView.setVideoURI(videoUri) // bu bilan videovidew da video chiqishni boshlaydi
        }

        if (requestCode == 1012){
            if (SDK_INT == Build.VERSION_CODES.R){
                if (Environment.isExternalStorageManager()){
                    getVideo()
                }else{
                    Toast.makeText(this, "Allow permission for storage access", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun requestPermission(){
        if (SDK_INT >= Build.VERSION_CODES.R){
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.setData(Uri.parse(String.format("package:%s",applicationContext.packageName)))
                startActivityForResult(intent,1012)
            }catch (e:Exception){
                val intent = Intent()
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivityForResult(intent,1012)
            }
        }else{
            // android versiya 11 yoki baland bo`lsa
            ActivityCompat.requestPermissions(this@MainActivity2, arrayOf<String>(WRITE_EXTERNAL_STORAGE),101)
        }
    }
}