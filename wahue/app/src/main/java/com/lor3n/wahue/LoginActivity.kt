package com.lor3n.wahue

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Bottom
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.lor3n.wahue.ui.theme.ui.theme.ToneTheme
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : ComponentActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToneTheme {
                // A surface container using the 'background' color from the theme
                firebaseAuth = FirebaseAuth.getInstance()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginPage()
                }
            }
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            goToHomapage()
        }
    }

    @Composable
    fun LoginPage() {
        var emailInput by remember { mutableStateOf("") }
        var passwordInput by remember { mutableStateOf("") }
        var resultText by remember { mutableStateOf("") }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Text(
                text = "Welcome",
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
            Row(){
                OutlinedButton(
                    onClick = {
                        var result:Boolean = verifyCredentials(emailInput, passwordInput)
                        if(!result){
                            resultText = "Email or password not exists"
                        } else {
                            goToHomapage()
                        }
                    },
                    Modifier.padding(10.dp)
                ){
                    Text("Log In")
                }
                FilledTonalButton(
                    onClick = { goToSignIn() },
                    Modifier.padding(10.dp)
                ){
                    Text("Sign In")
                }
            }
        }
    }


    private fun verifyCredentials(email: String, password: String): Boolean{
        var result = false
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Login successful
                    val user = firebaseAuth.currentUser
                    result = true
                    // You can handle the logged-in user here
                } else {
                    // Login failed
                    result = false
                    // You can display an error message or handle the failure case
                }
        }
        return result;
    }

    private fun goToHomapage(): Unit{
        val intent = Intent(this@LoginActivity, HomepageActivity::class.java)
        startActivity(intent)

    }

    private fun goToSignIn(): Unit{
        val intent = Intent(this@LoginActivity, SigninActivity::class.java)
        startActivity(intent)
    }

    @Preview(showBackground = true)
    @Composable
    fun LoginPagePreview() {
        ToneTheme {
            LoginPage()
        }
    }
}
