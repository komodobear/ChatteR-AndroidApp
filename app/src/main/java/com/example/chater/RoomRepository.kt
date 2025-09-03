package com.example.chater

import com.example.chater.Data.Room
import com.example.chater.Data.Result
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RoomRepository(private val firestore: FirebaseFirestore){

    suspend fun createRoom(name: String): Result<Unit> = try {
        val room = Room(name = name)
        firestore.collection("rooms").add(room).await()
        Result.Success(Unit)
    } catch(e:Exception){
        Result.Error(e)
    }

    suspend fun deleteRoom(roomId: String): Result<Unit> = try {
        withContext(Dispatchers.IO){
            val messagesQuery = firestore.collection("messages")
                .whereEqualTo("roomId",roomId)
                .get()
                .await()
            val batch = firestore.batch()

            for(document in messagesQuery.documents){
                batch.delete(document.reference)
            }

            val roomDocRef = firestore.collection("rooms").document(roomId)
            batch.delete(roomDocRef)

            batch.commit().await()
        }
        Result.Success(Unit)
    }catch (e: Exception){
        Result.Error(e)
    }

    fun getRoomFlow(roomId: String): Flow<Result<Room>> = callbackFlow {
        val roomRef = firestore.collection("rooms").document(roomId)

        val listenerRegistration = roomRef.addSnapshotListener{ snapshot, error->
            if(error != null){
                trySend(Result.Error(error)).isSuccess
                close(error)
                return@addSnapshotListener
            }
            if(snapshot != null && snapshot.exists()){
                val room = snapshot.toObject(Room::class.java)?.copy(id= snapshot.id)
                if(room != null){
                    trySend(Result.Success(room)).isSuccess
                }else{
                    trySend(Result.Error(Exception("Failed to parse from room data"))).isSuccess
                }
            }else{
                trySend(Result.Error(Exception(("Room with ID $roomId not found.")))).isSuccess
            }

        }
        awaitClose{
            listenerRegistration.remove()
        }
    }


    fun getRoomsFlow(): Flow<Result<List<Room>>> = callbackFlow{
        val listener = firestore.collection("rooms")
            .addSnapshotListener{ snapshot, error ->
                if(error != null){
                    trySend(Result.Error(error))
                    return@addSnapshotListener
                }
                val rooms = snapshot?.documents?.mapNotNull { doc->
                    doc.toObject(Room::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                trySend(Result.Success(rooms))
            }
        awaitClose{ listener.remove() }
    }

    suspend fun updateRoomBackground(roomId: String, backgroundRoute: Int?): Result<Unit> = try{
        val updateValue = backgroundRoute ?: FieldValue.delete()
        firestore.collection("rooms")
            .document(roomId)
            .update("bg",updateValue)
            .await()
        Result.Success(Unit)
    }catch (e: Exception){
        Result.Error(e)
    }
}