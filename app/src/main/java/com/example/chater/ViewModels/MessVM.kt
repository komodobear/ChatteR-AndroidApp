package com.example.chater.ViewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chater.Data.Message
import com.example.chater.Data.Result
import com.example.chater.Data.User
import com.example.chater.Injection
import com.example.chater.MessageRepository
import com.example.chater.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class MessVM : ViewModel() {

    private val messageRepository: MessageRepository
    private val userRepository: UserRepository

    init {
        messageRepository = MessageRepository(Injection.instance())
        userRepository = UserRepository(FirebaseAuth.getInstance(),Injection.instance())
        loadCurrentUser()
    }

    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> get() = _messages

    private val _roomId = MutableLiveData<String>()
    private val _currentUser = MutableLiveData<User>()
    val currentUser: LiveData<User> get() = _currentUser

    private fun loadCurrentUser(){
        viewModelScope.launch {
            when (val result = userRepository.getCurrentUser()){
                is Result.Success -> _currentUser.value = result.data
                is Result.Error -> {Log.e("MessVM", "Error loading current user: ${result.exception.message}")}
            }
        }
    }

    fun loadMessages(){
        viewModelScope.launch {
            if(_roomId.value != null){
                messageRepository.getChatMessages(_roomId.value.toString())
                    .collect{ _messages.value = it }
            }
        }
    }

    fun sendMessage(text: String){
        if(_currentUser.value!=null){
            val message = Message(
                senderFirstName = _currentUser.value!!.nick,
                senderId = _currentUser.value!!.email,
                text = text
            )
            viewModelScope.launch {
                when(messageRepository.sendMessage(_roomId.value.toString(), message)){
                    is Result.Success -> Unit
                    is Result.Error -> {}
                }
            }
        }
    }

    fun setRoomId(roomId: String){
        _roomId.value = roomId
        loadCurrentUser()
        loadMessages()
    }

    fun isMessageFromCurrentUser(message: Message): Boolean{
        return _currentUser.value?.email == message.senderId
    }



}