package com.eslirodrigues.spendtalk.ui.screen.channel

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eslirodrigues.spendtalk.R
import com.eslirodrigues.spendtalk.core.state.ChannelState
import com.eslirodrigues.spendtalk.core.state.UserState
import com.eslirodrigues.spendtalk.data.model.Channel
import com.eslirodrigues.spendtalk.ui.screen.destinations.SignInScreenDestination
import com.eslirodrigues.spendtalk.ui.theme.LightGreen
import com.eslirodrigues.spendtalk.ui.theme.PrimaryGreen
import com.eslirodrigues.spendtalk.ui.viewmodel.ChannelViewModel
import com.eslirodrigues.spendtalk.ui.viewmodel.UserViewModel
import com.google.accompanist.glide.rememberGlidePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Destination(start = true)
@Composable
fun ChannelScreen(
    navigator: DestinationsNavigator,
    channelViewModel: ChannelViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel(),
) {

    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    val showAddChannelDialog = remember { mutableStateOf(false) }
    val showMenu = remember { mutableStateOf(false) }

    val reference = Firebase.database.getReference("message")
    val auth = FirebaseAuth.getInstance()

    var userEmail = ""
    if (!auth.currentUser?.email.isNullOrEmpty()) {
        userEmail = auth.currentUser?.email!!
    }

    channelViewModel.getChannels(userEmail)
    userViewModel.getUser(userEmail)

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            ChannelTopAppBar(navigator, auth, userViewModel, showMenu)
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
                        viewModel = channelViewModel,
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
            when(val result = channelViewModel.response.value) {
                is ChannelState.Success -> {
                    LazyColumn {
                        items(result.data) { channel ->
                            val state = rememberDismissState(
                                confirmStateChange = {
                                    if (it == DismissValue.DismissedToStart) {
                                        scope.launch {
                                            val snackbarResult = scaffoldState.snackbarHostState.showSnackbar(
                                                message = "Delete task was successful",
                                                actionLabel = "Undo"
                                            )
                                            when(snackbarResult) {
                                                SnackbarResult.Dismissed -> {
                                                    channelViewModel.deleteChannel(channel)
                                                    channelViewModel.getChannels(userEmail)
                                                }
                                                SnackbarResult.ActionPerformed -> channelViewModel.getChannels(userEmail)
                                            }
                                        }
                                    }
                                    true
                                }
                            )
                            SwipeToDismiss(
                                state = state,
                                background = {
                                    val color = when (state.dismissDirection) {
                                        DismissDirection.StartToEnd -> Color.Transparent
                                        DismissDirection.EndToStart -> Color.Red
                                        null -> Color.Transparent
                                    }
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(color = color)
                                            .padding(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = Color.White,
                                            modifier = Modifier.align(Alignment.CenterEnd)
                                        )
                                    }
                                },
                                dismissContent = {
                                    ChannelListItem(navigator = navigator, channel = channel)
                                },
                                directions = setOf(DismissDirection.EndToStart)
                            )
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
    userViewModel: UserViewModel,
    showMenu: MutableState<Boolean>
) {
    TopAppBar(
        title = {
            Text("SpendTalk", color = Color.White)
        },
        backgroundColor = PrimaryGreen,
        navigationIcon = {
            IconButton(onClick = {
                // navigate to alert change image
                },
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .border(width = 2.dp, color = LightGreen, shape = CircleShape)
            ) {
                when(val userResult = userViewModel.response.value) {
                    is UserState.Success -> {
                        val imageBitmap = userResult.data.firstOrNull()?.image
                        if (!imageBitmap.isNullOrEmpty()) {
                            val imageBytes = Base64.decode(imageBitmap, 0)
                            val image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                            Image(
                                painter = rememberGlidePainter(image),
                                contentScale = ContentScale.Crop,
                                contentDescription = stringResource(id = R.string.user_profile),
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
            }
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