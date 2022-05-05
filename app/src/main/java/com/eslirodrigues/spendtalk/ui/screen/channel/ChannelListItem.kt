package com.eslirodrigues.spendtalk.ui.screen.channel

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eslirodrigues.spendtalk.R
import com.eslirodrigues.spendtalk.core.state.UserState
import com.eslirodrigues.spendtalk.data.model.Channel
import com.eslirodrigues.spendtalk.ui.screen.destinations.MessageScreenDestination
import com.eslirodrigues.spendtalk.ui.theme.LightGreen
import com.eslirodrigues.spendtalk.ui.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
fun ChannelListItem(navigator: DestinationsNavigator, channel: Channel, userViewModel: UserViewModel = viewModel()) {

    val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email

    val emailName = if (channel.friendEmail == currentUserEmail) channel.creatorEmail else channel.friendEmail

    Card(
        Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(top = 7.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(start = 6.dp)
                .clickable {
                    navigator.navigate(MessageScreenDestination(channel))
                },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Card(
                Modifier
                    .size(45.dp)
                    .clip(CircleShape)
                    .border(width = 2.dp, color = LightGreen, shape = CircleShape)
            ) {
                when(val userResult = userViewModel.userState.value) {
                    is UserState.Success -> {
                        val users = userResult.data.filter { it.email == emailName  }
                        val imageBitmap = users.firstOrNull()?.image
                        if (!imageBitmap.isNullOrEmpty()) {
                            val imageBytes = Base64.decode(imageBitmap, 0)
                            val image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                            Image(
                                bitmap = image.asImageBitmap(),
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
            Text(text = emailName, modifier = Modifier.padding(start = 8.dp))
//            if (true) {
//                Canvas(
//                    modifier = Modifier.size(22.dp),
//                    onDraw = {
//                        drawCircle(color = Color.Red)
//                    }
//                )
//            }
        }
    }
}