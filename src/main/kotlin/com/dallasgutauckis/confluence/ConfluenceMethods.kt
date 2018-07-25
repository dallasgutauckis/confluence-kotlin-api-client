package com.dallasgutauckis.confluence

import com.dallasgutauckis.confluence.search.SearchResponse

interface ConfluenceMethods {
    fun search(searchRequest: ConfluenceService.SearchRequest): SearchResponse
}
