package com.eslirodrigues.spendtalk.ui.screen.auth

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eslirodrigues.spendtalk.R
import com.eslirodrigues.spendtalk.data.model.User
import com.eslirodrigues.spendtalk.ui.screen.destinations.SignInScreenDestination
import com.eslirodrigues.spendtalk.ui.theme.DarkGreen
import com.eslirodrigues.spendtalk.ui.theme.LightGreen
import com.eslirodrigues.spendtalk.ui.theme.PrimaryGreen
import com.eslirodrigues.spendtalk.ui.viewmodel.UserViewModel
import com.google.accompanist.glide.rememberGlidePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import java.io.ByteArrayOutputStream

@Destination
@Composable
fun SignUpScreen(
    navigator: DestinationsNavigator,
    viewModel: UserViewModel = viewModel(),
) {
    var inputEmail by remember { mutableStateOf("") }
    var iconEmailState by remember { mutableStateOf(false) }
    var inputPassword by remember { mutableStateOf("") }
    var iconPasswordState by remember { mutableStateOf(false) }
    var iconShowPasswordState by remember { mutableStateOf(false) }
    var inputConfirmPassword by remember { mutableStateOf("") }
    var iconConfirmPasswordState by remember { mutableStateOf(false) }
    var iconConfirmShowPasswordState by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            imageUri = uri
        }

    Box(
        modifier = Modifier.background(Brush.verticalGradient(colors = listOf(PrimaryGreen, DarkGreen))).fillMaxSize()
    ) {
        IconButton(
            onClick = {
                launcher.launch("image/*")
            },
            modifier = Modifier.align(Alignment.TopCenter)
                .padding(top = 50.dp)
                .size(200.dp)
                .clip(CircleShape)
                .border(width = 7.dp, color = LightGreen, shape = CircleShape)
                .background(Color.White)
        ) {
            if (imageUri == null) {
                Icon(Icons.Default.AddAPhoto, contentDescription = stringResource(id = R.string.add_photo), modifier = Modifier.size(80.dp))
            } else {
                Image(
                    painter = rememberGlidePainter(imageUri),
                    contentScale = ContentScale.Crop,
                    contentDescription = null
                )
            }

        }
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 10.dp, vertical = 50.dp)
                .width(400.dp)
                .height(400.dp),
            elevation = 2.dp,
            shape = RoundedCornerShape(corner = CornerSize(14.dp))
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier
                        .padding(top = 20.dp, bottom = 5.dp)
                        .align(Alignment.CenterHorizontally),
                    text = stringResource(id = R.string.create_account),
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    modifier = Modifier
                        .onFocusChanged {
                            iconEmailState = !iconEmailState
                        }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .fillMaxWidth(),
                    value = inputEmail,
                    onValueChange = {
                        inputEmail = it
                    },
                    label = {
                        Text(text = stringResource(id = R.string.email))
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = stringResource(id = R.string.add),
                            tint = if (iconEmailState) Color.Gray else DarkGreen
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    )
                )

                OutlinedTextField(
                    modifier = Modifier
                        .onFocusChanged {
                            iconPasswordState = !iconPasswordState
                        }
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth(),
                    value = inputPassword,
                    onValueChange = {
                        inputPassword = it
                    },
                    label = {
                        Text(text = stringResource(id = R.string.password))
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = stringResource(id = R.string.add),
                            tint = if (iconPasswordState) Color.Gray else DarkGreen
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    visualTransformation = if (iconShowPasswordState) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { iconShowPasswordState = !iconShowPasswordState }) {
                            Icon(
                                imageVector = if (iconShowPasswordState) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Password Toggle"
                            )
                        }
                    }
                )
                OutlinedTextField(
                    modifier = Modifier
                        .onFocusChanged {
                            iconConfirmPasswordState = !iconConfirmPasswordState
                        }
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth(),
                    value = inputConfirmPassword,
                    onValueChange = {
                        inputConfirmPassword = it
                    },
                    label = {
                        Text(text = stringResource(id = R.string.confirm_password))
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = stringResource(id = R.string.add),
                            tint = if (iconConfirmPasswordState) Color.Gray else DarkGreen
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            signUp(
                                imageUri = imageUri,
                                viewModel = viewModel,
                                navigator = navigator,
                                context = context,
                                email = inputEmail,
                                password = inputPassword,
                                confirmPassword = inputConfirmPassword
                            )
                        }
                    ),
                    visualTransformation = if (iconConfirmShowPasswordState) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { iconConfirmShowPasswordState = !iconConfirmShowPasswordState }) {
                            Icon(
                                imageVector = if (iconConfirmShowPasswordState) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Password Toggle"
                            )
                        }
                    }
                )
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(115.dp)
                        .padding(vertical = 32.dp, horizontal = 46.dp)
                        .align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(backgroundColor = PrimaryGreen),
                    shape = RoundedCornerShape(corner = CornerSize(50.dp)),
                    onClick = {
                        signUp(
                            imageUri = imageUri,
                            viewModel = viewModel,
                            navigator = navigator,
                            context = context,
                            email = inputEmail,
                            password = inputPassword,
                            confirmPassword = inputConfirmPassword
                        )
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.submit).toUpperCase(Locale.current)
                    )
                }
                Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text(
                        text = stringResource(id = R.string.already_account),
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .align(Alignment.CenterVertically)
                    )
                    Text(
                        color = PrimaryGreen,
                        text = stringResource(id = R.string.log_in),
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .align(Alignment.CenterVertically)
                            .clickable {
                                navigator.popBackStack(
                                    SignInScreenDestination,
                                    inclusive = false
                                )
                            }
                    )
                }
            }
        }
    }
}

fun signUp(
    viewModel: UserViewModel,
    imageUri: Uri?,
    navigator: DestinationsNavigator,
    context: Context, email: String,
    password: String,
    confirmPassword: String,
) {
    when {
        TextUtils.isEmpty(
            email.trim { it <= ' ' }) -> {
            Toast.makeText(context, "Please Enter email", Toast.LENGTH_SHORT).show()
        }

        TextUtils.isEmpty(
            password.trim { it <= ' ' }) -> {
            Toast.makeText(context, "Please Enter password", Toast.LENGTH_SHORT).show()
        }
        else -> {
            email.trim { it <= ' ' }
            password.trim { it <= ' ' }
            confirmPassword.trim { it <= ' ' }

            if(password != confirmPassword) {
                Toast.makeText(context, "Password mismatch", Toast.LENGTH_SHORT)
                    .show()
            }

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "You were registered successfully", Toast.LENGTH_SHORT).show()

                        val reference = Firebase.database.getReference("user")

                        if (imageUri != null) {
                            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, imageUri))
                            } else {
                                MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
                            }

                            val baos = ByteArrayOutputStream()
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
                            val b = baos.toByteArray()
                            val stringBitmap = Base64.encodeToString(b, Base64.DEFAULT)

                            viewModel.addUser(user = User(id = reference.push().key!!, email = email, image = stringBitmap))
                        } else {
                            viewModel.addUser(user = User(id = reference.push().key!!, email = email))
                        }

                        navigator.navigate(SignInScreenDestination())

                    } else {
                        Toast.makeText(context, task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}


@Preview
@Composable
fun PreviewSignUpScreen() {

}