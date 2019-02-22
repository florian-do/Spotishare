package do_f.com.spotishare.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import do_f.com.spotishare.App
import do_f.com.spotishare.api.model.SearchResponse
import do_f.com.spotishare.api.repository.SearchRepo
import do_f.com.spotishare.api.service.SearchService
import do_f.com.spotishare.datasource.SearchDSFactory

class SearchViewModel : ViewModel() {

    private var data :LiveData<PagedList<SearchResponse>>
    private var searchDSFactory : SearchDSFactory

    private val api : SearchService = App.retrofit.create(SearchService::class.java)
    private val config : PagedList.Config = PagedList.Config.Builder()
        .setPageSize(10)
        .setInitialLoadSizeHint(10)
        .build()

    private val repo : SearchRepo

    init {
        repo = SearchRepo()
        searchDSFactory = SearchDSFactory(api)
        data = LivePagedListBuilder(searchDSFactory, config).build()
    }

    fun search(q : String) : LiveData<SearchResponse> {
        return repo.search(q)
    }
}