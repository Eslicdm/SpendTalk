package com.eslirodrigues.spendtalk.ui.screen.message

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.eslirodrigues.spendtalk.data.model.Message
import com.eslirodrigues.spendtalk.ui.theme.LightGreen

@Composable
fun MessageListItem(message: Message) {

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = message.email)
        Text(
            text = message.text,
            color = Color.Black,
            modifier = Modifier.background(LightGreen)
        )
    }
}