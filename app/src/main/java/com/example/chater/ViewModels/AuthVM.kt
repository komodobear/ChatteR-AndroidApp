package com.example.chater.ViewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.chater.SessionManager
import kotlinx.coroutines.launch

class AuthVM(application: Application): AndroidViewModel(application) {

	private val sessionManager = SessionManager(application.applicationContext)

	val userToken: LiveData<String?> = sessionManager.userTokenFlow.asLiveData()

	fun loginUser(token: String, userId: String, userName: String) {
		viewModelScope.launch {
			try {
				sessionManager.saveUserSession(token, userId, userName)
			}catch(e: Exception){
				Log.e("AuthVM", "Login failed: ${e.message}")
			}
		}
	}

	fun logoutUser() {
		viewModelScope.launch {
			try {
				sessionManager.clearSession()
			}catch(e: Exception){
				Log.e("AuthVM", "Logout failed: ${e.message}")
			}
		}
	}
}