package net.accelf.contral.core.models

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "saved_timelines")
internal data class SavedTimeline(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "params") val params: String,
)

@Dao
internal interface SavedTimelineDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(savedTimeline: SavedTimeline): Long

    @Query("SELECT * FROM saved_timelines;")
    fun listSavedTimelines(): Flow<List<SavedTimeline>>

    @Query("SELECT * FROM saved_timelines WHERE id = :id;")
    suspend fun getSavedTimeline(id: Long): SavedTimeline?

    @Query("DELETE FROM saved_timelines WHERE id = :id;")
    suspend fun delete(id: Long)
}
