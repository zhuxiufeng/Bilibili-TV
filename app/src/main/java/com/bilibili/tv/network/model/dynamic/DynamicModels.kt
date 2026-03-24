package com.bilibili.tv.network.model.dynamic

import com.bilibili.tv.network.model.VideoItem
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DynamicResponseData(
    @Json(name = "has_more") val hasMore: Boolean,
    @Json(name = "items") val items: List<DynamicItem>,
    @Json(name = "offset") val offset: String
)

@JsonClass(generateAdapter = true)
data class DynamicItem(
    @Json(name = "id_str") val idStr: String,
    @Json(name = "type") val type: String, // E.g., "DYNAMIC_TYPE_AV" (video), "DYNAMIC_TYPE_DRAW" (images)
    @Json(name = "visible") val visible: Boolean,
    @Json(name = "modules") val modules: DynamicModules
)

@JsonClass(generateAdapter = true)
data class DynamicModules(
    @Json(name = "module_author") val moduleAuthor: DynamicAuthor,
    @Json(name = "module_dynamic") val moduleDynamic: DynamicContent
)

@JsonClass(generateAdapter = true)
data class DynamicAuthor(
    @Json(name = "mid") val mid: Long,
    @Json(name = "face") val face: String,
    @Json(name = "name") val name: String,
    @Json(name = "pub_time") val pubTime: String,
    @Json(name = "pub_action") val pubAction: String
)

@JsonClass(generateAdapter = true)
data class DynamicContent(
    @Json(name = "major") val major: DynamicMajor?,
    @Json(name = "desc") val desc: DynamicDesc?
)

@JsonClass(generateAdapter = true)
data class DynamicDesc(
    @Json(name = "text") val text: String?
)

@JsonClass(generateAdapter = true)
data class DynamicMajor(
    @Json(name = "type") val type: String,
    @Json(name = "archive") val archive: DynamicArchive?, // Used for Videos
    @Json(name = "draw") val draw: DynamicDraw? // Used for Images
)

// Video structure inside a dynamic post
@JsonClass(generateAdapter = true)
data class DynamicArchive(
    @Json(name = "aid") val aid: String,
    @Json(name = "bvid") val bvid: String,
    @Json(name = "cover") val cover: String,
    @Json(name = "desc") val desc: String,
    @Json(name = "title") val title: String,
    @Json(name = "duration_text") val durationText: String? = ""
)

// Image structured inside a dynamic post
@JsonClass(generateAdapter = true)
data class DynamicDraw(
    @Json(name = "items") val items: List<DynamicImageItem>
)

@JsonClass(generateAdapter = true)
data class DynamicImageItem(
    @Json(name = "src") val src: String,
    @Json(name = "width") val width: Int,
    @Json(name = "height") val height: Int
)
