package com.novacut.editor

import android.app.Application
import androidx.media3.common.util.UnstableApi
import com.novacut.editor.core.di.ServiceLocator

@UnstableApi
class NovaCutApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ServiceLocator.initialize(this)
    }
}
