package com.eslirodrigues.spendtalk.ui.screen.channel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eslirodrigues.spendtalk.R
import com.eslirodrigues.spendtalk.core.state.ChannelState
import com.eslirodrigues.spendtalk.data.model.Channel
import com.eslirodrigues.spendtalk.ui.screen.destinations.SignInScreenDestination
import com.eslirodrigues.spendtalk.ui.theme.LightGreen
import com.eslirodrigues.spendtalk.ui.theme.PrimaryGreen
import com.eslirodrigues.spendtalk.ui.viewmodel.ChannelViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination(start = true)
@Composable
fun ChannelScreen(
    navigator: DestinationsNavigator,
    viewModel: ChannelViewModel = viewModel(),
) {

    val scaffoldState = rememberScaffoldState()

    val showAddChannelDialog = remember { mutableStateOf(false) }
    val showMenu = remember { mutableStateOf(false) }

    val reference = Firebase.database.getReference("message")
    val auth = FirebaseAuth.getInstance()

    LaunchedEffect(key1 = Unit) {
        viewModel.getChannels(auth.currentUser?.email!!)
    }


    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            ChannelTopAppBar(navigator, auth, showMenu)
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(
                backgroundColor = PrimaryGreen,
                onClick = {
                    showAddChannelDialog.value = !showAddChannelDialog.value
                }
            ) {
                if (showAddChannelDialog.value)
                    AddChannelDialog(
                        viewModel = viewModel,
                        showAddChannelDialog = showAddChannelDialog,
                        auth = auth,
                        reference = reference
                    )
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.add),
                    tint = Color.White
                )
            }
        }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            when(val result = viewModel.response.value) {
                is ChannelState.Success -> {
                    LazyColumn {
                        items(result.data) { item ->
                            ChannelListItem(navigator = navigator, channel = item)
                        }
                    }
                }
                is ChannelState.Failure -> {
                    Text(text = "${result.msg}")
                }
                is ChannelState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }
                is ChannelState.Empty -> {
                    Text(text = "")
                }
            }
        }
    }
}

@Composable
fun AddChannelDialog(
    viewModel: ChannelViewModel,
    showAddChannelDialog: MutableState<Boolean>,
    auth: FirebaseAuth,
    reference: DatabaseReference
) {
    var inputFriendEmail by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    AlertDialog(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp),
        onDismissRequest = { showAddChannelDialog.value = false },
        text = {
            TextField(
                value = inputFriendEmail,
                onValueChange = { inputFriendEmail = it },
                placeholder = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(id = R.string.add),
                        tint = Color.White
                    )
                    Text(
                        text = stringResource(id = R.string.email),
                        modifier = Modifier
                            .width(250.dp),
                        textAlign = TextAlign.Center,
                    )
                },
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.White,
                    placeholderColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    backgroundColor = PrimaryGreen
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = androidx.compose.ui.text.input.ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        showAddChannelDialog.value = false
                    }
                )
            )
        },
        confirmButton = {
            TextButton(
                modifier = Modifier
                    .padding(start = 5.dp, end = 15.dp, top = 5.dp, bottom = 5.dp),
                onClick = {
                    focusManager.clearFocus()
                    val channelId = reference.push().key
                    val creatorEmail = auth.currentUser?.email
                    val channel = Channel(
                        id = channelId!!,
                        creatorEmail = creatorEmail!!,
                        friendEmail = inputFriendEmail,
                        hasMessage = false
                    )
                    viewModel.addChannel(channel)
                    showAddChannelDialog.value = false
                }
            ) {
                Text(
                    text = stringResource(id = R.string.save).uppercase(),
                    color = Color.White,
                )
            }
        },
        dismissButton = {
            TextButton(
                modifier = Modifier
                    .padding(start = 15.dp, end = 5.dp, top = 5.dp, bottom = 5.dp),
                onClick = {
                    showAddChannelDialog.value = false
                }
            ) {
                Text(
                    text = stringResource(id = R.string.cancel).uppercase(),
                    color = Color.White,
                )

            }
        },
        backgroundColor = LightGreen,
        contentColor = Color.White,
        shape = RoundedCornerShape(25.dp)
    )
}

@Composable
fun ChannelTopAppBar(
    navigator: DestinationsNavigator,
    auth: FirebaseAuth,
    showMenu: MutableState<Boolean>
) {
    TopAppBar(
        title = {
            Text("SpendTalk", color = Color.White)
        },
        backgroundColor = PrimaryGreen,
        navigationIcon = {
//            Image(
//                painterResource(R.drawable.ic_simpletasktodobrand),
//                contentDescription = stringResource(id = R.string.app_name),
//                modifier = Modifier.padding(6.dp)
//            ) },
        },
        actions = {
            IconButton(onClick = { showMenu.value = !showMenu.value }) {
                Icon(imageVector = Icons.Default.MoreVert, "More", tint = Color.White)
            }
            DropdownMenu(
                expanded = showMenu.value,
                onDismissRequest = { showMenu.value = false },
                modifier = Modifier.background(Color.White)
            ) {
                DropdownMenuItem(
                    onClick = {
                        auth.signOut()
                        navigator.popBackStack(
                            SignInScreenDestination,
                            inclusive = false
                        )
                        navigator.navigate(SignInScreenDestination)
                    }
                ) {
                    Text(text = "Log out")
                }
            }
        }
    )
}