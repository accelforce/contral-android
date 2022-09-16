package net.accelf.contral.mastodon.timelines

import androidx.paging.PagingSource
import androidx.paging.PagingState
import net.accelf.contral.mastodon.api.Status
import net.accelf.contral.mastodon.util.firstKeyOrNull
import net.accelf.contral.mastodon.util.lastKeyOrNull
import java.util.*

internal class StatusPagingSource(
    private val statuses: TreeMap<String, Status>,
) : PagingSource<LoadKey, Status>() {

    override fun getRefreshKey(state: PagingState<LoadKey, Status>): LoadKey? = null

    override suspend fun load(params: LoadParams<LoadKey>): LoadResult<LoadKey, Status> =
        when (params) {
            is LoadParams.Prepend -> statuses.headMap(params.key.minId, false)
            is LoadParams.Append -> statuses.tailMap(params.key.maxId, false)
            is LoadParams.Refresh -> statuses
        }
            .let { loaded ->
                LoadResult.Page(
                    loaded.map { (_, it) -> it },
                    loaded.firstKeyOrNull()?.let { LoadKey(minId = it) },
                    loaded.lastKeyOrNull()?.let { LoadKey(maxId = it) },
                )
            }

    fun replace(updated: Status) =
        statuses.replace(updated.id, updated).also { invalidate() }

    fun removeAll() {
        statuses.clear()
        invalidate()
    }

    fun remove(status: Status) =
        statuses.remove(status.id).also { invalidate() }

    fun putAll(values: List<Status>) {
        statuses.putAll(values.map { it.id to it })
        invalidate()
    }
}
