package do_f.com.spotishare.datasource

import android.arch.paging.DataSource
import do_f.com.spotishare.api.model.SearchResponse
import do_f.com.spotishare.api.service.SearchService

class SearchDSFactory(val api: SearchService) : DataSource.Factory<Int, SearchResponse>() {

    val source = SearchDS(api)

    override fun create(): DataSource<Int, SearchResponse> {
        return source
    }
}