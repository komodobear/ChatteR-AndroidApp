package com.example.chater

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.chater.ViewModels.AuthVM
import com.example.chater.ViewModels.LoginVM
import com.example.chater.ui.theme.CzateriaTheme
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity: ComponentActivity() {

	private lateinit var authVM: AuthVM

	@RequiresApi(Build.VERSION_CODES.O)

	private val userRepository = UserRepository(
		FirebaseAuth.getInstance(),
		Injection.instance()
	)

	@RequiresApi(Build.VERSION_CODES.O)
	override fun onCreate(savedInstanceState: Bundle?) {

		authVM = ViewModelProvider(this).get(AuthVM::class.java)

		authVM.userToken.observe(this) { token ->
			if(token != null && token.isNotEmpty()) {
				Log.d("MainActivity", "User is logged in with token: $token")
			} else {
				Log.d("MainActivity", "User is not logged in")
			}
		}

		super.onCreate(savedInstanceState)

		MobileAds.initialize(this) { status ->
			Log.d("AdMob", "Initialization status ${status.adapterStatusMap}")
		}

		setContent {
			val navController = rememberNavController()
			val loginVM: LoginVM = viewModel()
			CzateriaTheme {

				SystemAppearance()

				Surface(
					modifier = Modifier.fillMaxSize(),
					color = MaterialTheme.colorScheme.background
				) {
					Navigation(navController, loginVM, authVM)
				}
			}
		}
	}

}

@Composable
fun SystemAppearance() {
	val view = LocalView.current
	val window = (view.context as Activity).window
	val darkTheme = isSystemInDarkTheme()
	val color = MaterialTheme.colorScheme.primary.toArgb()

	SideEffect {
		WindowCompat.getInsetsController(window, view).apply {
			isAppearanceLightStatusBars = false
		}
		window.statusBarColor = when {
			darkTheme -> color
			else -> color
		}
		window.navigationBarColor = color
	}
}