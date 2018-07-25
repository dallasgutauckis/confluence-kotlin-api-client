package com.dallasgutauckis.confluence.search

import com.google.gson.annotations.SerializedName
import java.util.*


data class SearchResponse(
        val results: Array<SearchResult>,
        val start: Int,
        val limit: Int,
        val size: Int,
        val totalSize: Int,
        val cqlQuery: String,
        @SerializedName("_links") val links: Links
) {
    data class SearchResult(
            val content: SearchResult.Content,
            val title: String, // TODO why is this duplicated from content?
            val excerpt: String,
            /**
             * Relative path from base URL
             */
            val url: String,
            val breadcrumbs: Array<Any>, // TODO figure out what the inner type is
            val entityType: String, // TODO might be an enum
            val lastModified: Date,
            val friendlyLastModified: String,
            val score: Double
    ) {
        data class Content(
                val id: String,
                val type: String, // TODO probably should be an enum
                val status: String, // TODO maybe should be an enum
                val title: String,
                val childType: Any, // TODO figure out what this is
                @SerializedName("_links") val links: Content.Links
        ) {
            data class Links(
                    val webui: String,
                    val self: String,
                    val tinyui: String
            )
        }
    }

    data class Links(
            val base: String,
            val context: String
    )
}
