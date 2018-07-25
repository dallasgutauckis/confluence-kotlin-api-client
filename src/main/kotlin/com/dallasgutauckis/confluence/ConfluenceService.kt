package com.dallasgutauckis.confluence

import com.dallasgutauckis.confluence.search.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.CompletableFuture

interface ConfluenceService {
    //  curl -D- -u "dallas@seatgeek.com:p00p" -X GET -H "Content-Type: application/json" https://seatgeek.atlassian.net/wiki/rest/api/search\?cql\=\(type\=page\)
    @GET("rest/api/search")
    fun search(@Query("cql") cql: String): CompletableFuture<SearchResponse>

    class SearchRequestBuilder {
        private var query: String = ""

        fun group(body: (groupBuilder: SearchRequestBuilder) -> SearchRequestBuilder): SearchRequestBuilder {
            // TODO I _thuink_ this is right
            val group = SearchRequestBuilder()
            query += " (%s)".format(body(group).query)
            return this
        }

        fun and(): SearchRequestBuilder {
            query += " and"
            return this
        }

        fun or(): SearchRequestBuilder {
            query += " and"
            return this
        }

        fun text(text: String): SearchRequestBuilder {
            query += " text ~ \"%s\"".format(escapeInput(text))
            return this;
        }

        fun type(type: String): SearchRequestBuilder {
            query += " type = \"%s\"".format(escapeInput(type))
            return this
        }

        fun label(label: String): SearchRequestBuilder {
            query += " label = \"%s\"".format(escapeInput(label))
            return this
        }

        fun title(title: String): SearchRequestBuilder {
            query += " title = \"%s\"".format(escapeInput(title))
            return this
        }

        fun build(): SearchRequest {
            return SearchRequest(query)
        }

        private fun escapeInput(input: String): String {
            return input.replace("\"", "\\\"")
        }

        override fun toString() = build().cql
    }

    data class SearchRequest(val cql: String) {
        companion object {
            fun builder(): SearchRequestBuilder {
                return SearchRequestBuilder()
            }
        }
    }
}