package com.mintflow.personal.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.mintflow.personal.R
import com.mintflow.personal.model.SliderItem
import com.smarteist.autoimageslider.SliderViewAdapter


class OnBoardingSliderAdapter(private val context: Context) :
    SliderViewAdapter<OnBoardingSliderAdapter.SliderAdapterVH>() {
    private var mSliderItems: MutableList<SliderItem> = ArrayList()

    fun renewItems(sliderItems: MutableList<SliderItem>) {
        this.mSliderItems = sliderItems
        notifyDataSetChanged()
    }

    fun deleteItem(position: Int) {
        mSliderItems.removeAt(position)
        notifyDataSetChanged()
    }

    fun addItem(sliderItem: SliderItem) {
        mSliderItems.add(sliderItem)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup): SliderAdapterVH {
        val inflate: View =
            LayoutInflater.from(parent.context).inflate(R.layout.onboarding_image_slider, null)
        return SliderAdapterVH(inflate)
    }

    override fun onBindViewHolder(viewHolder: SliderAdapterVH, position: Int) {
        val sliderItem = mSliderItems[position]
        viewHolder.imageViewBackground.setImageResource(sliderItem.image)
        viewHolder.textViewSubtitles.text= sliderItem.subtitle
        viewHolder.textViewTitle.text= sliderItem.topic

    }

    override fun getCount(): Int {
        return mSliderItems.size
    }

    inner class SliderAdapterVH(itemView: View) : ViewHolder(itemView) {
        var imageViewBackground: ImageView = itemView.findViewById(R.id.iv_auto_image_slider)
        var imageGifContainer: ImageView = itemView.findViewById(R.id.iv_gif_container)
        var textViewSubtitles: TextView = itemView.findViewById(R.id.tv_auto_image_slider)
        var textViewTitle: TextView = itemView.findViewById(R.id.tv_main_image_slider)
    }
}