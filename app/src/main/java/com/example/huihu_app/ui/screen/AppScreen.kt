package com.example.huihu_app.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.huihu_app.Nav
import com.example.huihu_app.state.AuthState
import com.example.huihu_app.ui.AppViewModelProvider
import com.example.huihu_app.ui.viewModel.AppViewModel

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun AppScreen(viewModel: AppViewModel = viewModel(factory = AppViewModelProvider.FACTORY)) {
    val authState by viewModel.authState.collectAsStateWithLifecycle(Nav.Splash)

    val backStack = rememberNavBackStack()
    var authToken by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(authState) {
        backStack.clear()
        when(authState) {
            is AuthState.Loading -> {
                backStack.add(Nav.Splash)
            }
            is AuthState.UnAuthenticated -> {
                backStack.add(Nav.Login)
            }
            is AuthState.Authenticated -> {
                authToken = (authState as AuthState.Authenticated).token
                if ((authState as AuthState.Authenticated).isNewUser) {
                    backStack.add(Nav.NewPerson)
                } else {
                    backStack.add(Nav.Home)
                }
            }
        }
    }

    if (!backStack.isEmpty()) {
        NavDisplay(
            backStack = backStack,
            entryProvider = entryProvider {
                entry<Nav.Login>() {
                    LoginScreen(
                        onRegister = { backStack.add(Nav.Register) }
                    )
                }
                entry<Nav.Register>() {
                    RegisterScreen(
                        onLogin = {
                            if (backStack.size > 1) {
                                backStack.removeLast()
                            } else {
                                backStack.add(Nav.Login)
                            }
                        }
                    )
                }
                entry<Nav.Home>() {
                    HomeScreen(
                        token = authToken!!,
                        onCreateTopic = { backStack.add(Nav.CreateTopic) },
                        onWriteComment = { topicId -> backStack.add(Nav.WriteComment(topicId)) },
                        onOpenTopicDetail = { topic -> backStack.add(Nav.TopicDetail(topic)) },
                        onEditProfile = { backStack.add(Nav.EditProfile) }
                    )
                }
                entry<Nav.EditProfile>() {
                    EditProfileScreen(
                        token = authToken!!,
                        onBack = {
                            if (backStack.size > 1) {
                                backStack.removeLast()
                            } else {
                                backStack.add(Nav.Home)
                            }
                        }
                    )
                }
                entry<Nav.TopicDetail> { nav ->
                    TopicDetailScreen(
                        token = authToken!!,
                        topic = nav.topic,
                        onBack = {
                            if (backStack.size > 1) {
                                backStack.removeLast()
                            } else {
                                backStack.add(Nav.Home)
                            }
                        },
                        onWriteComment = { topicId -> backStack.add(Nav.WriteComment(topicId)) },
                        onOpenTopicDetail = { topic -> backStack.add(Nav.TopicDetail(topic)) }
                    )
                }
                entry<Nav.CreateTopic>() {
                    CreateTopicScreen(
                        token = authToken!!,
                        onCreated = {
                            if (backStack.size > 1) {
                                backStack.removeLast()
                            } else {
                                backStack.add(Nav.Home)
                            }
                        }
                    )
                }
                entry<Nav.WriteComment> { nav ->
                    CreateTopicScreen(
                        token = authToken!!,
                        commentToId = nav.commentToId,
                        screenTitle = "Write Comment",
                        submitButtonText = "Post Comment",
                        onCreated = {
                            if (backStack.size > 1) {
                                backStack.removeLast()
                            } else {
                                backStack.add(Nav.Home)
                            }
                        }
                    )
                }
                entry<Nav.NewPerson>() {
                    NewPersonScreen(authToken!!)
                }
                entry<Nav.Splash>() {
                    SplashScreen()
                }
            }
        )
    }

}
