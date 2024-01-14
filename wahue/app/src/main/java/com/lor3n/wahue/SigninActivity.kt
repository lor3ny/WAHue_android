package com.lor3n.wahue

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material3.Divider
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.lor3n.wahue.ui.theme.ToneTheme

class SigninActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        enableEdgeToEdge()
        setContent {
            ToneTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFF2F2F2)
                ) {
                    Signin()
                }
            }
        }
    }


    @Composable
    fun Signin() {
        var emailInput by remember { mutableStateOf("") }
        var passwordInput by remember { mutableStateOf("") }
        var verPasswordInput by remember { mutableStateOf("") }
        var resultText by remember { mutableStateOf("") }

        var nameInput by remember { mutableStateOf("") }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.verticalScroll(rememberScrollState())
        ){
            Spacer(modifier = Modifier.height(50.dp))
            Text(
                text = "Join the family!",
                color = Color.Black,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = resultText,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall
            )
            OutlinedTextField(
                value = emailInput,
                leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = "emailIcon") },
                //trailingIcon = { Icon(imageVector = Icons.Default.Add, contentDescription = null) },
                onValueChange = {
                    emailInput = it
                },
                label = { Text(text = "Email") },
                placeholder = { Text(text = "Enter your e-mail") },


                modifier = Modifier
                    .padding(5.dp)


            )
            OutlinedTextField(
                value = passwordInput,
                leadingIcon = { Icon(imageVector = Icons.Default.Key, contentDescription = "emailIcon") },
                //trailingIcon = { Icon(imageVector = Icons.Default.Add, contentDescription = null) },
                onValueChange = {
                    passwordInput = it
                },
                label = { Text(text = "Password") },
                placeholder = { Text(text = "Enter your Password") },
                modifier = Modifier.padding(5.dp)
            )
            OutlinedTextField(
                value = verPasswordInput,
                leadingIcon = { Icon(imageVector = Icons.Default.Check, contentDescription = "emailIcon") },
                //trailingIcon = { Icon(imageVector = Icons.Default.Add, contentDescription = null) },
                onValueChange = {
                    verPasswordInput = it
                },
                label = { Text(text = "Verify Password") },
                placeholder = { Text(text = "Enter your Password") },
                modifier = Modifier.padding(5.dp)
            )
            Divider(
                color = Color.Gray,
                thickness = 1.dp, // You can adjust the thickness of the line as needed
                modifier = Modifier.padding(
                    vertical = 10.dp,
                    horizontal = 80.dp)

            )
            OutlinedTextField(
                value = nameInput,
                //leadingIcon = { Icon(imageVector = Icons.Default.Check, contentDescription = "emailIcon") },
                //trailingIcon = { Icon(imageVector = Icons.Default.Add, contentDescription = null) },
                onValueChange = {
                    nameInput = it
                },
                label = { Text(text = "First Name") },
                placeholder = { Text(text = "Enter your Name") },
                modifier = Modifier.padding(5.dp)
            )
            Row(){
                FilledTonalButton(
                    onClick = {
                        auth.createUserWithEmailAndPassword(emailInput, passwordInput)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    updateUserProfile(auth.currentUser!!, nameInput)
                                } else {
                                    val exception = task.exception
                                    if (exception != null) {
                                        // Handle specific exceptions here if needed
                                        val errorMessage = exception.message ?: "Unknown error occurred"
                                        resultText = errorMessage
                                    }
                                }
                            }
                    },
                    Modifier.padding(10.dp)
                ){
                    Text("Sign In")
                }
            }
        }
    }

    private fun goToHomepage(){
        val intent = Intent(this@SigninActivity, HomepageActivity::class.java)
        startActivity(intent)
    }

    private fun verifyEmailExistence(): Boolean{
        return false
    }

    private fun updateUserProfile(user: FirebaseUser, displayName: String?){
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(displayName)
            .build()
        user.updateProfile(profileUpdates)
            .addOnCompleteListener { task: Task<Void?> ->
                if (task.isSuccessful) {
                    goToHomepage()
                } else {
                    //Bho
                }
            }
    }
}