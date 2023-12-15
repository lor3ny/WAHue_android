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
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.annotation.ExperimentalCoilApi
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File


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
                val imageUrls = remember { mutableStateListOf<String>() }

                Surface (
                    modifier = Modifier
                        .background(color = Color.White)
                        .fillMaxSize()
                ){
                    Column(
                        //modifier = Modifier.verticalScroll()
                    ){
                        Text(
                            text = auth.currentUser?.email.toString(),
                            textAlign = TextAlign.Center
                        )
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
                                    GlobalScope.launch(Dispatchers.IO) {
                                        val fetchedImageUrls = getAllImageUrlsFromFirebaseStorage()
                                        imageUrls.addAll(fetchedImageUrls)
                                    }
                                },
                                modifier = Modifier
                                    .padding(16.dp)
                            ) {
                                Text("Update")
                            }
                            Text(
                                text = imageUrls.size.toString(),
                                modifier = Modifier
                                    .padding(16.dp)
                            )
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            for (imageUrl in imageUrls) {
                                Image(
                                    painter = // You can add more customization options here
                                    rememberAsyncImagePainter(
                                        ImageRequest.Builder(LocalContext.current).data(data = imageUrl)
                                            .apply(block = fun ImageRequest.Builder.() {
                                                // You can add more customization options here
                                                //error(Color.Red)
                                            }).build()
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .height(200.dp)
                                        .fillMaxWidth(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
            }
        }
    }


    suspend fun getAllImageUrlsFromFirebaseStorage(): List<String> {
        val imagesRef = storage.reference.child("images") // Change this to your Firebase Storage path
        println("Starting Firebase retreving")
        return try {
            val imageUrls = mutableListOf<String>()
            val listResult = imagesRef.listAll().await()
            listResult.items.forEach { imageRef ->
                val downloadUrl = imageRef.downloadUrl.await().toString()
                imageUrls.add(downloadUrl)
            }
            imageUrls
        } catch (e: Exception) {
            // Handle any exceptions here
            emptyList()
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
