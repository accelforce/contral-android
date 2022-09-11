package net.accelf.contral.core

import androidx.room.Database
import androidx.room.RoomDatabase
import net.accelf.contral.core.models.SavedTimeline
import net.accelf.contral.core.models.SavedTimelineDao

@Database(
    entities = [
        SavedTimeline::class,
    ],
    version = 1,
)
internal abstract class ContralDatabase : RoomDatabase() {
    internal abstract fun savedTimelineDao(): SavedTimelineDao
}
