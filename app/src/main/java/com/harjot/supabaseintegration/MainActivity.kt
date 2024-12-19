package com.harjot.supabaseintegration

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.harjot.supabaseintegration.databinding.ActivityMainBinding
import com.russhwolf.settings.Settings
import io.github.jan.supabase.SupabaseClient

class MainActivity : AppCompatActivity() {
    val pickImageRequest = 1
    val permissionRequestCode = 100
    val externalStorageRequestCode = 101
    lateinit var supabaseClient: SupabaseClient
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        supabaseClient = (applicationContext as MyApplication).supabaseClient
        checkAndRequestPermission()
        binding.btn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent,pickImageRequest)
        }
    }

    private fun checkAndRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                if (Environment.isExternalStorageManager()){
                    //permission granted, proceed
                }else{
                    //ask for permission
                    requestManageExternalStoragePermission()
                }
            }else{
                if(ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    requestManageExternalStoragePermission()
                }
            }
        }else{
            if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), permissionRequestCode)
            }
        }
    }

    private fun requestManageExternalStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            try{
                val intent = Intent(android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                startActivityForResult(intent,permissionRequestCode)
            }catch (e:ActivityNotFoundException){
                Toast.makeText(this, "Activity not Found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            permissionRequestCode->{
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "granted", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this, "denied", Toast.LENGTH_SHORT).show()
                }
            }
            externalStorageRequestCode ->{
                if (Environment.isExternalStorageManager()){
                    Toast.makeText(this, "full storage access granted", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this, "permission not granted", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == pickImageRequest){
            data?.data?.let { uri->
                binding.ivImage.setImageURI(uri)
                uploadImageToSupabase(uri)
            }
        }
    }

    private fun uploadImageToSupabase(uri: Uri) {
        val byteArray = uriToByteArray(this,uri)
        val filename = ""
    }

    private fun uriToByteArray(context: Context, uri: Uri){

    }
}