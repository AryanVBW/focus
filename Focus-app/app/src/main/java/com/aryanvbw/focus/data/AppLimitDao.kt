package com.aryanvbw.focus.data

import androidx.room.*
import androidx.lifecycle.LiveData

/**
 * DAO for app limits
 */
@Dao
interface AppLimitDao {
    
    @Query("SELECT * FROM app_limits ORDER BY appName ASC")
    fun getAllLimits(): LiveData<List<AppLimit>>
    
    @Query("SELECT * FROM app_limits WHERE isEnabled = 1 ORDER BY appName ASC")
    fun getEnabledLimits(): LiveData<List<AppLimit>>
    
    @Query("SELECT * FROM app_limits WHERE packageName = :packageName")
    suspend fun getLimitForApp(packageName: String): AppLimit?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateLimit(limit: AppLimit)
    
    @Update
    suspend fun updateLimit(limit: AppLimit)
    
    @Delete
    suspend fun deleteLimit(limit: AppLimit)
    
    @Query("DELETE FROM app_limits WHERE packageName = :packageName")
    suspend fun deleteLimitByPackage(packageName: String)
}
