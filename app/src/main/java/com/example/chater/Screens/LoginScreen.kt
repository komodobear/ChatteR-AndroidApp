package com.example.chater.Screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.chater.BuildConfig
import com.example.chater.ViewModels.LoginVM
import com.example.chater.Data.Result
import com.example.chater.ViewModels.AuthVM
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun LoginScreen(
	loginVM: LoginVM,
	authVM: AuthVM,
	onSignInSuccess: () -> Unit
) {
	val context = LocalContext.current

	var showDialog by remember { mutableStateOf(false) }

	var email by remember { mutableStateOf("") }
	var password by remember { mutableStateOf("") }

	val result by loginVM.authResult.observeAsState()

	val bannerAdId = BuildConfig.AdMobBannerId

	LaunchedEffect(result) {
		when(val currentResult = result) {
			is Result.Success -> {
				val firebaseUser = (currentResult.data as? com.google.firebase.auth.FirebaseUser)
				val token = firebaseUser?.getIdToken(true)?.result?.token
				val userId = firebaseUser?.uid
				val userName = firebaseUser?.email

				if(token != null && userId != null && userName != null) {
					authVM.loginUser(token, userId, userName)
				}
				onSignInSuccess()
			}

			is Result.Error -> {
				val error = (result as Result.Error).exception
				Toast.makeText(context, "Login error: ${error.message}", Toast.LENGTH_SHORT).show()
			}

			else -> {}
		}
		loginVM.resetResult()
	}

	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(16.dp),
		verticalArrangement = Arrangement.SpaceBetween,
		horizontalAlignment = Alignment.CenterHorizontally
	) {

		Column(
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally,
			modifier = Modifier.weight(1f)
		) {
			Spacer(modifier = Modifier.height(24.dp))

			Row {
				Text(
					text = "Welcome in",
					fontSize = 30.sp,
					color = MaterialTheme.colorScheme.onBackground
				)
				Text(
					text = " ChateR",
					fontSize = 30.sp,
					color = MaterialTheme.colorScheme.onBackground,
					fontWeight = FontWeight.Bold
				)
			}

			Spacer(modifier = Modifier.height(8.dp))

			OutlinedTextField(
				value = email,
				onValueChange = { email = it },
				label = { Text("Email") },
				modifier = Modifier
					.fillMaxWidth()
					.padding(8.dp),
				colors = OutlinedTextFieldDefaults.colors(
					cursorColor = MaterialTheme.colorScheme.inversePrimary,
					focusedBorderColor = MaterialTheme.colorScheme.inversePrimary,
					focusedLabelColor = MaterialTheme.colorScheme.inversePrimary
				)
			)
			OutlinedTextField(
				value = password,
				onValueChange = { password = it },
				label = { Text("Password") },
				modifier = Modifier
					.fillMaxWidth()
					.padding(8.dp),
				visualTransformation = PasswordVisualTransformation(),
				colors = OutlinedTextFieldDefaults.colors(
					cursorColor = MaterialTheme.colorScheme.inversePrimary,
					focusedBorderColor = MaterialTheme.colorScheme.inversePrimary,
					focusedLabelColor = MaterialTheme.colorScheme.inversePrimary
				)
			)
			Button(
				onClick = {
					loginVM.login(email, password, authVM)
				},
				modifier = Modifier
					.fillMaxWidth()
					.padding(8.dp),
				colors = ButtonDefaults.buttonColors(
					containerColor = MaterialTheme.colorScheme.inversePrimary,
					contentColor = MaterialTheme.colorScheme.onSecondary
				)
			) {
				Text("Login", color = MaterialTheme.colorScheme.background)
			}
			Spacer(modifier = Modifier.height(16.dp))
			Text(
				"Don't have an account? Sign in",
				modifier = Modifier.clickable { showDialog = true },
				color = MaterialTheme.colorScheme.onBackground
			)
		}

		if(showDialog) {

			var email by remember { mutableStateOf("") }
			var password by remember { mutableStateOf("") }
			var nick by remember { mutableStateOf("") }

			AlertDialog(
				onDismissRequest = { showDialog = false },
				title = {
					Text(
						text = "Sign in an account",
						modifier = Modifier.fillMaxWidth(),
						textAlign = TextAlign.Center
					)
				},
				text = {
					Column(
						modifier = Modifier
							.fillMaxWidth()
							.height(300.dp)
							.padding(16.dp),
						verticalArrangement = Arrangement.SpaceBetween,
						horizontalAlignment = Alignment.CenterHorizontally
					) {
						Column(
							modifier = Modifier.weight(1f),
							verticalArrangement = Arrangement.Center,
							horizontalAlignment = Alignment.CenterHorizontally
						) {
							OutlinedTextField(
								value = email,
								onValueChange = { email = it },
								label = { Text("Email") },
								modifier = Modifier
									.fillMaxWidth()
									.padding(8.dp),
								colors = OutlinedTextFieldDefaults.colors(
									cursorColor = MaterialTheme.colorScheme.inversePrimary,
									focusedBorderColor = MaterialTheme.colorScheme.inversePrimary,
									focusedLabelColor = MaterialTheme.colorScheme.inversePrimary
								)
							)
							OutlinedTextField(
								value = password,
								onValueChange = { password = it },
								label = { Text("Password") },
								modifier = Modifier
									.fillMaxWidth()
									.padding(8.dp),
								visualTransformation = PasswordVisualTransformation(),
								colors = OutlinedTextFieldDefaults.colors(
									cursorColor = MaterialTheme.colorScheme.inversePrimary,
									focusedBorderColor = MaterialTheme.colorScheme.inversePrimary,
									focusedLabelColor = MaterialTheme.colorScheme.inversePrimary
								)
							)
							OutlinedTextField(
								value = nick,
								onValueChange = { nick = it },
								label = { Text("Nick") },
								modifier = Modifier
									.fillMaxWidth()
									.padding(8.dp),
								colors = OutlinedTextFieldDefaults.colors(
									cursorColor = MaterialTheme.colorScheme.inversePrimary,
									focusedBorderColor = MaterialTheme.colorScheme.inversePrimary,
									focusedLabelColor = MaterialTheme.colorScheme.inversePrimary
								)
							)
						}
					}
				},
				confirmButton = {
					Row(
						modifier = Modifier
							.fillMaxWidth()
							.padding(8.dp),
						horizontalArrangement = Arrangement.SpaceBetween
					) {
						Button(
							onClick = {
								loginVM.signUp(email, password, nick)
								email = ""
								password = ""
								nick = ""
								showDialog = false
							},
							colors = ButtonDefaults.buttonColors(
								containerColor = MaterialTheme.colorScheme.inversePrimary,
								contentColor = MaterialTheme.colorScheme.background
							)
						) {
							Text("Submit")
						}
						Button(
							onClick = { showDialog = false },
							colors = ButtonDefaults.buttonColors(
								containerColor = MaterialTheme.colorScheme.inversePrimary,
								contentColor = MaterialTheme.colorScheme.background
							)
						) {
							Text("Cancel")
						}
					}
				}
			)
		}

		Ad(modifier = Modifier.fillMaxWidth(), adId = bannerAdId)
	}
}

@Composable
fun Ad(modifier: Modifier, adId: String) {
	Column(
		modifier = modifier,
		verticalArrangement = Arrangement.Bottom
	) {
		Spacer(modifier = Modifier.size(24.dp))
		AndroidView(
			modifier = Modifier.fillMaxWidth(),
			factory = { context ->
				AdView(context).apply {
					setAdSize(AdSize.BANNER)
					adUnitId = adId
					loadAd(AdRequest.Builder().build())
				}
			}
		)
	}
}