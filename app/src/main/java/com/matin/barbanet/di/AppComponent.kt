package com.matin.barbanet.di

import android.app.Application
import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.matin.barbanet.domain.location.LocationClient
import com.matin.barbanet.domain.location.LocationClientImp
import com.matin.common.utiles.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppComponent {

    @Provides
    fun provideSecurityHelper(): SecurityHelper {
        return SecurityHelperImpl()
    }

    @Provides
    fun providePreference(app: Application, securityHelper: SecurityHelper): Preference {
        return PreferenceImpl(app, securityHelper)
    }

    @Provides
    fun provideSettingManager(preference: Preference): SettingManager {
        return SettingManagerImpl(preference)
    }

    @Singleton
    @Provides
    fun provideUserSharedPref(context: Context): CommonSharedPref {
        return CommonSharedPref(context)
    }

    @Singleton
    @Provides
    fun provideApplicationContext(@ApplicationContext context: Context): Context = context

    @Singleton
    @Provides
    fun provideFusedLocation(context: Context): FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)


    @Provides
    fun provideLocationClient(
        context: Context,
        fusedLocationProviderClient: FusedLocationProviderClient
    )
            : LocationClient {
        return LocationClientImp(context, fusedLocationProviderClient)
    }
}