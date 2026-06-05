package org.noztek.coppy.di.modules

import com.russhwolf.settings.Settings
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.noztek.AppDatabase
import org.noztek.coppy.core.AppSettings
import org.noztek.coppy.core.database.DatabaseDriverFactory
import org.noztek.coppy.core.database.DatabaseHelper
import org.noztek.coppy.core.database.SampleDataSeeder
import org.noztek.coppy.core.database.VaultDataResetter
import org.noztek.coppy.core.database.dao.GroupDao
import org.noztek.coppy.core.database.dao.EntryFieldDao
import org.noztek.coppy.core.database.dao.ItemDao
import org.noztek.coppy.feature.home.data.EntryFieldRepositoryImpl
import org.noztek.coppy.feature.home.data.GroupRepositoryImpl
import org.noztek.coppy.feature.home.data.ItemRepositoryImpl
import org.noztek.coppy.feature.home.domain.usecase.CreateGroupUseCase
import org.noztek.coppy.feature.home.domain.usecase.CreateItemUseCase
import org.noztek.coppy.feature.home.domain.usecase.DeleteGroupUseCase
import org.noztek.coppy.feature.home.domain.usecase.GetGroupsUseCase
import org.noztek.coppy.feature.home.domain.usecase.GetHiddenItemsUseCase
import org.noztek.coppy.feature.home.domain.usecase.GetItemCountByGroupUseCase
import org.noztek.coppy.feature.home.domain.usecase.GetItemCountForGroupUseCase
import org.noztek.coppy.feature.home.domain.usecase.GetItemsUseCase
import org.noztek.coppy.feature.home.domain.respository.GroupRepository
import org.noztek.coppy.feature.home.domain.respository.EntryFieldRepository
import org.noztek.coppy.feature.home.domain.respository.ItemRepository
import org.noztek.coppy.feature.home.domain.usecase.DeleteItemUseCase
import org.noztek.coppy.feature.home.domain.usecase.DeleteEntryFieldsUseCase
import org.noztek.coppy.feature.home.domain.usecase.GetEntryFieldsUseCase
import org.noztek.coppy.feature.home.domain.usecase.GetItemByIdUseCase
import org.noztek.coppy.feature.home.domain.usecase.ToggleItemVisibilityUseCase
import org.noztek.coppy.feature.home.domain.usecase.UpdateGroupUseCase
import org.noztek.coppy.feature.home.domain.usecase.UpdateItemUseCase
import org.noztek.coppy.feature.home.domain.usecase.ReplaceEntryFieldsUseCase
import org.noztek.coppy.feature.home.presentation.viewmodels.CreateListViewModel
import org.noztek.coppy.feature.home.presentation.viewmodels.EntryDetailViewModel
import org.noztek.coppy.feature.home.presentation.viewmodels.GroupViewModel
import org.noztek.coppy.feature.home.presentation.viewmodels.HomeViewModel
import org.noztek.coppy.feature.welcome.presentation.WelcomeViewModel

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
    single { EntryFieldDao(get<AppDatabase>().entryFieldQueries) }
    single { ItemDao(get<AppDatabase>().entryItemQueries) }

    singleOf(::GroupRepositoryImpl) { bind<GroupRepository>() }
    singleOf(::EntryFieldRepositoryImpl) { bind<EntryFieldRepository>() }
    singleOf(::ItemRepositoryImpl) { bind<ItemRepository>() }
    single { CreateGroupUseCase(get()) }
    single { UpdateGroupUseCase(get()) }
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
    single { ReplaceEntryFieldsUseCase(get()) }
    single { DeleteEntryFieldsUseCase(get()) }
    single { GetEntryFieldsUseCase(get()) }

    viewModel { WelcomeViewModel(get()) }
    viewModel { HomeViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { GroupViewModel(get(), get(), get(), get(), get()) }
    viewModel { CreateListViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { EntryDetailViewModel(get(), get(), get()) }
}
