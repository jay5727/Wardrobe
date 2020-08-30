package com.jay.nearbysample.adapter

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import com.jay.nearbysample.R

/**
 * Adapter class to inflate items for fragments
 * @param context object  to access resource
 * @param imagesList list of bitmap
 */
class WardrobeAdapter(
    private val context: Context,
    private val imagesList: List<Bitmap>
) : PagerAdapter() {

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        return imagesList.size
    }

    override fun isViewFromObject(
        view: View,
        `object`: Any
    ): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView =
            layoutInflater.inflate(R.layout.row_item, container, false)
        val imageView =
            itemView.findViewById<ImageView>(R.id.imageView)
        imageView.setImageBitmap(imagesList[position])
        container.addView(itemView)
        return itemView
    }

    override fun destroyItem(
        container: ViewGroup,
        position: Int,
        `object`: Any
    ) {
        container.removeView(`object` as LinearLayout)
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

}