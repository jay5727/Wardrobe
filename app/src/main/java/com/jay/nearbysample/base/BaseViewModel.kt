package com.jay.nearbysample.base

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 *  BaseViewModel containing a CompositeDisposable to keep a track of all the
 *  disposables that are defined in the ViewModel
 */
abstract class BaseViewModel() : ViewModel(){

    private val compositeDisposable = CompositeDisposable()

    /**
     * Adds the disposable to the compositeDisposable
     * @param disposable the disposable to be added
     */
    fun addDisposable(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    /**
     * clears the [CompositeDisposable]
     */
    override fun onCleared() {
        compositeDisposable.clear()
    }

}