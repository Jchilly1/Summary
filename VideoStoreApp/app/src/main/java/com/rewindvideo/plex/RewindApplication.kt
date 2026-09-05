package com.rewindvideo.plex

import android.app.Application
import com.rewindvideo.plex.data.PlexRepository
import com.rewindvideo.plex.data.TokenStore

/** Tiny hand-rolled service locator -- no DI framework needed for an app this size. */
class RewindApplication : Application() {

    lateinit var tokenStore: TokenStore
        private set

    lateinit var repository: PlexRepository
        private set

    override fun onCreate() {
        super.onCreate()
        tokenStore = TokenStore(this)
        repository = PlexRepository(tokenStore)
    }
}
