package com.eslirodrigues.spendtalk.ui.screen.message

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eslirodrigues.spendtalk.R
import com.eslirodrigues.spendtalk.core.state.MessageState
import com.eslirodrigues.spendtalk.data.model.Channel
import com.eslirodrigues.spendtalk.data.model.Message
import com.eslirodrigues.spendtalk.ui.theme.PrimaryGreen
import com.eslirodrigues.spendtalk.ui.viewmodel.MessageViewModel
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
    viewModel: MessageViewModel = viewModel(),
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
        viewModel.getMessages(channel)
    }

    val messageId = reference.push().key
    val userEmail = auth.currentUser?.email
    val message = Message(id = messageId!!, channelId = channel.id, email = userEmail!!, text = inputText)

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            MessageTopAppBar(navigator, auth, showMenu, channel)
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
                            viewModel.sendMessage(message)
                            inputText = ""
                            focusManager.clearFocus()
                        }
                    )
                )
                IconButton(onClick = {
                    viewModel.sendMessage(message)
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
                when (val result = viewModel.response.value) {
                    is MessageState.Success -> {
                        LazyColumn(modifier = Modifier.padding(bottom = it.calculateBottomPadding()),
                            state = lazyState) {
                            coroutineScope.launch {
                                lazyState.scrollToItem(result.data.lastIndex)
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
    channel: Channel
) {
    val currentUserEmail = auth.currentUser?.email
    TopAppBar(
        title = {
            Text(if (currentUserEmail == channel.creatorEmail) channel.friendEmail else channel.creatorEmail, color = Color.White)
        },
        backgroundColor = PrimaryGreen,
        navigationIcon = {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = stringResource(id = R.string.back),
                modifier = Modifier
                    .padding(6.dp)
                    .clickable {
                        navigator.popBackStack()
                    }
            )
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