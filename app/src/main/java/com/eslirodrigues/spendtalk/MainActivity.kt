package com.eslirodrigues.spendtalk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.eslirodrigues.spendtalk.ui.screen.NavGraphs
import com.eslirodrigues.spendtalk.ui.screen.destinations.SignInScreenDestination
import com.eslirodrigues.spendtalk.ui.theme.SpendTalkTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ramcosta.composedestinations.DestinationsNavHost

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Firebase.database.setPersistenceEnabled(true)

        setContent {
            SpendTalkTheme {
                if (FirebaseAuth.getInstance().currentUser == null) {
                    DestinationsNavHost(navGraph = NavGraphs.root, startDestination = SignInScreenDestination)
                } else {
                    DestinationsNavHost(navGraph = NavGraphs.root)
                }
            }
        }
    }
}