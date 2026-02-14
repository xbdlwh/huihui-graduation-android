package com.example.huihu_app.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.huihu_app.MainApp
import com.example.huihu_app.ui.viewModel.AppViewModel
import com.example.huihu_app.ui.viewModel.AuthViewModel
import com.example.huihu_app.ui.viewModel.CreateTopicViewModel
import com.example.huihu_app.ui.viewModel.EditProfileViewModel
import com.example.huihu_app.ui.viewModel.FoodRecommendationViewModel
import com.example.huihu_app.ui.viewModel.ForumViewModel
import com.example.huihu_app.ui.viewModel.HomeViewModel
import com.example.huihu_app.ui.viewModel.MineViewModel
import com.example.huihu_app.ui.viewModel.NewPersonViewModel
import com.example.huihu_app.ui.viewModel.TopicDetailViewModel

class AppViewModelProvider {
    companion object {
        val FACTORY = viewModelFactory {
            initializer {
                AppViewModel(
                    container().localStoreRepository
                )
            }
            initializer {
                HomeViewModel(
                    container().localStoreRepository,
                    container().foodRepository
                )
            }
            initializer {
                AuthViewModel(
                    container().authRepository,
                    container().localStoreRepository
                )
            }
            initializer {
                NewPersonViewModel(
                    container().foodRepository,
                    container().localStoreRepository
                )
            }
            initializer {
                FoodRecommendationViewModel(
                    container().foodRepository,
                    container().localStoreRepository
                )
            }
            initializer {
                ForumViewModel(
                    container().topicRepository
                )
            }
            initializer {
                CreateTopicViewModel(
                    container().topicRepository
                )
            }
            initializer {
                TopicDetailViewModel(
                    container().topicRepository
                )
            }
            initializer {
                MineViewModel(
                    container().authRepository
                )
            }
            initializer {
                EditProfileViewModel(
                    container().authRepository,
                    container().topicRepository
                )
            }
        }
    }
}
fun CreationExtras.container() = (this[APPLICATION_KEY] as MainApp).container
