package do_f.com.spotishare.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import do_f.com.spotishare.R
import do_f.com.spotishare.databinding.AdapterQueueBinding

class QueueAdapter : RecyclerView.Adapter<QueueAdapter.ViewHolder>() {

    var items : List<String> = emptyList()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val binding : AdapterQueueBinding = DataBindingUtil.inflate(
            LayoutInflater.from(p0.context),
            R.layout.adapter_queue,
            p0, false
        )

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {

    }

    class ViewHolder(val binding : AdapterQueueBinding) : RecyclerView.ViewHolder(binding.root)
}