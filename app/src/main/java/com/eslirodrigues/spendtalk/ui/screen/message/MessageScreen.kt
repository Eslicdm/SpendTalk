package com.eslirodrigues.spendtalk.ui.screen.message

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eslirodrigues.spendtalk.R
import com.eslirodrigues.spendtalk.core.state.MessageState
import com.eslirodrigues.spendtalk.core.state.UserState
import com.eslirodrigues.spendtalk.data.model.Channel
import com.eslirodrigues.spendtalk.data.model.Message
import com.eslirodrigues.spendtalk.ui.theme.LightGreen
import com.eslirodrigues.spendtalk.ui.theme.PrimaryGreen
import com.eslirodrigues.spendtalk.ui.viewmodel.MessageViewModel
import com.eslirodrigues.spendtalk.ui.viewmodel.UserViewModel
import com.google.accompanist.glide.rememberGlidePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

@Destination
@Composable
fun MessageScreen(
    navigator: DestinationsNavigator,
    messageViewModel: MessageViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel(),
    channel: Channel
) {
    val scaffoldState = rememberScaffoldState()
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    val lazyState = rememberLazyListState()

    val showMenu = remember { mutableStateOf(false) }

    val reference = Firebase.database.getReference("message")
    val auth = FirebaseAuth.getInstance()

    var inputText by remember { mutableStateOf("") }

    LaunchedEffect(key1 = Unit) {
        messageViewModel.getMessages(channel)
    }

    val messageId = reference.push().key
    val userEmail = auth.currentUser?.email
    val message = Message(id = messageId!!, channelId = channel.id, email = userEmail!!, text = inputText)

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            MessageTopAppBar(navigator, auth, showMenu, channel, userViewModel)
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom
            ) {
                TextField(
                    modifier = Modifier.height(56.dp),
                    value = inputText,
                    onValueChange = {
                        inputText = it
                    },
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.send_a_message),
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            messageViewModel.sendMessage(message)
                            inputText = ""
                            focusManager.clearFocus()
                        }
                    )
                )
                IconButton(onClick = {
                    messageViewModel.sendMessage(message)
                    inputText = ""
                    focusManager.clearFocus()
                }) {
                    Icon(Icons.Default.Send, contentDescription = stringResource(id = R.string.send))
                }
            }
        },
        content = {
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)) {
                when (val result = messageViewModel.response.value) {
                    is MessageState.Success -> {
                        LazyColumn(modifier = Modifier.padding(bottom = it.calculateBottomPadding()),
                            state = lazyState) {
                            coroutineScope.launch {
                                if (result.data.isNotEmpty()) {
                                    lazyState.scrollToItem(result.data.lastIndex)
                                }
                            }
                            items(result.data) { item ->
                                MessageListItem(message = item)
                            }

                        }
                    }
                    is MessageState.Failure -> {
                        Text(text = "${result.msg}")
                    }
                    is MessageState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }
                    }
                    is MessageState.Empty -> {
                        Text(text = "Empty")
                    }
                }
            }
        }
    )
}

@Composable
fun MessageTopAppBar(
    navigator: DestinationsNavigator,
    auth: FirebaseAuth,
    showMenu: MutableState<Boolean>,
    channel: Channel,
    userViewModel: UserViewModel,
) {
    userViewModel.getUsers()
    val currentUserEmail = auth.currentUser?.email
    TopAppBar(
        title = {
            Text(if (currentUserEmail == channel.creatorEmail) channel.friendEmail else channel.creatorEmail, color = Color.White, modifier = Modifier.padding(start = 3.dp))
        },
        backgroundColor = PrimaryGreen,
        navigationIcon = {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = stringResource(id = R.string.back),
                modifier = Modifier.clickable { navigator.popBackStack() }
            )
            when(val userResult = userViewModel.response.value) {
                is UserState.Success -> {
                    val users = userResult.data.filter { it.email == channel.friendEmail }
                    val imageBitmap = users.firstOrNull()?.image
                    if (!imageBitmap.isNullOrEmpty()) {
                        val imageBytes = Base64.decode(imageBitmap, 0)
                        val image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                        Image(
                            painter = rememberGlidePainter(image),
                            contentScale = ContentScale.Crop,
                            contentDescription = stringResource(id = R.string.user_profile),
                            modifier = Modifier.size(40.dp).clip(CircleShape).border(width = 2.dp, color = LightGreen, shape = CircleShape)
                        )
                    } else {
                        Icon(Icons.Default.AccountCircle, contentDescription = stringResource(id = R.string.user_profile), modifier = Modifier.fillMaxSize())
                    }
                }
                is UserState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }
                else -> {
                    Icon(Icons.Default.AccountCircle, contentDescription = stringResource(id = R.string.user_profile), modifier = Modifier.fillMaxSize())
                }
            }
        },
//        actions = {
//            IconButton(onClick = { showMenu.value = !showMenu.value }) {
//                Icon(imageVector = Icons.Default.MoreVert, "More", tint = Color.White)
//            }
//            DropdownMenu(
//                expanded = showMenu.value,
//                onDismissRequest = { showMenu.value = false },
//                modifier = Modifier.background(Color.White)
//            ) {
//                DropdownMenuItem(
//                    onClick = {
//
//                    }
//                ) {
//                    Text(text = "Log out")
//                }
//            }
//        }
    )
}