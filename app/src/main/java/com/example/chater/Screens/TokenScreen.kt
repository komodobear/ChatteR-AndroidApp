package com.example.chater.Screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.chater.Data.Screen
import com.example.chater.ViewModels.AuthVM

@Composable
fun TokenScreen(
	navController: NavController,
	authVM: AuthVM
) {
	val token = authVM.userToken.observeAsState()

	Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
		CircularProgressIndicator()
	}

	LaunchedEffect(token.value) {
		when {
			! token.value.isNullOrEmpty() -> {
				Log.d("TokenScreen", "User is logged in with token: ${token.value}")
				navController.navigate(Screen.ChatRoomsScreen.route) {
					popUpTo(Screen.TokenScreen.route) { inclusive = true }
				}
			}

			else -> {
				Log.d("TokenScreen", "User is not logged in")
				navController.navigate(Screen.LoginScreen.route) {
					popUpTo(Screen.TokenScreen.route) { inclusive = true }
				}
			}
		}
	}
}

