package com.dallasgutauckis.confluence

import com.dallasgutauckis.confluence.search.SearchResponse

class ConfluenceMethodsRetrofit(val restClient: ConfluenceService) : ConfluenceMethods {
    override fun search(searchRequest: ConfluenceService.SearchRequest): SearchResponse {
        return restClient.search(searchRequest.cql).get()
    }

}