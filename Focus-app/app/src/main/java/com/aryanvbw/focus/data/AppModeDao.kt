package com.aryanvbw.focus.data

import androidx.room.*
import androidx.lifecycle.LiveData

/**
 * DAO for app modes
 */
@Dao
interface AppModeDao {
    
    @Query("SELECT * FROM app_modes WHERE id = 'current_mode'")
    suspend fun getCurrentMode(): AppMode?
    
    @Query("SELECT * FROM app_modes WHERE id = 'current_mode'")
    fun getCurrentModeLive(): LiveData<AppMode?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateMode(mode: AppMode)
    
    @Update
    suspend fun updateMode(mode: AppMode)
    
    @Query("UPDATE app_modes SET mode = :mode, updatedAt = :updatedAt WHERE id = 'current_mode'")
    suspend fun updateCurrentMode(mode: String, updatedAt: java.util.Date)
}
