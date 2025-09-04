package com.example.chater.Screens

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.chater.AdManager
import com.example.chater.BuildConfig
import com.example.chater.Data.Background
import com.example.chater.Data.Message
import com.example.chater.Data.allBackgorunds
import com.example.chater.Data.drawerBackgorunds
import com.example.chater.R
import com.example.chater.ViewModels.MessVM
import com.example.chater.ViewModels.RoomVM
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatScreen(
	roomId: String,
	messVM: MessVM = viewModel(),
	roomVM: RoomVM = viewModel(),
	nav: NavController
) {
	val context = LocalContext.current
	val messages by messVM.messages.observeAsState(emptyList())
	val currentUser by messVM.currentUser.observeAsState()
	val currentRoom by roomVM.currentRoom.observeAsState()

	val listState = rememberLazyListState()

	val rewardAdManager = remember { AdManager(context) }
	var showAdButton by remember { mutableStateOf(false) }

	var barClicked by remember { mutableStateOf(false) }

	val roomBackground = remember(currentRoom) {
		currentRoom?.bg?.let { bg ->
			allBackgorunds.find { it.route == bg }
		}
	}

	val bgColor = MaterialTheme.colorScheme.background.copy(0.03f)

	var infoTextColor = if(isSystemInDarkTheme()) {
		Color.White.copy(alpha = 0.7f)
	} else {
		Color.Black.copy(alpha = 0.7f)
	}

	val drawerState = rememberDrawerState(DrawerValue.Closed)
	val scope = rememberCoroutineScope()

	val rewardedAdId = BuildConfig.AdMobRewardedAd

	LaunchedEffect(Unit) {
		rewardAdManager.loadRewardedAd(rewardedAdId) {
			showAdButton = true
		}
	}

	LaunchedEffect(barClicked) {
		if(barClicked) {
			delay(200)
			val lastIndex = messages.lastIndex
			if(lastIndex >= 0) {
				listState.animateScrollToItem(lastIndex)
			}
			barClicked = false
		}
	}

	LaunchedEffect(messages) {
		if(messages.isNotEmpty()) {
			val lastIndex = messages.lastIndex
			if(lastIndex >= 0) {
				listState.animateScrollToItem(lastIndex)
			}
		}
	}

	LaunchedEffect(roomId) {
		messVM.setRoomId(roomId)
		roomVM.observeRoomById(roomId) {
			nav.popBackStack()
		}
	}

	val text = remember { mutableStateOf("") }

	val bottomBar: @Composable () -> Unit = {

		Row(
			modifier = Modifier
				.fillMaxWidth()
				.background(MaterialTheme.colorScheme.primary)
				.height(50.dp)
				.imePadding(),
			verticalAlignment = Alignment.CenterVertically
		) {
			BasicTextField(
				value = text.value,
				onValueChange = {
					text.value = it
				},
				textStyle = TextStyle.Default.copy(fontSize = 16.sp, color = Color.White),
				modifier = Modifier
					.weight(1f)
					.padding(8.dp)
					.height(30.dp)
					.pointerInteropFilter {
						if(it.action == android.view.MotionEvent.ACTION_DOWN) {
							barClicked = true
						}
						false
					},
				cursorBrush = SolidColor(Color.White)
			)

			IconButton(
				onClick = {
					if(text.value.isNotEmpty()) {
						messVM.sendMessage(text.value.trim())
						text.value = ""
					}
				}
			) {
				Icon(
					imageVector = Icons.Default.Send,
					contentDescription = "Send",
					tint = Color.White
				)
			}
		}
	}

	Box(
		Modifier.fillMaxSize(),
	) {
		if(roomBackground != null) {
			Image(
				painter = painterResource(roomBackground.route !!),
				contentDescription = null,
				modifier = Modifier
					.fillMaxSize()
					.zIndex(0f),
				contentScale = ContentScale.Crop
			)
			infoTextColor = Color.White.copy(alpha = 0.7f)
		}

		ModalNavigationDrawer(
			drawerState = drawerState,
			drawerContent = {
				ModalDrawerSheet {
					Column(
						verticalArrangement = Arrangement.SpaceBetween
					) {
						LazyColumn(
							Modifier
								.padding(16.dp)
								.weight(1f)
						) {
							items(drawerBackgorunds) { item ->
								DrawerBackgroundItem(bg = item) {
									scope.launch {
										currentRoom?.let { roomVM.updateBackground(it.id, item) }
										drawerState.close()
									}
								}
							}
						}
						Column(
							Modifier.fillMaxWidth()
						) {
							Row(
								Modifier
									.fillMaxWidth()
									.padding(start = 24.dp)
							) {
								if(showAdButton) {
									OutlinedButton(
										onClick = {
											rewardAdManager.showRewardedAd(
												onRewardEarner = { reward ->
													scope.launch {
														currentRoom?.let {
															roomVM.updateBackground(
																it.id,
																Background.backgroundThree
															)
														}
														drawerState.close()
													}
												},
												onAdClosed = {
													showAdButton = false
													rewardAdManager.loadRewardedAd(rewardedAdId)
												},
												onAdFailed = {
													Toast.makeText(
														context,
														"Ad failed to load",
														Toast.LENGTH_SHORT
													).show()
												}
											)
										},
										modifier = Modifier.padding(start = 8.dp, end = 8.dp),

										colors = ButtonDefaults.outlinedButtonColors(
											contentColor = MaterialTheme.colorScheme.inversePrimary
										),
										border = BorderStroke(
											width = 1.dp,
											color = MaterialTheme.colorScheme.inversePrimary
										),

										) {
										Text("Premium Background")
									}
								}
							}

							Row(
								Modifier
									.fillMaxWidth()
									.padding(32.dp)
									.clickable {
										scope.launch {
											Toast.makeText(
												context,
												"Room ${currentRoom?.name} deleted",
												Toast.LENGTH_SHORT
											).show()
											roomVM.deleteRoom()
										}
									}
							) {
								Icon(
									imageVector = Icons.Default.Delete,
									contentDescription = "bg",
									Modifier.padding(end = 8.dp, top = 4.dp)
								)
								Text(
									text = "Delete Room",
									style = MaterialTheme.typography.titleMedium
								)
							}
						}
					}
				}
			}
		) {

			Scaffold(
				containerColor = bgColor,
				bottomBar = bottomBar,
				topBar = {
					TopAppBar(
						title = { Text(text = currentRoom?.name ?: "Chat", color = Color.White) },
						navigationIcon = {
							IconButton(onClick = { nav.popBackStack() }) {
								Icon(
									imageVector = Icons.Default.ArrowBack,
									contentDescription = "back",
									tint = Color.White
								)
							}
						},
						actions = {
							IconButton(onClick = {
								scope.launch { drawerState.open() }
							}) {
								Icon(
									Icons.Default.Menu,
									contentDescription = "Open Drawer",
									tint = Color.White
								)
							}
						},
						colors = TopAppBarColors(
							containerColor = MaterialTheme.colorScheme.primary,
							titleContentColor = MaterialTheme.colorScheme.onPrimary,
							navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
							actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
							scrolledContainerColor = MaterialTheme.colorScheme.onPrimary
						)
					)
				},
				modifier = Modifier
					.nestedScroll(rememberNestedScrollInteropConnection())
			) {
				Column(
					modifier = Modifier
						.fillMaxSize()
						.padding(it)
						.imePadding()
				) {

					if(currentUser != null) {
						LazyColumn(
							state = listState,
							modifier = Modifier
								.weight(1f)
								.padding(horizontal = 16.dp)
								.padding(bottom = 16.dp)
								.imePadding(),
						) {
							items(messages) { mess ->
								ChatMessageItem(
									message = mess,
									messVM = messVM,
									textColor = infoTextColor
								)
							}
						}
					} else {
						Box(
							modifier = Modifier
								.weight(1f)
								.fillMaxSize(),
							contentAlignment = Alignment.Center
						) {
							Text("Loading...")
						}
					}
				}
			}

		}

	}
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatMessageItem(message: Message, messVM: MessVM, textColor: Color) {

	val isCurrentUser = remember { messVM.isMessageFromCurrentUser(message) }

	Column(
		modifier = Modifier
			.fillMaxWidth()
			.padding(8.dp, top = 20.dp),
		horizontalAlignment = if(isCurrentUser) Alignment.End else Alignment.Start
	) {

		Box(
			modifier = Modifier
				.background(
					if(isCurrentUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
					shape = RoundedCornerShape(8.dp)
				)
				.padding(8.dp)
		) {
			Text(
				text = message.text,
				color = Color.White,
				style = TextStyle(fontSize = 16.sp)
			)
		}
		Spacer(modifier = Modifier.height(4.dp))
		Text(
			text = message.senderFirstName,
			style = TextStyle(
				fontSize = 12.sp,
				color = textColor
			)
		)
		Text(
			text = formatTimeStamp(message.timestamp),
			style = TextStyle(
				fontSize = 12.sp,
				color = textColor
			)
		)
	}
}

@Composable
fun DrawerBackgroundItem(
	bg: Background,
	drawerClick: () -> Unit
) {
	val icon = if(bg.name == "delete background") {
		painterResource(R.drawable.cancel)
	} else {
		painterResource(R.drawable.image)
	}

	Row(
		Modifier
			.fillMaxWidth()
			.padding(16.dp)
			.clickable {
				drawerClick()
			},
		verticalAlignment = Alignment.CenterVertically
	) {
		Icon(
			painter = icon,
			contentDescription = "bg",
			Modifier.padding(end = 8.dp, top = 4.dp)
		)
		Text(
			text = bg.name,
			style = MaterialTheme.typography.titleMedium
		)
	}
}

@RequiresApi(Build.VERSION_CODES.O)
private fun formatTimeStamp(timestamp: Long): String {
	val messageDateTime =
		LocalDateTime.ofInstant(
			Instant.ofEpochMilli(timestamp),
			ZoneId.systemDefault()
		)
	val now = LocalDateTime.now()

	return when {
		isSameDay(messageDateTime, now) -> "today ${formatTime(messageDateTime)}"
		isSameDay(messageDateTime.plusDays(1), now) -> "yesterday ${formatTime(messageDateTime)}"
		else -> formatDate(messageDateTime)
	}
}

@RequiresApi(Build.VERSION_CODES.O)
private fun isSameDay(time1: LocalDateTime, time2: LocalDateTime): Boolean {
	val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
	return time1.format(formatter) == time2.format(formatter)
}

@RequiresApi(Build.VERSION_CODES.O)
private fun formatTime(dateTime: LocalDateTime): String {
	val formatter = DateTimeFormatter.ofPattern("HH:mm")
	return formatter.format(dateTime)
}

@RequiresApi(Build.VERSION_CODES.O)
private fun formatDate(dateTime: LocalDateTime): String {
	val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
	return formatter.format(dateTime)
}
