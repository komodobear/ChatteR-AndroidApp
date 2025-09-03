package com.example.chater.Data

sealed class Screen(val route:String){
    object LoginScreen: Screen("loginscreen")
    object ChatRoomsScreen: Screen("chatroomscreen")
    object ChatScreen: Screen("chatscreen")
    object TokenScreen: Screen("tokenscreen")
}