package com.example.chater

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.chater.Data.Screen
import com.example.chater.Screens.ChatRoomListScreen
import com.example.chater.Screens.ChatScreen
import com.example.chater.Screens.LoginScreen
import com.example.chater.Screens.TokenScreen
import com.example.chater.ViewModels.AuthVM
import com.example.chater.ViewModels.LoginVM

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation(
    navController: NavHostController,
    loginVM: LoginVM,
    authVM: AuthVM
    ) {
    NavHost(
        navController = navController,
        startDestination = Screen.TokenScreen.route
    ) {
        composable(Screen.TokenScreen.route) {
            TokenScreen(navController = navController, authVM = authVM)
        }

        composable(Screen.LoginScreen.route) {
            LoginScreen(
                loginVM = loginVM,
                authVM = authVM,
                onSignInSuccess = { navController.navigate(Screen.ChatRoomsScreen.route) }
            )
        }

        composable(Screen.ChatRoomsScreen.route){
            ChatRoomListScreen(
                onJoinClicked = {
                    navController.navigate("${Screen.ChatScreen.route}/${it.id}")
                },
                onLogout = {
                    loginVM.logout()
                    authVM.logoutUser()
                    navController.navigate(Screen.LoginScreen.route)
                }
            )
        }

        composable("${Screen.ChatScreen.route}/{roomId}"){
            val roomId: String = it
                .arguments?.getString("roomId") ?: ""
            ChatScreen(roomId = roomId,nav = navController)
        }
    }
}