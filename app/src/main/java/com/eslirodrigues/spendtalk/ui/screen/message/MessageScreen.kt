package com.eslirodrigues.spendtalk.ui.screen.message

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eslirodrigues.spendtalk.core.state.MessageState
import com.eslirodrigues.spendtalk.data.model.Message
import com.eslirodrigues.spendtalk.ui.screen.destinations.SignInScreenDestination
import com.eslirodrigues.spendtalk.ui.theme.PrimaryGreen
import com.eslirodrigues.spendtalk.ui.viewmodel.MessageViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination(start = true)
@Composable
fun MessageScreen(
    navigator: DestinationsNavigator,
    viewModel: MessageViewModel = viewModel(),
) {
    val scaffoldState = rememberScaffoldState()

    val reference = Firebase.database.getReference("message")
    val auth = FirebaseAuth.getInstance()

    var inputText by remember { mutableStateOf("") }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            MessageTopAppBar(navigator, auth)
        }
    ) {
        Column(modifier = Modifier.padding(20.dp).fillMaxSize()) {
            Spacer(modifier = Modifier.padding(top = 20.dp))
            TextField(
                value = inputText,
                onValueChange = {
                    inputText = it
                }
            )
            Button(
                onClick = {
                    val messageId = reference.push().key
                    val userEmail = auth.currentUser?.email
                    val message = Message(id = messageId!!, username = userEmail!!, text = inputText)
                    viewModel.sendMessage(message)
                    viewModel.getAllMessages()
                }
            ) {
                Text(text = "Send")
            }

            when(val result = viewModel.response.value) {
                is MessageState.Success -> {
                    LazyColumn {
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
}

@Composable
fun MessageTopAppBar(
    navigator: DestinationsNavigator,
    auth: FirebaseAuth
) {
    var showMenu by remember { mutableStateOf(false) }
    val currentUser = auth.currentUser
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
            IconButton(onClick = { showMenu = !showMenu }) {
                Icon(imageVector = Icons.Default.MoreVert, "More", tint = Color.White)
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                modifier = Modifier.background(Color.White)
            ) {
                if (currentUser != null) {
                    DropdownMenuItem(
                        onClick = {
                            auth.signOut()
                            navigator.popBackStack(
                                SignInScreenDestination,
                                inclusive = false
                            )
                        }
                    ) {
                        Text(text = "Log out")
                    }
                } else {
                    DropdownMenuItem(
                        onClick = {
                            navigator.navigate(SignInScreenDestination)
                        }
                    ) {
                        Text(text = "Log in")
                    }
                }
            }
        }
    )
}