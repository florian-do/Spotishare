package do_f.com.spotishare.datasource

import android.arch.paging.PageKeyedDataSource
import do_f.com.spotishare.api.model.SearchResponse
import do_f.com.spotishare.api.service.SearchService


class SearchDS(api: SearchService) : PageKeyedDataSource<Int, SearchResponse>() {
    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, SearchResponse>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, SearchResponse>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, SearchResponse>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun callApi(limit : Int, callback: () -> Unit) {

    }

}