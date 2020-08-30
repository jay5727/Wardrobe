package com.jay.nearbysample.repository

import com.jay.nearbysample.room.dao.LikedDao
import com.jay.nearbysample.room.dao.WardrobeDao
import com.jay.nearbysample.room.model.Liked
import com.jay.nearbysample.room.model.Wardrobe
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import io.reactivex.Observable

/***
 * Created by Jay on 28/08/2020
 */
@Singleton
/**
 * @param wardrobeDao DAO object to perform DB operations
 * @param likedDao DAO object to perform DB operations
 */
class WardrobeRepository @Inject constructor(
    private val wardrobeDao: WardrobeDao,
    private val likedDao: LikedDao
) : Repository {

    init {
        Timber.d("Injection WardrobeRepository")
    }

    /**
     * Returns Observable of Wardrobe containing both Shirt & Jeans
     */
    fun getWardrobeLiveData(): Observable<List<Wardrobe>> {
        return wardrobeDao.getAllWardrobe()
    }

    /**
     * Returns Observable of Wardrobe containing Shirt
     */
    fun getShirtWardrobeLiveData(): Observable<List<Wardrobe>> {
        return wardrobeDao.getShirtWardrobe()
    }

    /**
     * Returns Observable of Wardrobe containing Jeans
     */
    fun getJeansWardrobeLiveData(): Observable<List<Wardrobe>> {
        return wardrobeDao.getJeansWardrobe()
    }

    /**
     * Returns Observable of Liked items
     */
    fun getLikedLiveData(): Observable<List<Liked>> {
        return likedDao.getLikedList()
    }

    /**
     * @param wardrobe object to insert
     */
    suspend fun insertWardrobe(wardrobe: Wardrobe) {
        wardrobeDao.insertImage(wardrobe)
    }

    /**
     * @param liked object to mark Favorite in DB
     */
    suspend fun insertLike(liked: Liked) {
        likedDao.insertLike(liked)
    }

    /**
     * @param likedID id of Liked model object to delete from DB
     */
    suspend fun deleteLikeById(likedId: String) {
        likedDao.deleteById(likedId)
    }

}
