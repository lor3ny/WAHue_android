package com.lor3n.wahue

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPoolAdapter
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import com.lor3n.wahue.ui.theme.ToneTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import kotlin.random.Random

class CameraActivity : AppCompatActivity() {


    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth
    private var checkImage: Bitmap? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        storage = Firebase.storage

        enableEdgeToEdge()
        setContent {
            var showWhiteScreen by remember { mutableStateOf(false) }

            ToneTheme {
                if(!showWhiteScreen){
                    CameraLayout(onBack = { showWhiteScreen = true })
                } else {
                    CheckLayout(onBack = {showWhiteScreen = false})
                }
            }
        }
    }


    @Composable
    private fun CameraLayout(onBack: () -> Unit){
        val controller = remember{
            LifecycleCameraController(applicationContext).apply {
                setEnabledUseCases(
                    CameraController.IMAGE_CAPTURE
                )
            }
        }

        Box (
            modifier = Modifier
                .fillMaxSize()
        ) {
            CameraPreview(
                controller = controller,
                modifier = Modifier
                    .fillMaxSize()
            )
           IconButton(
                onClick = {
                    controller.cameraSelector =
                        if (controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                            CameraSelector.DEFAULT_FRONT_CAMERA
                        } else {
                            CameraSelector.DEFAULT_BACK_CAMERA
                        }
                },
                modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(
                            horizontal = 16.dp,
                            vertical = 40.dp
                        )
            ) {
                Icon(
                    imageVector = Icons.Default.Cameraswitch,
                    contentDescription = "Switch camera",
                    tint = Color.White
                )
            }
            Button(
                onClick = {
                    takePhoto(
                        controller = controller,
                        checkActivator = onBack
                    )
                },
                modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoCamera,
                    contentDescription = "Take Photo"
                )
            }
        }
    }

    @Composable
    private fun CheckLayout(onBack: () -> Unit){
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF2F2F2))
        )
        {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .matchParentSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .shadow(10.dp)
                ) {
                    Image(
                        bitmap = checkImage!!.asImageBitmap(),
                        contentDescription = "Bitmap Image"
                    )
                }
                Row() {
                    Button(
                        onClick = {
                            onBack()
                            GlobalScope.launch(Dispatchers.IO) {
                                UploadPhotos()
                            }
                        },
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Switch camera",
                        )
                    }

                    Button(
                        onClick = { onBack() },
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = "Switch camera",
                        )
                    }
                }
            }
        }
    }


    private fun takePhoto(
        controller: LifecycleCameraController,
        checkActivator: () -> Unit
    ){

        controller.takePicture(
            ContextCompat.getMainExecutor(applicationContext),
            object : ImageCapture.OnImageCapturedCallback(){
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)

                    checkImage = image.toBitmap()

                    checkActivator()
                }

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)
                    Log.e("Camera", "Couldn't take photo")
                }

            }
        )
    }


    private fun UploadPhotos(){

        val byteStreamHue = ByteArrayOutputStream()
        val byteStreamImage = ByteArrayOutputStream()

        val ranInt: Int = (0..1000).random()

        var hue: HueBuilder = HueBuilder(checkImage!!)
        hue.BuildHue()
        hue.getHueImage()!!.compress(Bitmap.CompressFormat.PNG, 50, byteStreamHue)
        val dataHue = byteStreamHue.toByteArray()
        val hueRef = storage.reference.child("${auth.currentUser?.uid}/hues/hue_"+ranInt.toString()+".png")
        val uploadTaskHue = hueRef.putBytes(dataHue)
        uploadTaskHue.addOnSuccessListener { taskSnapshot ->
            println("Uploaded: "+taskSnapshot.metadata)
        }.addOnFailureListener { exception ->
            println("Not uploaded: "+exception.message)
        }

        checkImage!!.compress(Bitmap.CompressFormat.PNG, 50, byteStreamImage)
        val dataImage = byteStreamImage.toByteArray()
        val imageRef = storage.reference.child("${auth.currentUser?.uid}/images/image_"+ranInt.toString()+".png")
        val uploadTaskImage = imageRef.putBytes(dataImage)

        uploadTaskImage.addOnSuccessListener { taskSnapshot ->
            println("Uploaded: "+taskSnapshot.metadata)
        }.addOnFailureListener { exception ->
            println("Not uploaded: "+exception.message)
        }
    }
}