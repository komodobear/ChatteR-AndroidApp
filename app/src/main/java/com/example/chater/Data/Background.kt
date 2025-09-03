package com.example.chater.Data

import com.example.chater.R

sealed class Background(
    val name: String,
    val route: Int?
){
    object backgroundOne: Background("Background One", R.raw.bg1)
    object backgroundTwo: Background("Background Two", R.raw.bg2)
    object backgroundThree: Background("mur",R.raw.mur)
    object backgroundDel: Background("Delete Background",null)
}

val drawerBackgorunds = listOf(
    Background.backgroundDel,
    Background.backgroundOne,
    Background.backgroundTwo,
)

val allBackgorunds = listOf(
    Background.backgroundDel,
    Background.backgroundOne,
    Background.backgroundThree,
    Background.backgroundTwo,
)