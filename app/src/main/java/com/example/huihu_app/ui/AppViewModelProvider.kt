package com.example.huihu_app.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.huihu_app.MainApp
import com.example.huihu_app.ui.viewModel.AppViewModel
import com.example.huihu_app.ui.viewModel.AddSuggestionViewModel
import com.example.huihu_app.ui.viewModel.AuthViewModel
import com.example.huihu_app.ui.viewModel.CreateTopicViewModel
import com.example.huihu_app.ui.viewModel.EditProfileViewModel
import com.example.huihu_app.ui.viewModel.FoodRecommendationViewModel
import com.example.huihu_app.ui.viewModel.FoodLikedViewModel
import com.example.huihu_app.ui.viewModel.FoodAttrViewModel
import com.example.huihu_app.ui.viewModel.ForumViewModel
import com.example.huihu_app.ui.viewModel.HomeViewModel
import com.example.huihu_app.ui.viewModel.MineViewModel
import com.example.huihu_app.ui.viewModel.NewPersonViewModel
import com.example.huihu_app.ui.viewModel.SuggestionViewModel
import com.example.huihu_app.ui.viewModel.TopicDetailViewModel
import com.example.huihu_app.ui.viewModel.TopicManageViewModel
import com.example.huihu_app.ui.viewModel.UserViewModel
import com.example.huihu_app.ui.viewModel.CalorieGoalViewModel

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
                    container().foodRepository,
                    container().authRepository
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
                FoodLikedViewModel(
                    container().foodRepository
                )
            }
            initializer {
                FoodAttrViewModel(
                    container().foodRepository
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
                TopicManageViewModel(
                    container().topicRepository
                )
            }
            initializer {
                MineViewModel(
                    container().authRepository,
                    container().foodRepository,
                    container().userRepository
                )
            }
            initializer {
                EditProfileViewModel(
                    container().authRepository,
                    container().topicRepository
                )
            }
            initializer {
                SuggestionViewModel(
                    container().suggestionRepository
                )
            }
            initializer {
                AddSuggestionViewModel(
                    container().suggestionRepository,
                    container().restaurantRepository,
                    container().topicRepository
                )
            }
            initializer {
                UserViewModel(
                    container().userRepository
                )
            }
            initializer {
                CalorieGoalViewModel(
                    container().calorieGoalRepository
                )
            }
        }
    }
}
fun CreationExtras.container() = (this[APPLICATION_KEY] as MainApp).container
