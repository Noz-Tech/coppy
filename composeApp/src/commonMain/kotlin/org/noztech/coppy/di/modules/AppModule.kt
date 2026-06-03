package org.noztech.coppy.di.modules

import com.russhwolf.settings.Settings
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.noztech.AppDatabase
import org.noztech.coppy.core.AppSettings
import org.noztech.coppy.core.database.DatabaseDriverFactory
import org.noztech.coppy.core.database.DatabaseHelper
import org.noztech.coppy.core.database.SampleDataSeeder
import org.noztech.coppy.core.database.VaultDataResetter
import org.noztech.coppy.core.database.dao.GroupDao
import org.noztech.coppy.core.database.dao.ImageDao
import org.noztech.coppy.core.database.dao.ItemDao
import org.noztech.coppy.feature.home.data.GroupRepositoryImpl
import org.noztech.coppy.feature.home.data.ImageRepositoryImpl
import org.noztech.coppy.feature.home.data.ItemRepositoryImpl
import org.noztech.coppy.feature.home.domain.usecase.CreateGroupUseCase
import org.noztech.coppy.feature.home.domain.usecase.CreateItemUseCase
import org.noztech.coppy.feature.home.domain.usecase.DeleteGroupUseCase
import org.noztech.coppy.feature.home.domain.usecase.GetGroupsUseCase
import org.noztech.coppy.feature.home.domain.usecase.GetHiddenItemsUseCase
import org.noztech.coppy.feature.home.domain.usecase.GetItemCountByGroupUseCase
import org.noztech.coppy.feature.home.domain.usecase.GetItemCountForGroupUseCase
import org.noztech.coppy.feature.home.domain.usecase.GetItemsUseCase
import org.noztech.coppy.feature.home.domain.respository.GroupRepository
import org.noztech.coppy.feature.home.domain.respository.ImageRepository
import org.noztech.coppy.feature.home.domain.respository.ItemRepository
import org.noztech.coppy.feature.home.domain.usecase.DeleteItemUseCase
import org.noztech.coppy.feature.home.domain.usecase.GetItemByIdUseCase
import org.noztech.coppy.feature.home.domain.usecase.ToggleItemVisibilityUseCase
import org.noztech.coppy.feature.home.domain.usecase.UpdateItemUseCase
import org.noztech.coppy.feature.home.presentation.viewmodels.CreateListViewModel
import org.noztech.coppy.feature.home.presentation.viewmodels.EntryDetailViewModel
import org.noztech.coppy.feature.home.presentation.viewmodels.GroupViewModel
import org.noztech.coppy.feature.home.presentation.viewmodels.HomeViewModel
import org.noztech.coppy.feature.welcome.presentation.WelcomeViewModel

val appModule = module {
    single { Settings() }
    single { AppSettings(get()) }
    // single<FirebaseManager> { PlatformFirebaseManager }
    single {
        AppDatabase(get<DatabaseDriverFactory>().createDriver()).also { database ->
            SampleDataSeeder(
                database = database,
                appSettings = get()
            ).seedIfNeeded()
        }
    }
    single<DatabaseHelper> { DatabaseHelper(get()) }
    single { VaultDataResetter(get()) }
    single { GroupDao(get<AppDatabase>().entryGroupQueries) }
    single { ItemDao(get<AppDatabase>().entryItemQueries) }
    single { ImageDao(get<AppDatabase>().entryImageQueries) }

    singleOf(::GroupRepositoryImpl) { bind<GroupRepository>() }
    singleOf(::ItemRepositoryImpl) { bind<ItemRepository>() }
    singleOf(::ImageRepositoryImpl) { bind<ImageRepository>() }
    single { CreateGroupUseCase(get()) }
    single { DeleteGroupUseCase(get()) }
    single { GetGroupsUseCase(get()) }
    single { GetHiddenItemsUseCase(get()) }
    single { GetItemByIdUseCase(get()) }
    single { GetItemCountByGroupUseCase(get()) }
    single { GetItemCountForGroupUseCase(get()) }
    single { GetItemsUseCase(get()) }
    single { CreateItemUseCase(get()) }
    single { UpdateItemUseCase(get()) }
    single { ToggleItemVisibilityUseCase(get()) }
    single { DeleteItemUseCase(get()) }

    viewModel { WelcomeViewModel(get()) }
    viewModel { HomeViewModel(get(), get(), get(), get(), get()) }
    viewModel { GroupViewModel(get(), get(), get(), get()) }
    viewModel { CreateListViewModel(get(), get(), get(), get()) }
    viewModel { EntryDetailViewModel(get(), get()) }
}
