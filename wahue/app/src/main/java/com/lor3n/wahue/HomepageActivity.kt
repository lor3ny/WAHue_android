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
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.google.firebase.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import coil.compose.rememberImagePainter
import java.io.File


class HomepageActivity : ComponentActivity() {

    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth
    private var bitmap: Bitmap? = null


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
                val viewModel = viewModel<MainViewModel>()
                val bitmaps by viewModel.bitmaps.collectAsState()

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

                            },
                            modifier = Modifier
                                .padding(16.dp)
                        ) {
                            Text("Update")
                        }
                    }
                    Column {
                        val storageRef = storage.getReferenceFromUrl("gs://wahue-8d9b5.appspot.com/test.png")
                        val localFile = File.createTempFile("images", "jpg")

                        storageRef.getFile(localFile)
                            .addOnSuccessListener {

                            }
                            .addOnFailureListener { exception ->
                            }
                        DisplayImage(imageUrl = localFile.path)
                    }
                }
            }
        }
    }


    @Composable
    fun DisplayImage(imageUrl: String) {
        val painter: Painter = rememberImagePainter(data = imageUrl)
        Image(
            painter = painter,
            contentDescription = "Image from Firebase"
        )
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
