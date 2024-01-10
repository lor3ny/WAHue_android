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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import com.lor3n.wahue.ui.theme.ToneTheme
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import kotlin.random.Random

class CameraActivity : AppCompatActivity() {


    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        storage = Firebase.storage

        enableEdgeToEdge()
        setContent {
            ToneTheme {
                ToneTheme {
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
                                .offset(16.dp, 16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cameraswitch,
                                contentDescription = "Switch camera"
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        )
                        {
                            IconButton(
                                onClick = {
                                    takePhoto(
                                        controller = controller
                                    )
                                },
                                modifier = Modifier
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhotoCamera,
                                    contentDescription = "Take Photo"
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun takePhoto(
        controller: LifecycleCameraController,
    ){
        controller.takePicture(
            ContextCompat.getMainExecutor(applicationContext),
            object : ImageCapture.OnImageCapturedCallback(){
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)

                    val byteStream = ByteArrayOutputStream()
                    var bitmapPhoto: Bitmap = image.toBitmap()
                    var hue: HueBuilder = HueBuilder(bitmapPhoto)
                    hue.BuildHue()

                    hue.getHueImage()!!.compress(Bitmap.CompressFormat.PNG, 100, byteStream)
                    val data = byteStream.toByteArray()

                    val ranInt: Int = (0..1000).random()
                    val imagesRef = storage.reference.child("${auth.currentUser?.uid}/images/image_"+ranInt.toString()+".png")
                    val uploadTask = imagesRef.putBytes(data)

                    uploadTask.addOnSuccessListener { taskSnapshot ->
                        println("Uploaded")
                    }.addOnFailureListener { exception ->
                        println("Not uploaded")
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)
                    Log.e("Camera", "Couldn't take photo")
                }
            }
        )
    }
}