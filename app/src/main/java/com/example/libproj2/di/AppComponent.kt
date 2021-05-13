package com.example.libproj2.di

import android.content.Context
import com.example.libproj2.MainActivity
import com.example.libproj2.repositories.InAppNotificationsRepository
import com.example.libproj2.repositories.UserFromNotificationRepository
import com.example.libproj2.repositories.UserRepository
import com.example.libproj2.services.BookActionNotificationService
import com.example.libproj2.services.FirebaseMes
import com.example.libproj2.ui.fragments.*
import com.example.libproj2.ui.fragments.catalog.CatalogFragment
import com.example.libproj2.ui.fragments.notifications.NotificationsAdapter
import com.example.libproj2.ui.fragments.notifications.NotificationsFragment
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Inject
import javax.inject.Singleton


class A @Inject constructor(){
    var data = 0
}
class B @Inject constructor(val a: A){}

@Module
class ServicesModule{
    @Provides
    fun provideBookActionNotificationService(inAppNotificationsRepository: InAppNotificationsRepository)
    = BookActionNotificationService(inAppNotificationsRepository)
}

@Module
class UserModule{
    @Provides @Singleton
    fun provideUserRepo(context: Context) : UserRepository = UserRepository(context)

    @Provides @Singleton
    fun provideInAppNotificationsRepository(userRepository: UserRepository) = InAppNotificationsRepository(userRepository)

    @Provides @Singleton
    fun provideUserFromNotificationRepository() = UserFromNotificationRepository()
}

@Module
class ContextModule(val context: Context){
    @Provides @Singleton
    fun provideContext() : Context{
        return context
    }
}

@Component(modules = [ContextModule::class, UserModule::class, ServicesModule::class])
@Singleton
interface AppComponent {
    fun getB() : B
    fun inject(mainActivity: MainActivity)
    fun inject(loginFragment: LoginFragment)
    fun inject(notificationsFragment: NotificationsFragment)
    fun inject(bookDetailsTakenFragment: BookDetailsTakenFragment)
    fun inject(profileFragment: ProfileFragment)
    fun inject(bookDetailsToTakeFragment: BookDetailsToTakeFragment)
    fun inject(libraryCardFragment: LibraryCardFragment)
    fun inject(netFragment: NetFragment)
    fun inject(catalogFragment: CatalogFragment)

    fun inject(notificationsAdapter: NotificationsAdapter)
    fun inject(firebaseMes: FirebaseMes)
}