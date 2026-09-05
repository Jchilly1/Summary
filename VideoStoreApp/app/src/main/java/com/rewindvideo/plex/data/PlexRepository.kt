package com.rewindvideo.plex.data

import com.rewindvideo.plex.data.model.PlexItem
import com.rewindvideo.plex.data.model.PlexPin
import com.rewindvideo.plex.data.model.PlexResource
import com.rewindvideo.plex.data.remote.NetworkModule
import com.rewindvideo.plex.data.remote.PlexServerApi
import kotlinx.coroutines.delay
import java.net.URLEncoder
import java.util.UUID

/** A shelf of movies/shows that share a genre, the way a real store groups its aisles. */
data class Shelf(val genre: String, val items: List<PlexItem>)

data class LibrarySection(val key: String, val title: String, val type: String)

class PlexRepository(private val tokenStore: TokenStore) {

    private val authApi by lazy { NetworkModule.createAuthApi(tokenStore.clientIdentifier) }

    private var serverApi: PlexServerApi? = restoreServerApiIfPossible()

    private fun restoreServerApiIfPossible(): PlexServerApi? {
        val baseUrl = tokenStore.serverBaseUrl ?: return null
        val token = tokenStore.serverAccessToken ?: return null
        return NetworkModule.createServerApi(baseUrl, token, tokenStore.clientIdentifier)
    }

    val isSignedIn: Boolean get() = tokenStore.hasServerSession

    // ---------- Sign-in (plex.tv PIN / "link" flow) ----------

    data class LinkRequest(val pin: PlexPin, val linkUrl: String)

    suspend fun startAccountLink(): LinkRequest {
        val pin = authApi.requestPin(strong = true)
        val clientId = tokenStore.clientIdentifier
        val encodedProduct = URLEncoder.encode("Rewind Video Club", "UTF-8")
        val linkUrl = "https://app.plex.tv/auth#?clientID=$clientId&code=${pin.code}" +
            "&context%5Bdevice%5D%5Bproduct%5D=$encodedProduct"
        return LinkRequest(pin, linkUrl)
    }

    /** Polls plex.tv until the user finishes signing in on the linkUrl, or [timeoutMs] elapses. */
    suspend fun awaitAccountLink(pinId: Long, timeoutMs: Long = 180_000): Boolean {
        val deadline = System.currentTimeMillis() + timeoutMs
        while (System.currentTimeMillis() < deadline) {
            val result = authApi.checkPin(pinId)
            if (!result.authToken.isNullOrBlank()) {
                tokenStore.accountAuthToken = result.authToken
                return true
            }
            delay(2000)
        }
        return false
    }

    suspend fun discoverServers(): List<PlexResource> {
        return authApi.getResources().filter { it.isServer && it.accessToken != null }
    }

    fun selectServer(resource: PlexResource) {
        val connection = resource.connections
            .sortedWith(compareBy({ it.relay }, { !it.local }))
            .firstOrNull() ?: error("Server \"${resource.name}\" has no reachable connection")
        val token = resource.accessToken ?: error("No access token for \"${resource.name}\"")

        tokenStore.serverName = resource.name
        tokenStore.serverBaseUrl = connection.uri
        tokenStore.serverAccessToken = token
        serverApi = NetworkModule.createServerApi(connection.uri, token, tokenStore.clientIdentifier)
    }

    fun signOut() = tokenStore.clearAll()

    // ---------- Library browsing ----------

    private fun requireApi(): PlexServerApi =
        serverApi ?: error("No Plex server selected yet")

    suspend fun getLibrarySections(): List<LibrarySection> =
        requireApi().getSections().mediaContainer.directories
            .filter { it.type == "movie" || it.type == "show" }
            .map { LibrarySection(it.key, it.title, it.type) }

    /** Every item in a library section, grouped into video-store-style genre shelves. */
    suspend fun getShelves(sectionKey: String): List<Shelf> {
        val items = requireApi().getSectionItems(sectionKey).mediaContainer.metadata
        return items.groupBy { it.primaryGenre }
            .toSortedMap()
            .map { (genre, itemsInGenre) -> Shelf(genre, itemsInGenre) }
    }

    suspend fun getMetadata(ratingKey: String): PlexItem =
        requireApi().getMetadata(ratingKey).mediaContainer.metadata.first()

    /** Seasons of a show, or episodes of a season. */
    suspend fun getChildren(ratingKey: String): List<PlexItem> =
        requireApi().getChildren(ratingKey).mediaContainer.metadata

    // ---------- Images & streaming ----------

    fun imageUrl(path: String?): String? {
        if (path.isNullOrBlank()) return null
        val baseUrl = tokenStore.serverBaseUrl ?: return null
        val token = tokenStore.serverAccessToken ?: return null
        return "$baseUrl$path?X-Plex-Token=$token"
    }

    /** A Plex "Universal Transcode" HLS URL: works for basically any source format/codec. */
    fun streamUrl(item: PlexItem): String? {
        val baseUrl = tokenStore.serverBaseUrl ?: return null
        val token = tokenStore.serverAccessToken ?: return null
        val encodedPath = URLEncoder.encode(item.key, "UTF-8")
        val session = UUID.randomUUID().toString()
        return "$baseUrl/video/:/transcode/universal/start.m3u8" +
            "?path=$encodedPath" +
            "&mediaIndex=0&partIndex=0" +
            "&protocol=hls" +
            "&fastSeek=1" +
            "&directPlay=1&directStream=1" +
            "&subtitleSize=100&audioBoost=100" +
            "&session=$session" +
            "&X-Plex-Token=$token"
    }
}
