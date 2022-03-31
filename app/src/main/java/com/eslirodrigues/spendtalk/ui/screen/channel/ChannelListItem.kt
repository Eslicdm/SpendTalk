package com.eslirodrigues.spendtalk.ui.screen.channel

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.eslirodrigues.spendtalk.data.model.Channel
import com.eslirodrigues.spendtalk.ui.screen.destinations.MessageScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
fun ChannelListItem(navigator: DestinationsNavigator, channel: Channel) {

    Card(Modifier.fillMaxWidth().height(60.dp).padding(top = 7.dp)) {
        Row(
            modifier = Modifier.padding(start = 20.dp)
                .clickable {
                    navigator.navigate(MessageScreenDestination(channel))
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = channel.friendEmail)
            if (channel.hasMessage) {
                Canvas(modifier = Modifier.size(42.dp).padding(end = 30.dp), onDraw = {
                    drawCircle(color = Color.Red)
                })
            }
        }
    }
}