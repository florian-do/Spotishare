package do_f.com.spotishare.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import do_f.com.spotishare.R
import do_f.com.spotishare.api.model.SearchResponse
import do_f.com.spotishare.databinding.AdapterSearchBinding

class SearchAdapter : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    var items : List<SearchResponse> = emptyList()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {

        val binding : AdapterSearchBinding = DataBindingUtil.inflate(
            LayoutInflater.from(p0.context),
            R.layout.adapter_search, p0, false
        )

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {

    }

    class ViewHolder(val binding : AdapterSearchBinding) : RecyclerView.ViewHolder(binding.root)
}