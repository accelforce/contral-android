package net.accelf.contral.mastodon

import androidx.room.Database
import androidx.room.RoomDatabase
import net.accelf.contral.mastodon.models.Account
import net.accelf.contral.mastodon.models.AccountDao

@Database(
    entities = [
        Account::class,
    ],
    version = 1,
)
internal abstract class MastodonDatabase : RoomDatabase() {
    internal abstract fun accountDao(): AccountDao
}
