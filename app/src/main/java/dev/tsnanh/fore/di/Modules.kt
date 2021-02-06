package dev.tsnanh.fore.di

import dev.tsnanh.fore.repository.FoReRepository
import dev.tsnanh.fore.repository.FoReRepositoryImpl
import dev.tsnanh.fore.ui.addfood.AddFoodViewModel
import dev.tsnanh.fore.ui.detail.DetailViewModel
import dev.tsnanh.fore.ui.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val modules = module {
    viewModel {
        HomeViewModel(get())
    }
    single<FoReRepository> {
        FoReRepositoryImpl()
    }
    viewModel {
        AddFoodViewModel(get())
    }
    viewModel {
        DetailViewModel(get())
    }
}
