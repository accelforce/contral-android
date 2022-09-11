package net.accelf.contral.mastodon.models

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Entity(tableName = "accounts", primaryKeys = ["domain", "id"])
internal data class Account(
    @ColumnInfo(name = "domain") val domain: String,
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "access_token") val accessToken: String,
)

@Dao
internal interface AccountDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(vararg accounts: Account)

    @Query("SELECT * FROM accounts WHERE domain = :domain AND id = :id LIMIT 1;")
    suspend fun getAccount(domain: String, id: String): Account?
}

internal class PreviewAccountProvider : PreviewParameterProvider<Account> {
    override val values: Sequence<Account>
        get() = sequenceOf(
            Account(
                domain = "example.domain",
                id = "1",
                accessToken = "",
            ),
            Account(
                domain = "example.domain",
                id = "2",
                accessToken = "",
            ),
        )
}
