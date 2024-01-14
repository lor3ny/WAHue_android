package com.lor3n.wahue

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.lor3n.wahue.ui.theme.ToneTheme
import kotlin.random.Random

class LogoActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToneTheme {

                auth = Firebase.auth

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFF2F2F2)
                ) {
                    LogoPage()
                }
            }
        }
    }
    @Composable
    fun LogoPage(){
        val imageArray = arrayOf(
            R.drawable.wahue_1,
            R.drawable.wahue_2,
            R.drawable.wahue_3,
            R.drawable.wahue_4// Add as many as you have
        )
        var indexImage = Random.nextInt(imageArray.size)
        var imageResource by remember { mutableStateOf(imageArray[indexImage]) }

        Box(modifier = Modifier.fillMaxSize()) {
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
                        painter = painterResource(id = imageResource),
                        contentDescription = "Random Image"
                    )
                }
                Row() {
                    FilledTonalButton(
                        onClick = {
                            if (auth.currentUser != null) {
                                goToHomepage()
                            } else {
                                goToLogin()
                            }
                      },
                        Modifier.padding(10.dp)
                    ) {
                        Text("Login")
                    }
                }
            }

            // Join! button at the bottom
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Divider(
                    color = Color.Gray,
                    thickness = 1.dp, // You can adjust the thickness of the line as needed
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Text(
                    text = "Don't you have an account?",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                OutlinedButton(
                    onClick = {goToSignin()},
                ) {
                    Text("Join!")
                }
            }
        }

    }

    private fun goToHomepage(): Unit{
        val intent = Intent(this@LogoActivity, HomepageActivity::class.java)
        startActivity(intent)
    }

    private fun goToSignin(): Unit{
        val intent = Intent(this@LogoActivity, SigninActivity::class.java)
        startActivity(intent)
    }

    private fun goToLogin(): Unit{
        val intent = Intent(this@LogoActivity, LoginActivity::class.java)
        startActivity(intent)
    }
}