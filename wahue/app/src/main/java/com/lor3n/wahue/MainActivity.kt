package com.lor3n.wahue

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.lor3n.wahue.ui.theme.ToneTheme
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import com.google.firebase.Firebase
import com.google.firebase.initialize
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.component1
import com.google.firebase.storage.component2
import com.google.firebase.storage.component3
import com.google.firebase.storage.storage
import com.google.firebase.storage.storageMetadata


class HomepageActivity : ComponentActivity() {

    lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!hasRequiredPermissions()) {
            ActivityCompat.requestPermissions(
                this, CAMERAX_PERMISSIONS, 0
            )
        }

        storage = Firebase.storage

        setContent {
            ToneTheme {
                val bitmaps = MutableStateFlow<List<Bitmap>>(emptyList())
                Homepage(bitmaps)
            }
        }
    }

    @Composable
    private fun Homepage(
        photos: MutableStateFlow<List<Bitmap>>
    ){
        Surface (
            modifier = Modifier
                .background(color = Color.White)
                .fillMaxSize()
        ){
            Column (
                modifier = Modifier
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                Button(
                    onClick = {
                        val intent = Intent(this@HomepageActivity, CameraActivity::class.java)
                        startActivity(intent)
                    },
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    Text("Camera")
                }
                Button(
                    onClick = {
                        //val intent = Intent(this@MainActivity, GalleryActivity::class.java)
                        //startActivity(intent)
                    },
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    Text("Gallery")
                }
                Button(
                    onClick = {
                        /*Download photo from Firebase*/
                    },
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    Text("Download from firebase")
                }
            }
        }
    }

    private fun hasRequiredPermissions(): Boolean{
        return CAMERAX_PERMISSIONS.all{
            ContextCompat.checkSelfPermission(
                applicationContext,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object{
        private val CAMERAX_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}
