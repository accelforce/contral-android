package net.accelf.contral.mastodon.timelines

import androidx.paging.PagingSource
import androidx.paging.PagingState
import at.connyduck.calladapter.networkresult.fold
import net.accelf.contral.mastodon.api.MastodonApi
import net.accelf.contral.mastodon.api.Status

internal class StatusPagingSource(
    private val mastodonApi: MastodonApi,
) : PagingSource<LoadKey, Status>() {
    override fun getRefreshKey(state: PagingState<LoadKey, Status>): LoadKey? =
        state.anchorPosition?.let { anchorPosition ->
            LoadKey(minId = state.closestItemToPosition(anchorPosition)?.id)
        }

    override suspend fun load(params: LoadParams<LoadKey>): LoadResult<LoadKey, Status> =
        mastodonApi.getHomeTimeline(
            limit = params.loadSize,
            minId = params.key?.minId,
            maxId = params.key?.maxId,
        )
            .fold(
                {
                    LoadResult.Page(
                        data = it,
                        prevKey = it.firstOrNull()?.id?.let { id -> LoadKey(minId = id) },
                        nextKey = it.lastOrNull()?.id?.let { id -> LoadKey(maxId = id) },
                    )
                },
                {
                    LoadResult.Error(
                        throwable = it,
                    )
                },
            )
}
