package net.accelf.contral.mastodon.timelines

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import net.accelf.contral.mastodon.api.Status
import net.accelf.contral.mastodon.util.Reference

@OptIn(ExperimentalPagingApi::class)
internal class StatusRemoteMediator(
    pagingSourceRef: Reference<StatusPagingSource>,
    private val loader: suspend (LoadKey, Int) -> List<Status>,
) : RemoteMediator<LoadKey, Status>() {

    private val pagingSource by pagingSourceRef

    override suspend fun load(loadType: LoadType, state: PagingState<LoadKey, Status>): MediatorResult =
        when (loadType) {
            LoadType.PREPEND -> null
            LoadType.APPEND -> LoadKey(minId = null, maxId = state.lastItemOrNull()?.id)
            LoadType.REFRESH -> LoadKey(minId = null, maxId = null)
        }
            .runCatching { this?.let { loader(this, state.config.pageSize) } ?: emptyList() }
            .fold(
                { loaded ->
                    pagingSource.putAll(loaded)
                    MediatorResult.Success(endOfPaginationReached = loaded.isEmpty())
                },
                {
                    MediatorResult.Error(it)
                },
            )
}
