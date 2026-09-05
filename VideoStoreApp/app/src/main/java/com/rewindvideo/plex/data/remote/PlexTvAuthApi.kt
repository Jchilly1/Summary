package com.rewindvideo.plex.data.remote

import com.rewindvideo.plex.data.model.PlexPin
import com.rewindvideo.plex.data.model.PlexResource
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * plex.tv account endpoints used for the PIN-based OAuth "link" flow, and for
 * discovering the user's own Plex Media Servers once they're signed in.
 * Docs: https://forums.plex.tv/t/authenticating-with-plex/609370
 */
interface PlexTvAuthApi {

    @POST("api/v2/pins")
    suspend fun requestPin(@Query("strong") strong: Boolean = true): PlexPin

    @GET("api/v2/pins/{id}")
    suspend fun checkPin(@Path("id") id: Long): PlexPin

    @GET("api/v2/resources")
    suspend fun getResources(
        @Query("includeHttps") includeHttps: Int = 1,
        @Query("includeRelay") includeRelay: Int = 1,
    ): List<PlexResource>
}
