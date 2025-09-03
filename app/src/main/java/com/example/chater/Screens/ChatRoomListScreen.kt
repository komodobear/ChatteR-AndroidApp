package com.example.chater.Screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chater.Data.Room
import com.example.chater.R
import com.example.chater.ViewModels.MessVM
import com.example.chater.ViewModels.RoomVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatRoomListScreen(
	roomVM: RoomVM = viewModel(),
	onJoinClicked: (Room) -> Unit,
	messVM: MessVM = viewModel(),
	onLogout: () -> Unit
) {
	val currentUser by messVM.currentUser.observeAsState()
	val userName = currentUser?.nick
	val rooms by roomVM.rooms.observeAsState(emptyList())
	var showDialog by remember { mutableStateOf(false) }
	var name by remember { mutableStateOf("") }

	LaunchedEffect(Unit) {
		roomVM.loadRooms()
	}

	Scaffold(
		topBar = {
			TopAppBar(
				title = {
					Column {
						Text("Rooms", fontWeight = FontWeight.Bold)
						Text(
							text = "Nick: $userName",
							fontSize = 14.sp,
							color = Color.LightGray
						)
					}
				},
				colors = TopAppBarDefaults.topAppBarColors(
					containerColor = MaterialTheme.colorScheme.primary,
					titleContentColor = Color.White
				),
				actions = {
					IconButton(onClick = {
						onLogout()
					}) {
						Icon(
							painter = painterResource(R.drawable.logout),
							contentDescription = "Logout",
							tint = Color.White
						)
					}
				}
			)
		},

		) { innerPadding ->
		Column(
			modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
			verticalArrangement = Arrangement.SpaceBetween
		) {
			Column(
				modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .padding(16.dp),
			) {
				LazyColumn(
					Modifier.weight(1f, fill = false)
				) {
					items(rooms) { room ->
						RoomItem(room = room) { onJoinClicked(room) }
					}
				}

				Spacer(modifier = Modifier.height(16.dp))

				Button(
					onClick = {
						showDialog = true
					},
					modifier = Modifier.fillMaxWidth(),
					colors = ButtonDefaults.buttonColors(
						containerColor = MaterialTheme.colorScheme.secondary,
						contentColor = MaterialTheme.colorScheme.onSecondary
					)
				) {
					Text("Add room")
				}

				if(showDialog) {
					AlertDialog(
						onDismissRequest = {
						showDialog = false
						name = ""
					},
						title = {
							Text(
								"Make a new room",
								textAlign = TextAlign.Center,
								modifier = Modifier.fillMaxWidth()
							)
						},
						text = {
							OutlinedTextField(
								value = name,
								onValueChange = { name = it },
								singleLine = true,
								modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
								colors = OutlinedTextFieldDefaults.colors(
									cursorColor = MaterialTheme.colorScheme.secondary,
									focusedBorderColor = MaterialTheme.colorScheme.secondary
								)
							)
						}, confirmButton = {
							Row(
								modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
								horizontalArrangement = Arrangement.SpaceBetween
							) {
								Button(
									onClick = {
										if(name.isNotBlank()) {
											showDialog = false
											roomVM.createRoom(name)
											name = ""
										}
									},
									colors = ButtonDefaults.buttonColors(
										containerColor = MaterialTheme.colorScheme.secondary,
										contentColor = MaterialTheme.colorScheme.onSecondary
									)
								) {
									Text("Add")
								}
								Button(
									onClick = {
										showDialog = false
										name = ""
									},
									colors = ButtonDefaults.buttonColors(
										containerColor = MaterialTheme.colorScheme.secondary,
										contentColor = MaterialTheme.colorScheme.onSecondary
									)
								) {
									Text("Cancel")
								}
							}
						}
					)
				}
			}

			Column(
				modifier = Modifier.fillMaxWidth(),
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Ad(
					modifier = Modifier.fillMaxWidth(),
					adId = "ca-app-pub-3940256099942544/9214589741"
				)
				Spacer(modifier = Modifier.height(16.dp))
			}
		}
	}
}

@Composable
fun RoomItem(room: Room, onJoinClicked: (Room) -> Unit) {
	Row(
		modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically
	) {
		Text(text = room.name, fontSize = 16.sp, fontWeight = FontWeight.Normal)

		OutlinedButton(
			onClick = { onJoinClicked(room) },
			colors = ButtonDefaults.outlinedButtonColors(
				contentColor = MaterialTheme.colorScheme.inversePrimary
			),
			border = BorderStroke(
				width = 1.dp,
				color = MaterialTheme.colorScheme.inversePrimary
			)
		)
		{
			Text("Join")
		}
	}
}