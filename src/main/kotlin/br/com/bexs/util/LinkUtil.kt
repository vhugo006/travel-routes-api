package br.com.bexs.util

import javax.servlet.http.HttpServletResponse

class LinkUtil {

    companion object{
        val REL_COLLECTION = "collection"
        const val REL_NEXT = "next"
        const val REL_PREV = "prev"
        const val REL_FIRST = "first"
        const val REL_LAST = "last"
        const val PAGE = "page"

        fun createLinkHeader(uri: String, rel: String): String? {
            return "<$uri>; rel=\"$rel\""
        }
    }
}