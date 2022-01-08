package com.example.greenroadtest.ui

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.greenroadtest.GeoFencePointViewModel


class GeoFanceModelDiffUtill: DiffUtil.ItemCallback<GeoFencePointViewModel>(){
    override fun areItemsTheSame(
        oldItem: GeoFencePointViewModel,
        newItem: GeoFencePointViewModel
    ): Boolean {
        return oldItem.point.id == newItem.point.id
    }

    override fun areContentsTheSame(
        oldItem: GeoFencePointViewModel,
        newItem: GeoFencePointViewModel
    ): Boolean {
        return oldItem.point == oldItem.point
    }
}
