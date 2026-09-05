package com.rewindvideo.plex.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ---- plex.tv account-linking (PIN based OAuth) ----

@Serializable
data class PlexPin(
    val id: Long,
    val code: String,
    @SerialName("authToken") val authToken: String? = null,
)

@Serializable
data class PlexResourceConnection(
    val uri: String,
    val local: Boolean = false,
    val relay: Boolean = false,
)

@Serializable
data class PlexResource(
    val name: String,
    @SerialName("clientIdentifier") val clientIdentifier: String,
    val provides: String = "",
    @SerialName("accessToken") val accessToken: String? = null,
    val connections: List<PlexResourceConnection> = emptyList(),
) {
    val isServer: Boolean get() = provides.split(",").contains("server")
}

// ---- Plex Media Server library browsing ----

@Serializable
data class PlexContainerResponse<T>(
    @SerialName("MediaContainer") val mediaContainer: PlexMediaContainer<T>,
)

@Serializable
data class PlexMediaContainer<T>(
    val size: Int = 0,
    @SerialName("Directory") val directories: List<PlexDirectory> = emptyList(),
    @SerialName("Metadata") val metadata: List<T> = emptyList(),
)

@Serializable
data class PlexDirectory(
    val key: String,
    val title: String,
    val type: String = "",
)

@Serializable
data class PlexGenre(val tag: String)

@Serializable
data class PlexRole(val tag: String, val role: String = "")

@Serializable
data class PlexPart(
    val key: String,
    val duration: Long = 0,
)

@Serializable
data class PlexMedia(
    val duration: Long = 0,
    val videoResolution: String = "",
    @SerialName("Part") val parts: List<PlexPart> = emptyList(),
)

/** A movie, show, season or episode as returned by /library/sections/{id}/all or /library/metadata/{key}. */
@Serializable
data class PlexItem(
    val ratingKey: String,
    val key: String,
    val guid: String = "",
    val type: String = "",
    val title: String,
    val summary: String = "",
    val year: Int? = null,
    val duration: Long = 0,
    val rating: Double? = null,
    val contentRating: String = "",
    val studio: String = "",
    val thumb: String? = null,
    val art: String? = null,
    val parentRatingKey: String? = null,
    val grandparentRatingKey: String? = null,
    val parentTitle: String? = null,
    val index: Int? = null,
    val leafCount: Int? = null,
    @SerialName("Genre") val genres: List<PlexGenre> = emptyList(),
    @SerialName("Role") val roles: List<PlexRole> = emptyList(),
    @SerialName("Media") val media: List<PlexMedia> = emptyList(),
) {
    val primaryGenre: String get() = genres.firstOrNull()?.tag ?: "General"
    val isMovie: Boolean get() = type == "movie"
    val isShow: Boolean get() = type == "show"
    val isSeason: Boolean get() = type == "season"
    val isEpisode: Boolean get() = type == "episode"
}

data class PlexServerSession(
    val serverName: String,
    val baseUrl: String,
    val accessToken: String,
)
