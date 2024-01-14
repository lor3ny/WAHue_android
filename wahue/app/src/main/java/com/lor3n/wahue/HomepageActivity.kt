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
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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

data class ImageHue(var image: ImageBitmap?, var hue: ImageBitmap?, val code: Int)
class HomepageActivity : ComponentActivity() {

    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth
    private var ImagesAndHues = mutableStateListOf<ImageHue>()
    private var hueBitmaps = mutableStateListOf<ImageBitmap>()
    private var selectedImage: ImageHue? = null


    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!hasRequiredPermissions()) {
            ActivityCompat.requestPermissions(
                this, CAMERAX_PERMISSIONS, 0
            )
        }

        auth = Firebase.auth
        storage = Firebase.storage
        enableEdgeToEdge()
        setContent {

            var showImageSelected by remember { mutableStateOf(false) }

            ToneTheme {
                if(!showImageSelected){
                    GalleryLayout(onBack = { showImageSelected = true })
                } else {
                    PhotoLayout(onBack = { showImageSelected = false })
                }
            }
        }
    }


    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
    @Composable
    private fun GalleryLayout(onBack: () -> Unit){

        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        UpdateImages()
                    },
                    modifier = Modifier
                        .padding(16.dp)
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Cached,
                        contentDescription = "Switch camera",
                    )
                }
            },
            floatingActionButtonPosition = FabPosition.End, // Position can be changed
            containerColor = Color(0xFFF2F2F2)


        ) {
            Column (
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Spacer(modifier = Modifier.height(50.dp))
                Text(
                    text = "Welcome "+auth.currentUser?.displayName,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(16.dp)
                )
                OutlinedButton(
                    onClick = {
                        auth.signOut()
                        val intent = Intent(this@HomepageActivity, LoginActivity::class.java)
                        startActivity(intent)
                    },
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    Text("Sign Out")
                }
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
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2), // Number of columns
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ){
                    items(ImagesAndHues){ imageHue ->
                        Box(
                            modifier = Modifier
                                .padding(2.dp)
                                .shadow(10.dp)
                        ){
                            Image(
                                bitmap = imageHue.image!!,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(200.dp)
                                    .clickable {
                                        onBack()
                                        selectedImage = imageHue
                                    }
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun PhotoLayout(onBack: () -> Unit){
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF2F2F2))) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .matchParentSize()
            ) {
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .shadow(10.dp)
                ) {
                    Image(
                        bitmap = selectedImage!!.image!!,
                        contentDescription = null,
                    )
                }
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .shadow(10.dp)
                ) {
                    Image(
                        bitmap = selectedImage!!.hue!!,
                        contentDescription = null,
                    )
                }
                FilledTonalButton(
                    onClick = { onBack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "Switch camera",
                    )
                }
            }
        }
    }


    private suspend fun getAllImageUrlsFromFirebaseStorage() {
        val imagesRef = storage.reference.child("${auth.currentUser?.uid}/images/")
        val huesRef = storage.reference.child("${auth.currentUser?.uid}/hues/")

        try {
            val imagesListRefs = imagesRef.listAll().await()
            val huesListRefs = huesRef.listAll().await()
            imagesListRefs.items.forEach { imageRef ->

                val imageCode = imageRef.name.filter { it.isDigit() }.toInt()
                val imageHue: ImageHue = ImageHue(null, null, imageCode)

                huesListRefs.items.forEach { hueRef ->
                    val hueCode = hueRef.name.filter { it.isDigit() }.toInt()
                    if(imageCode == hueCode){
                        val hueBytes = hueRef.getBytes(Long.MAX_VALUE).await()
                        val bitmapHue: Bitmap = BitmapFactory.decodeByteArray(hueBytes, 0, hueBytes.size)
                        imageHue.hue = bitmapHue.asImageBitmap()
                    }
                }

                val imageBytes = imageRef.getBytes(Long.MAX_VALUE).await()
                val bitmapImage: Bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                imageHue.image = bitmapImage.asImageBitmap()
                ImagesAndHues.add(imageHue)
            }
        } catch (e: Exception) {
            // Handle any exceptions here
            println("ERROR DOWNLOADING")
        }
    }
    private fun UpdateImages(){
        GlobalScope.launch(Dispatchers.IO) {
            ImagesAndHues.removeAll(ImagesAndHues)
            getAllImageUrlsFromFirebaseStorage()
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
