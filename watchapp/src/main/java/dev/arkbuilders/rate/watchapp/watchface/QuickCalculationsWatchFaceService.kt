package dev.arkbuilders.rate.watchapp.watchface

import android.util.Log
import android.view.SurfaceHolder
import androidx.wear.watchface.ComplicationSlotsManager
import androidx.wear.watchface.WatchFace
import androidx.wear.watchface.WatchFaceService
import androidx.wear.watchface.WatchFaceType
import androidx.wear.watchface.WatchState
import androidx.wear.watchface.style.CurrentUserStyleRepository
import dagger.hilt.android.AndroidEntryPoint
import dev.arkbuilders.rate.core.domain.model.Amount
import dev.arkbuilders.rate.core.domain.usecase.ConvertWithRateUseCase
import dev.arkbuilders.rate.feature.quick.domain.repo.QuickRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class QuickCalculationsWatchFaceService : WatchFaceService() {

    @Inject
    lateinit var quickRepo: QuickRepo

    @Inject
    lateinit var convertUseCase: ConvertWithRateUseCase

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override suspend fun createWatchFace(
        surfaceHolder: SurfaceHolder,
        watchState: WatchState,
        complicationSlotsManager: ComplicationSlotsManager,
        currentUserStyleRepository: CurrentUserStyleRepository
    ): WatchFace {
        Log.d("QuickCalculationsWatchFace", "Creating watch face")
        val renderer = QuickCalculationsRenderer(
            context = this,
            surfaceHolder = surfaceHolder,
            watchState = watchState,
            currentUserStyleRepository = currentUserStyleRepository
        )

        scope.launch {
            Log.d("QuickCalculationsWatchFace", "Starting data collection")
            quickRepo.allFlow()
                .map { pairs -> pairs.filter { it.isPinned() } }
                .distinctUntilChanged { old, new ->
                    old.size == new.size && old.all { oldPair ->
                        new.any { it.id == oldPair.id && it.amount == oldPair.amount && it.from == oldPair.from && it.to == oldPair.to }
                    }
                }
                .flowOn(Dispatchers.IO)
                .collectLatest { pinnedPairs ->
                    Log.d("QuickCalculationsWatchFace", "Received ${pinnedPairs.size} pinned pairs")
                    val processedPairs = withContext(Dispatchers.IO) {
                        pinnedPairs.map { pair ->
                            val actualTo = pair.to.map { toAmount ->
                                val (convertedAmt, _) = convertUseCase.invoke(pair.from, pair.amount, toAmount.code)
                                Amount(convertedAmt.code, convertedAmt.value)
                            }
                            pair.copy(to = actualTo)
                        }.sortedByDescending { it.pinnedDate }
                    }
                    Log.d("QuickCalculationsWatchFace", "Updating renderer with ${processedPairs.size} processed pairs")
                    renderer.updateQuickCalculations(processedPairs)
                }
        }

        return WatchFace(
            watchFaceType = WatchFaceType.DIGITAL,
            renderer = renderer
        )
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}
