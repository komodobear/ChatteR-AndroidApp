package com.example.chater.ViewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chater.Data.Result
import com.example.chater.Injection
import com.example.chater.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginVM: ViewModel() {

	private val userRepository: UserRepository

	init {
		userRepository = UserRepository(
			FirebaseAuth.getInstance(),
			Injection.instance()
		)
	}

	private val _authResult = MutableLiveData<Result<Boolean>?>()
	val authResult: LiveData<Result<Boolean>?> get() = _authResult

	fun resetResult() {
		_authResult.value = null
	}

	fun logout() {
		viewModelScope.launch {
			try {
				FirebaseAuth.getInstance().signOut()
				resetResult()
			}catch(e: Exception){
				Log.e("LoginVM", "Logout failed: ${e.message}")
			}
		}
	}

	fun signUp(
		email: String, password: String, nick: String
	) {
		viewModelScope.launch {
			try {
				_authResult.value = userRepository.signUp(email, password, nick)
			}catch(e: Exception){
				_authResult.value = Result.Error(e)
				Log.e("LoginVM", "SignUp failed: ${e.message}")
			}
		}
	}

	fun login(email: String, password: String, authVM: AuthVM) {
		viewModelScope.launch {
			val result = userRepository.login(email, password)
			_authResult.value = result

			if(result is Result.Success) {
				val user = FirebaseAuth.getInstance().currentUser
				val token = user?.getIdToken(true)?.await()?.token
				val userId = user?.uid.orEmpty()
				val userName = user?.displayName.orEmpty()

				Log.d(
					"LoginVM",
					"Login successful: token=$token, userId=$userId, userName=$userName"
				)
				if(token != null) {
					authVM.loginUser(token, userId, userName)
				} else {
					Log.e("LoginVM", "Token is null")
				}

			} else {
				Log.e(
					"LoginVM",
					"Login failed: ${if(result is Result.Error) result.exception.message else "Unknown error"}"
				)
			}
		}
	}
}