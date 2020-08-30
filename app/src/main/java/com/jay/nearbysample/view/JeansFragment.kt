package com.jay.nearbysample.view

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager.widget.ViewPager
import com.jay.nearbysample.R
import com.jay.nearbysample.adapter.WardrobeAdapter
import com.jay.nearbysample.listener.IWardrobeItemsShuffleListener
import com.jay.nearbysample.room.model.Wardrobe
import com.jay.nearbysample.utilities.BitmapUtils
import com.jay.nearbysample.utilities.Utility.getRandomNumberFrom
import com.jay.nearbysample.viewmodel.WardrobeViewModel
import kotlinx.android.synthetic.main.fragment_shirt_jeans.*

/**
 * Fragment to display Jeans Fragment
 *
 */
class JeansFragment : Fragment(),
    IWardrobeItemsShuffleListener {

    private val viewModel: WardrobeViewModel by activityViewModels()
    private lateinit var pagerAdapter: WardrobeAdapter
    private var mContext: Context? = null

    companion object {
        fun newInstance(): JeansFragment {
            return JeansFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shirt_jeans, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.getJeansWardrobeImagesLiveData()
        attachObserver()
        setViewPagerListener()
    }

    /**
     * Method to set View Pager Listener
     */
    private fun setViewPagerListener() {
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                //no-op
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                //no-op
            }

            override fun onPageSelected(position: Int) {
                //swipe detected
                viewModel.currentSelectedJeansPosition.postValue(position)
            }

        })
    }

    /**
     * Method to attach observer
     */
    private fun attachObserver() {
        viewModel.jeansLiveData
            .observe(viewLifecycleOwner, androidx.lifecycle.Observer { jeansWardrobeList ->
                if (!jeansWardrobeList.isNullOrEmpty()) {
                    setUpViewPager(jeansWardrobeList)
                }
            })
    }

    /**
     * @param jeansWardrobeList list of wardrobe objects
     */
    private fun setUpViewPager(jeansWardrobeList: List<Wardrobe>) {
        mContext?.let {
            pagerAdapter = WardrobeAdapter(it, prepareBitmap(jeansWardrobeList))
            pagerAdapter.apply {
                viewPager.adapter = this
            }
        }
    }

    /**
     * @param jeansWardrobeList list of wardrobe objects
     * Returns list of bitmap for jeans wardrobes
     */
    private fun prepareBitmap(jeansWardrobeList: List<Wardrobe>): List<Bitmap> {
        val bitmapList = ArrayList<Bitmap>()
        for (i in jeansWardrobeList.indices) {
            bitmapList.add(
                BitmapUtils.convertCompressedByteArrayToBitmap(
                    jeansWardrobeList[i].image
                )
            )
        }
        return bitmapList
    }

    override fun onItemsShuffled(): Int {
        val position: Int = getRandomNumberFrom(0, pagerAdapter.count - 1)
        pagerAdapter.notifyDataSetChanged()
        viewPager.adapter = null
        viewPager.adapter = pagerAdapter
        viewPager.currentItem = position
        return viewPager.currentItem
    }

}