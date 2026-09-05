package com.rewindvideo.plex.data.remote

import com.rewindvideo.plex.data.model.PlexContainerResponse
import com.rewindvideo.plex.data.model.PlexItem
import retrofit2.http.GET
import retrofit2.http.Path

/** Talks directly to a user's own Plex Media Server (discovered via [PlexTvAuthApi]). */
interface PlexServerApi {

    @GET("library/sections")
    suspend fun getSections(): PlexContainerResponse<PlexItem>

    @GET("library/sections/{sectionKey}/all")
    suspend fun getSectionItems(@Path("sectionKey") sectionKey: String): PlexContainerResponse<PlexItem>

    @GET("library/metadata/{ratingKey}")
    suspend fun getMetadata(@Path("ratingKey") ratingKey: String): PlexContainerResponse<PlexItem>

    @GET("library/metadata/{ratingKey}/children")
    suspend fun getChildren(@Path("ratingKey") ratingKey: String): PlexContainerResponse<PlexItem>
}
