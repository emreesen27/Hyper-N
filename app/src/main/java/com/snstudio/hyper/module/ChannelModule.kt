package com.snstudio.hyper.module

import android.app.Application
import com.snstudio.hyper.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.plugin.common.MethodChannel
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChannelModule {
    @Singleton
    @Provides
    fun provideMethodChannel(application: Application): MethodChannel {
        val flutterEngine = FlutterEngine(application.applicationContext)
        flutterEngine.dartExecutor.executeDartEntrypoint(DartExecutor.DartEntrypoint.createDefault())
        return MethodChannel(flutterEngine.dartExecutor.binaryMessenger, BuildConfig.MY_CHANNEL)
    }
}
