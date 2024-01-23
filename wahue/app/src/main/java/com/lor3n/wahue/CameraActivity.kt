package com.lor3n.wahue

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import com.lor3n.wahue.ui.theme.ToneTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore


class CameraActivity : AppCompatActivity() {


    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth
    private lateinit var database : FirebaseFirestore
    private var checkImage: Bitmap? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        storage = Firebase.storage
        database = Firebase.firestore

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
            Row(
                modifier = Modifier
                    .padding(
                        horizontal = 20.dp,
                        vertical = 60.dp
                    )
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ){
                Button(
                    onClick = {
                        takePhoto(
                            controller = controller,
                            checkActivator = onBack
                        )
                    },
                    modifier = Modifier
                        .padding(5.dp)
                        .weight(1f),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 5.dp,  // Default elevation
                        pressedElevation = 10.dp,  // Elevation when the button is pressed
                        disabledElevation = 0.dp  // Elevation when the button is disabled
                    ),
                    shape = RoundedCornerShape(15.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Take Photo"
                    )
                    Text(text = "Take Photo")
                }
                FilledTonalButton(
                    onClick = {
                        controller.cameraSelector =
                            if (controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                                CameraSelector.DEFAULT_FRONT_CAMERA
                            } else {
                                CameraSelector.DEFAULT_BACK_CAMERA
                            }
                    },
                    modifier = Modifier
                        .padding(5.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 5.dp,  // Default elevation
                        pressedElevation = 10.dp,  // Elevation when the button is pressed
                        disabledElevation = 0.dp  // Elevation when the button is disabled
                    ),
                    shape = RoundedCornerShape(15.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Cameraswitch,
                        contentDescription = "Switch camera",
                        tint = Color.White
                    )
                }
            }

        }
    }

    @Composable
    private fun CheckLayout(onBack: () -> Unit){

        val context = LocalContext.current

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
                    .fillMaxSize()
            ) {
                Image(
                    bitmap = checkImage!!.asImageBitmap(),
                    contentDescription = "Bitmap Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding (
                            horizontal = 20.dp,
                            vertical = 10.dp
                        )
                )
                Row(
                    modifier = Modifier
                        .padding (
                            horizontal = 15.dp,
                            vertical = 10.dp
                        )
                ) {
                    Button(
                        onClick = {
                            onBack()
                            GlobalScope.launch(Dispatchers.IO) {
                                UploadPhotos(context)
                            }
                        },
                        modifier = Modifier
                            .padding(5.dp)
                            .weight(1f),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 5.dp,  // Default elevation
                            pressedElevation = 10.dp,  // Elevation when the button is pressed
                            disabledElevation = 0.dp  // Elevation when the button is disabled
                        ),
                        shape = RoundedCornerShape(15.dp)
                    ) {
                        Text(text = "Accept")
                    }
                    Button(
                        onClick = { onBack() },
                        modifier = Modifier
                            .weight(1f)
                            .padding(5.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 5.dp,  // Default elevation
                            pressedElevation = 10.dp,  // Elevation when the button is pressed
                            disabledElevation = 0.dp  // Elevation when the button is disabled
                        ),
                        shape = RoundedCornerShape(15.dp)
                    ) {
                        Text(text = "Cancel")
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
                    checkImage = resizeBitmap(image.toBitmap(), 3)
                    checkActivator()
                }

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)
                    Log.e("Camera", "Couldn't take photo")
                }

            }
        )
    }

    fun resizeBitmap(original: Bitmap, factor: Int): Bitmap? {
        var width = original.width
        var height = original.height
        val bitmapRatio = width.toFloat() / height.toFloat()

        if (bitmapRatio > 1) {
            width /= factor
            height = (width / bitmapRatio).toInt()
        } else {
            height /= factor
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(original, width, height, true)
    }


    private fun UploadPhotos(context: Context){

        val byteStreamImage = ByteArrayOutputStream()

        val ranInt: Int = (0..1000).random()

        checkImage!!.compress(Bitmap.CompressFormat.JPEG, 50, byteStreamImage)
        val dataImage = byteStreamImage.toByteArray()
        val imageRef = storage.reference.child("${auth.currentUser?.uid}/images/image_"+ranInt.toString()+".jpeg")
        val uploadTaskImage = imageRef.putBytes(dataImage)

        uploadTaskImage.addOnSuccessListener { taskSnapshot ->
            println("Uploaded: "+taskSnapshot.metadata)
        }.addOnFailureListener { exception ->
            println("Not uploaded: "+exception.message)
        }

        uploadLastLocation(context, ranInt)



    }

    @SuppressLint("MissingPermission")
    private fun uploadLastLocation(context: Context, code : Int,) {
        val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        val cancellationTokenSource = CancellationTokenSource()


        fusedLocationClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY, cancellationTokenSource.token)
            .addOnSuccessListener { location ->
                if (location != null) {
                    val lastCoords = hashMapOf(
                        "latitude" to location.latitude,
                        "longitude" to location.longitude,
                    )

                    database.collection(auth.currentUser!!.uid)
                        .document("image_$code")
                        .set(lastCoords)
                        .addOnSuccessListener { documentReference ->
                            println("Coords uploaded$documentReference")
                        }
                        .addOnFailureListener { e ->
                            println("Coords not uploaded${e.message}")
                        }
                }
            }
    }
}