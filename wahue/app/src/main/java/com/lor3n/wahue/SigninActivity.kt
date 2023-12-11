package com.lor3n.wahue

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.lor3n.wahue.ui.theme.ToneTheme

class SigninActivity : ComponentActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        setContent {
            ToneTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Text(
                text = "A newcomer!",
                style = MaterialTheme.typography.headlineMedium
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
                modifier = Modifier.padding(5.dp)
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
            Row(){
                OutlinedButton(
                    onClick = {
                        if(verifyEmailExistance()){
                            resultText = "The email already exists"
                        } else {
                            if(passwordInput != verPasswordInput){
                                resultText = "Passwords are not equal"
                            } else {
                                addCredentials(emailInput, passwordInput)
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

    private fun verifyEmailExistance(): Boolean{
        return false
    }

    private fun addCredentials(email: String, password: String): Unit{
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Registration successful
                    val user = firebaseAuth.currentUser
                    val intent = Intent(this@SigninActivity, HomepageActivity::class.java)
                    startActivity(intent)
                    // You can handle the registered user here
                } else {
                    // Registration failed
                }
            }
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        ToneTheme {
            Signin()
        }
    }
}