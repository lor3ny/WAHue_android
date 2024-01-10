package com.lor3n.wahue

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import com.lor3n.wahue.ui.theme.ToneTheme
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import coil.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


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
        val imageUrls = mutableStateListOf<String>()

        GlobalScope.launch(Dispatchers.IO) {
            imageUrls.removeAll(imageUrls)
            val fetchedImageUrls = getAllImageUrlsFromFirebaseStorage()
            imageUrls.addAll(fetchedImageUrls)
        }

        enableEdgeToEdge()
        setContent {
            ToneTheme {
                Surface (
                    modifier = Modifier
                        .fillMaxSize(),
                    color = Color(0xFFF2F2F2)

                ){
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        Text(
                            text = auth.currentUser?.email.toString(),
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.CenterHorizontally)
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
                                        imageUrls.removeAll(imageUrls)
                                        val fetchedImageUrls = getAllImageUrlsFromFirebaseStorage()
                                        imageUrls.addAll(fetchedImageUrls)
                                    }
                                },
                                modifier = Modifier
                                    .padding(16.dp)
                            ) {
                                Text("Update")
                            }
                        }
                        Text(
                            text = imageUrls.size.toString(),
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2), // Number of columns
                            contentPadding = PaddingValues(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ){
                            items(imageUrls){ url ->
                                Box(
                                    modifier = Modifier
                                        .padding(2.dp)
                                ){
                                    Image(
                                        painter = // You can add more customization options here
                                        rememberAsyncImagePainter(
                                            ImageRequest.Builder(LocalContext.current).data(data = url)
                                                .apply(block = fun ImageRequest.Builder.() {
                                                }).build()
                                        ),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(200.dp)
                                    )
                                }

                            }
                        }
                    }
                }
            }
        }
    }


    private suspend fun getAllImageUrlsFromFirebaseStorage(): List<String> {
        val imagesRef = storage.reference.child("${auth.currentUser?.uid}/images/") // Change this to your Firebase Storage path
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
