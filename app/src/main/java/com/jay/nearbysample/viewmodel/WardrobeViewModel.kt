package com.jay.nearbysample.viewmodel

import android.util.Log
import androidx.databinding.ObservableBoolean
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jay.nearbysample.base.BaseViewModel
import com.jay.nearbysample.repository.WardrobeRepository
import com.jay.nearbysample.room.model.Liked
import com.jay.nearbysample.room.model.Wardrobe
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/***
 * Created by Jay on 28/08/2020
 */

/**
 *@param repository Wardrobe Repository
 */
class WardrobeViewModel @ViewModelInject constructor(
    private val repository: WardrobeRepository
) : BaseViewModel() {

    val TAG = WardrobeViewModel::class.java.simpleName

    val shouldShowShuffle = ObservableBoolean(false)
    val shouldShowFavorite = ObservableBoolean(false)
    val isMarkedFavorite = ObservableBoolean(false)

    val currentSelectedShirtPosition = MutableLiveData<Int>()
    val currentSelectedJeansPosition = MutableLiveData<Int>()

    val wardrobeLiveData = MutableLiveData<List<Wardrobe>>()
    val shirtsLiveData = MutableLiveData<List<Wardrobe>>()
    val jeansLiveData = MutableLiveData<List<Wardrobe>>()
    val likedLiveData = MutableLiveData<List<Liked>>()

    /**
     * @param wardrobe object to insert
     * Possible types Refer[com.jay.nearbysample.enums.WardrobeType]
     */
    fun insertWardrobe(wardrobe: Wardrobe?) {
        viewModelScope.launch(Dispatchers.IO) {
            wardrobe?.let {
                repository.insertWardrobe(it)
            }
        }
    }

    /**
     * @param liked object to mark Favorite in DB
     */
    fun insertLike(liked: Liked?) {
        viewModelScope.launch(Dispatchers.IO) {
            liked?.let {
                repository.insertLike(it)
            }
        }
    }

    /**
     * @param likedID id of Liked model object to delete from DB
     */
    fun deleteLike(likedID: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            likedID?.let {
                repository.deleteLikeById(it)
            }
        }
    }

    /**
     * Method to get All Wardrobe Images consisting of Shirt & Jeans
     */
    fun getWardrobeLiveData() {
        val disposable = repository.getWardrobeLiveData()
            .onErrorResumeNext { _: Throwable ->
                //Since this is DB call, don't break the chain
                Observable.empty<List<Wardrobe>>()
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {//onNext ->
                    wardrobeLiveData.postValue(it)
                },
                { //onError ->
                        throwable ->
                    Log.e(TAG, throwable.toString())
                })

        addDisposable(disposable)
    }

    /**
     * Method to get Shirt Wardrobe Images
     */
    fun getShirtWardrobeImagesLiveData() {
        val disposable = repository.getShirtWardrobeLiveData()
            .onErrorResumeNext { _: Throwable ->
                //Since this is DB call, don't break the chain
                Observable.empty<List<Wardrobe>>()
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {//onNext ->
                    shirtsLiveData.postValue(it)
                },
                { //onError ->
                        throwable ->
                    Log.e(TAG, throwable.toString())
                })

        addDisposable(disposable)
    }

    /**
     * Method to get Jeans Wardrobe Images
     */
    fun getJeansWardrobeImagesLiveData() {
        val disposable = repository.getJeansWardrobeLiveData()
            .onErrorResumeNext { _: Throwable ->
                //Since this is DB call, don't break the chain
                Observable.empty<List<Wardrobe>>()
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {//onNext ->
                    jeansLiveData.postValue(it)
                },
                { //onError ->
                        throwable ->
                    Log.e(TAG, throwable.toString())
                })

        addDisposable(disposable)
    }

    /**
     * Method to get Liked Live Data List
     */
    fun getLikedLiveData() {
        val disposable = repository.getLikedLiveData()
            .onErrorResumeNext { _: Throwable ->
                //Since this is DB call, don't break the chain
                Observable.empty<List<Liked>>()
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {//onNext ->
                    likedLiveData.postValue(it)
                },
                { //onError ->
                        throwable ->
                    Log.e(TAG, throwable.toString())
                })

        addDisposable(disposable)
    }

    /**
     * @param isMarkedFavorite true to show marked combination of Shirt & Jeans as favorite, false otherwise
     */
    fun setIsMarkedFavorite(isMarkedFavorite: Boolean) {
        this.isMarkedFavorite.set(isMarkedFavorite)
    }

    /**
     * @param shouldShowFavorite true to show Favorite Button, false otherwise
     */
    fun setFavoriteVisibility(shouldShowFavorite: Boolean) {
        this.shouldShowFavorite.set(shouldShowFavorite)
    }

    /**
     * @param shouldShowShuffle true to show Shuffle Button, false otherwise
     */
    fun setShuffleVisibility(shouldShowShuffle: Boolean) {
        this.shouldShowShuffle.set(shouldShowShuffle)
    }

}
