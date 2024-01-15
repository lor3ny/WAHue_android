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
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.provider.CalendarContract
import android.provider.MediaStore
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import coil.request.ImageRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.OutputStream

data class ImageHue(var image: ImageBitmap?, var hue: List<String>?, val code: Int)
class HomepageActivity : ComponentActivity() {

    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore
    private var ImagesAndHues = mutableStateListOf<ImageHue>()
    private var selectedImage: ImageHue? = null


    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!hasRequiredPermissions()) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA), 0
            )
        }

        auth = Firebase.auth
        storage = Firebase.storage
        database = Firebase.firestore
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
    override fun onStart() {
        super.onStart()
        UpdateImages()
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
    @Composable
    private fun GalleryLayout(onBack: () -> Unit){
        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            floatingActionButton = {
                Row(
                    modifier = Modifier
                        .padding(
                            horizontal = 20.dp,
                            vertical = 10.dp
                        )
                        .fillMaxWidth(),
                ){
                    Button(
                        onClick = {
                            val intent = Intent(this@HomepageActivity, CameraActivity::class.java)
                            startActivity(intent)
                        },
                        modifier = Modifier
                            .padding(5.dp)
                            .weight(1f),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 5.dp,  // Default elevation
                            pressedElevation = 10.dp,  // Elevation when the button is pressed
                            disabledElevation = 0.dp  // Elevation when the button is disabled
                        ),
                    ) {
                        Text("Camera")
                    }
                    FilledTonalButton(
                        onClick = {
                            UpdateImages()
                        },
                        modifier = Modifier
                            .padding(5.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 5.dp,  // Default elevation
                            pressedElevation = 10.dp,  // Elevation when the button is pressed
                            disabledElevation = 0.dp  // Elevation when the button is disabled
                        ),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cached,
                            contentDescription = "Switch camera",
                        )
                    }
                }
            },
            floatingActionButtonPosition = FabPosition.Center, // Position can be changed
            containerColor = Color(0xFFF2F2F2)


        ) {
            Column (
                horizontalAlignment = Alignment.Start
            ){
                Spacer(modifier = Modifier.height(50.dp))
                Row(
                    modifier = Modifier
                        .padding(
                            horizontal = 20.dp,
                        )
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom
                ){
                    Text(
                        text = "HUEs of",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = auth.currentUser?.displayName.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Light,
                        modifier = Modifier.padding(horizontal = 2.dp)
                    )
                    TextButton(
                        onClick = {
                            auth.signOut()
                            val intent = Intent(this@HomepageActivity, LoginActivity::class.java)
                            startActivity(intent)
                        },
                        contentPadding = PaddingValues(0.dp )
                    ) {
                        Text("Sign Out")
                    }
                }
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3), // Number of columns
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement =  Arrangement.spacedBy(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ){
                    items(ImagesAndHues){ imageHue ->
                        Image(
                            bitmap = imageHue.image!!,
                            contentDescription = null,
                            modifier = Modifier
                                .size(130.dp)
                                .clickable {
                                    onBack()
                                    selectedImage = imageHue
                                    for (i in 0..<selectedImage!!.hue!!.size) {
                                        println("Color: ${selectedImage!!.hue!![i]}")
                                    }
                                },
                                contentScale = ContentScale.FillWidth
                        )
                    }
                }
            }
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    private fun PhotoLayout(onBack: () -> Unit){

        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }


        Scaffold (
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF2F2F2)),
            containerColor = Color(0xFFF2F2F2),
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            topBar = {
                OutlinedButton(
                    onClick = { onBack() },
                    modifier = Modifier
                    .padding(
                        vertical = 50.dp,
                        horizontal = 20.dp
                    )
                )
                {
                    Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    )
                }
            },

            bottomBar = {
                Row(
                    modifier = Modifier
                        .padding(
                            horizontal = 15.dp,
                            vertical = 40.dp
                        )
                ){
                    Button (
                        onClick = {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Hue Saved on Gallery",
                                    duration = SnackbarDuration.Short
                                )
                            }
                            val builder = HueBuilder(selectedImage!!.image!!.asAndroidBitmap())
                            builder.BuildHue()
                            saveBitmapToGallery(builder.getHueImage()!!, context, "wahue_hue_${selectedImage!!.code}")
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(5.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 5.dp,  // Default elevation
                            pressedElevation = 10.dp,  // Elevation when the button is pressed
                            disabledElevation = 0.dp  // Elevation when the button is disabled
                        ),
                    )
                    {
                        Icon(
                            imageVector = Icons.Default.ColorLens,
                            contentDescription = null,
                        )
                    }
                    Button(
                        onClick = {
                            val builder = HueBuilder(selectedImage!!.image!!.asAndroidBitmap())
                            var result: Bitmap = builder.BuildHueImage()
                            saveBitmapToGallery(result, context, "wahue_${selectedImage!!.code}")

                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Image Saved on Gallery",
                                    duration = SnackbarDuration.Short,
                                )
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(5.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 5.dp,  // Default elevation
                            pressedElevation = 10.dp,  // Elevation when the button is pressed
                            disabledElevation = 0.dp  // Elevation when the button is disabled
                        ),
                    )
                    {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                        )
                    }
                }
            },

            content = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Image(
                        bitmap = selectedImage!!.image!!,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 30.dp,
                                vertical = 10.dp
                            ),
                        contentScale = ContentScale.FillWidth
                    )
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(5), // Number of columns
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        modifier = Modifier.padding (
                            horizontal = 30.dp,
                            vertical = 0.dp
                        )
                    ){
                        items(selectedImage!!.hue!!){ color ->
                            Column (
                                horizontalAlignment = Alignment.CenterHorizontally
                            ){
                                Box(
                                    modifier = Modifier
                                        .height(90.dp)
                                        .fillMaxWidth()
                                        .background(Color(color.toLong(16)))
                                        .clickable {
                                            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val clipData = ClipData.newPlainText("Copied Text", "#${color.substring(2)}")
                                            clipboardManager.setPrimaryClip(clipData)
                                        }
                                )
                                Text(
                                    text = "#${color.substring(2)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        )
    }


    private suspend fun getAllImageUrlsFromFirebaseStorage() {
        val imagesRef = storage.reference.child("${auth.currentUser?.uid}/images/")
        val huesRef = storage.reference.child("${auth.currentUser?.uid}/hues/")

        try {
            val imagesListRefs = imagesRef.listAll().await()
            val huesListRefs = huesRef.listAll().await()
            imagesListRefs.items.forEach { imageRef ->

                val imageBytes = imageRef.getBytes(Long.MAX_VALUE).await()
                val bitmapImage: Bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                val imageCode = imageRef.name.filter { it.isDigit() }.toInt()
                val imageHue: ImageHue = ImageHue(null, null, imageCode)
                var hue: HueBuilder = HueBuilder(bitmapImage)
                val hueColors: List<String> = hue.BuildHueList()

                imageHue.hue = hueColors
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

    fun saveBitmapToGallery(bitmap: Bitmap, context: Context, filename: String) {
        val outputStream: OutputStream
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }

        // Inserting the file into the MediaStore
        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        outputStream = context.contentResolver.openOutputStream(uri!!)!!

        // Writing the bitmap
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.close()
    }


    private fun hasRequiredPermissions(): Boolean{
        return CAMERAX_PERMISSIONS.all{
            ContextCompat.checkSelfPermission(
                applicationContext,
                it
            ) == PackageManager.PERMISSION_GRANTED
        } and STORAGE_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                applicationContext,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object{
        private val CAMERAX_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private val STORAGE_PERMISSIONS = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }
}
