package com.rokoblak.routeplanner.data.repo

import com.rokoblak.routeplanner.data.datasource.RoutesRemoteDataSource
import com.rokoblak.routeplanner.data.repo.model.CallResult
import com.rokoblak.routeplanner.data.repo.model.LoadableResult
import com.rokoblak.routeplanner.data.repo.model.RoutesPage
import com.rokoblak.routeplanner.data.repo.model.toLoadable
import com.rokoblak.routeplanner.di.MainDispatcher
import com.rokoblak.routeplanner.domain.model.RoutesListing
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject


interface RoutesRepo {
    val flow: Flow<LoadableResult<RoutesListing>>
    suspend fun loadNext()
    suspend fun reload()
}

class AppRoutesRepo @Inject constructor(
    private val remote: RoutesRemoteDataSource,
    @MainDispatcher private val dispatcher: CoroutineDispatcher,
) : RoutesRepo {

    private val repoScope = CoroutineScope(dispatcher + Job())

    private val loadingMore = MutableStateFlow(false)
    private val calls = MutableStateFlow<CallResult<RoutesPage>?>(null)

    private val loadedPagesStorage = MutableStateFlow<RoutesPage?>(null)

    private val loadedPagesFlow: Flow<RoutesPage?> = flow {
        emit(loadedPagesStorage.value)
        reload()
        emitAll(loadedPagesStorage)
    }

    private val results: StateFlow<LoadableResult<RoutesListing>> =
        combine(loadedPagesFlow, calls, loadingMore) { loaded, callRes, loadingMore ->
            if (loaded != null && loaded.routes.isNotEmpty()) {
                LoadableResult.Success(
                    value = RoutesListing(
                        routes = loaded.routes,
                        loadingMore = loadingMore,
                        page = loaded.page,
                        end = loaded.end,
                    )
                )
            } else {
                when (callRes) {
                    is CallResult.Error -> callRes.toLoadable()
                    else -> LoadableResult.Loading
                }
            }
        }.stateIn(repoScope, SharingStarted.Lazily, initialValue = LoadableResult.Loading)

    override val flow: Flow<LoadableResult<RoutesListing>> = results

    override suspend fun loadNext() {
        if (loadingMore.value) return
        if (results.value is LoadableResult.Loading) return
        val lastPage = (results.value as? LoadableResult.Success)?.value
        if (lastPage?.end == true) return
        loadingMore.value = true
        makeLoad(page = lastPage?.page?.let { it + 1 } ?: RoutesRemoteDataSource.PAGE_START)
    }

    override suspend fun reload() {
        calls.value = null
        loadedPagesStorage.update { null }
        makeLoad(page = RoutesRemoteDataSource.PAGE_START)
    }

    private suspend fun makeLoad(page: Int) {
        val res = remote.load(page)
        when (res) {
            is CallResult.Error -> Unit
            is CallResult.Success -> {
                loadedPagesStorage.update { page ->
                    if (page == null) {
                        res.value
                    } else {
                        RoutesPage(
                            routes = page.routes + res.value.routes,
                            page = res.value.page,
                            end = res.value.end,
                        )
                    }
                }
            }
        }
        loadingMore.value = false
        calls.value = res
    }
}
