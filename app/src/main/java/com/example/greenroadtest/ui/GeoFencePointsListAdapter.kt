package com.example.greenroadtest

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.greenroadtest.databinding.ItemGeoFencePointBinding
import com.example.greenroadtest.model.GeoFenceModel
import com.example.greenroadtest.ui.*
import org.koin.core.component.KoinApiExtension


@KoinApiExtension
class GeoFencePointsListAdapter(private val itemClickListener: OnItemClicked<GeoFencePointViewModel>) :
    ListAdapter<GeoFencePointViewModel, GeoFenceViewHolder>(GeoFanceModelDiffUtill()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GeoFenceViewHolder {
        val binding = ItemGeoFencePointBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return GeoFenceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GeoFenceViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)

        holder.itemView.setOnClickListener {
            itemClickListener.onItemClicked(item)
            notifyDataSetChanged()
        }
    }

}

@KoinApiExtension
class GeoFenceViewHolder(val binder: ItemGeoFencePointBinding) :
    RecyclerView.ViewHolder(binder.root) {

     fun bind(item: GeoFencePointViewModel) {
        binder.viewModel = item
        binder.item.setBackgroundResource(if (item.isSelected){
            R.drawable.item_selected
        }else{
            R.color.white
        })
    }
}

data class GeoFencePointViewModel(
    val point: GeoFenceModel, var isSelected : Boolean = false
) {
    val id = "${point.id}"
    val lat = String.format("lat: %.4f", point.lat)
    val lon = String.format("lng: %.4f", point.lng)

}
