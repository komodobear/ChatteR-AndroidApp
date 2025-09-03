package com.example.chater.ViewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chater.Data.Background
import com.example.chater.Data.Result
import com.example.chater.Data.Room
import com.example.chater.Injection
import com.example.chater.RoomRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RoomVM: ViewModel() {

    private val _rooms = MutableLiveData<List<Room>>()
    val rooms: LiveData<List<Room>> get() = _rooms

    private val _currentRoom = MutableLiveData<Room?>()
    val currentRoom: LiveData<Room?> get() = _currentRoom

    private var currentRoomJob: Job? = null

    private val roomRepository: RoomRepository

    init {
        roomRepository = RoomRepository(Injection.instance())
        loadRooms()
    }

    fun observeRoomById(roomId: String, onError: () -> Unit){
        currentRoomJob?.cancel()

        currentRoomJob = viewModelScope.launch{
            roomRepository.getRoomFlow(roomId).collectLatest { result ->
                when(result){
                    is Result.Success -> _currentRoom.value = result.data
                    is Result.Error -> {
                        _currentRoom.value = null
                        onError()
                    }
                }
            }
        }
    }

    fun deleteRoom(){
        viewModelScope.launch {
            val result = currentRoom.value?.let { roomRepository.deleteRoom(it.id) }
            when(result){
                is Result.Success -> {
                    _currentRoom.value = null
                }
                is Result.Error -> {
                    Log.e("RoomVM", "Error deleting room: ${result.exception.message}")
                }

                null -> Log.e("RoomVM", "currentRoom.value = null")
            }
        }
    }

    fun createRoom(name: String){
        viewModelScope.launch {
            when(roomRepository.createRoom(name)){
                is Result.Success -> loadRooms()
                is Result.Error -> {  }
            }
        }
    }

    fun loadRooms(){
        viewModelScope.launch {
            roomRepository.getRoomsFlow().collect(){result->
                when(result){
                    is Result.Success -> _rooms.value = result.data
                    is Result.Error -> {}
                }
            }
        }
    }

    fun updateBackground(roomId: String, background: Background){
        viewModelScope.launch {
            try {
                roomRepository.updateRoomBackground(roomId,background.route)
            }catch(e: Exception){
                Log.e("RoomVM", "Error updating background: ${e.message}")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        currentRoomJob?.cancel()
    }
}