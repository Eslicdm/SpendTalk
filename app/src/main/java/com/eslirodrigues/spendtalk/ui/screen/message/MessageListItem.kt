package com.eslirodrigues.spendtalk.ui.screen.message

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eslirodrigues.spendtalk.data.model.Message
import com.google.firebase.auth.FirebaseAuth

@Composable
fun MessageListItem(message: Message) {

    val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = if (message.email != currentUserEmail) Alignment.Start else Alignment.End
    ) {
        Text(text = message.email,  fontSize = 14.sp)
        Spacer(modifier = Modifier.padding(vertical = 3.dp))
        Text(
            text = message.text,
            color = Color.Black,
            modifier = Modifier
                .background(color = Color.White,
                    shape = RoundedCornerShape(corner = CornerSize(8.dp)))
                .padding(horizontal = 11.dp, vertical = 4.dp)
        )
        Spacer(modifier = Modifier.padding(vertical = 10.dp))
    }
}