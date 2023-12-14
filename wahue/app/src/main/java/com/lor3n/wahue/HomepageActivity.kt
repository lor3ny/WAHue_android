package com.lor3n.wahue

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lor3n.wahue.ui.theme.ToneTheme
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.widget.ImageView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import com.google.firebase.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth


class HomepageActivity : ComponentActivity() {

    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!hasRequiredPermissions()) {
            ActivityCompat.requestPermissions(
                this, CAMERAX_PERMISSIONS, 0
            )
        }

        auth = Firebase.auth
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
            Row (
                modifier = Modifier
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Top
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
            Column {

            }
        }
    }

    private fun DonwloadUserImages(){
        //
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
