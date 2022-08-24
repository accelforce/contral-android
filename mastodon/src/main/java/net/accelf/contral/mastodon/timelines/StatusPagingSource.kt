package net.accelf.contral.mastodon.timelines

import androidx.paging.PagingSource
import androidx.paging.PagingState
import at.connyduck.calladapter.networkresult.fold
import net.accelf.contral.mastodon.api.MastodonApi
import net.accelf.contral.mastodon.api.Status

internal class StatusPagingSource(
    private val mastodonApi: MastodonApi,
) : PagingSource<String, Status>() {
    override fun getRefreshKey(state: PagingState<String, Status>): String? =
        state.anchorPosition?.let { anchorPosition ->
            state.closestItemToPosition(anchorPosition)?.id
        }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Status> =
        mastodonApi.getHomeTimeline(limit = params.loadSize)
            .fold(
                {
                    LoadResult.Page(
                        data = it,
                        prevKey = null,
                        nextKey = null,
                    )
                },
                {
                    LoadResult.Error(
                        throwable = it,
                    )
                },
            )
}
