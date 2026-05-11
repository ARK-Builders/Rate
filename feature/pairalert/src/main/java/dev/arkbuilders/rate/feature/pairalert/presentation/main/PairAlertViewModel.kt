package dev.arkbuilders.rate.feature.pairalert.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.arkbuilders.rate.core.domain.model.Group
import dev.arkbuilders.rate.core.domain.model.GroupFeatureType
import dev.arkbuilders.rate.core.domain.repo.AnalyticsManager
import dev.arkbuilders.rate.core.domain.repo.CurrencyRepo
import dev.arkbuilders.rate.core.domain.repo.GroupRepo
import dev.arkbuilders.rate.core.domain.usecase.GroupReorderSwapUseCase
import dev.arkbuilders.rate.core.presentation.ui.group.EditGroupOptionsSheetState
import dev.arkbuilders.rate.core.presentation.ui.group.EditGroupRenameSheetState
import dev.arkbuilders.rate.core.presentation.ui.group.EditGroupReorderSheetState
import dev.arkbuilders.rate.feature.pairalert.data.permission.NotificationPermissionHelper
import dev.arkbuilders.rate.feature.pairalert.di.PairAlertScope
import dev.arkbuilders.rate.feature.pairalert.domain.model.PairAlert
import dev.arkbuilders.rate.feature.pairalert.domain.repo.PairAlertRepo
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

data class PairAlertScreenPage(
    val group: Group,
    val created: List<PairAlert>,
    val oneTimeTriggered: List<PairAlert>,
)

data class PairAlertScreenState(
    val pages: List<PairAlertScreenPage> = emptyList(),
    val editGroupReorderSheetState: EditGroupReorderSheetState? = null,
    val editGroupOptionsSheetState: EditGroupOptionsSheetState? = null,
    val editGroupRenameSheetState: EditGroupRenameSheetState? = null,
    val initialized: Boolean = false,
    val askNotificationPermissionPairId: Long? = null,
)

sealed class PairAlertEffect {
    data class NavigateToAdd(val pairId: Long? = null) : PairAlertEffect()

    data object AskNotificationPermissionOnScreenOpen : PairAlertEffect()

    data object AskNotificationPermissionOnNewPair : PairAlertEffect()

    data class SelectTab(val groupId: Long) : PairAlertEffect()

    data class ShowSnackbarAdded(
        val pair: PairAlert,
    ) : PairAlertEffect()

    data class ShowRemovedSnackbar(val pair: PairAlert) : PairAlertEffect()
}

class PairAlertViewModel(
    private val pairAlertRepo: PairAlertRepo,
    private val currencyRepo: CurrencyRepo,
    private val groupRepo: GroupRepo,
    private val analyticsManager: AnalyticsManager,
    private val groupReorderSwapUseCase: GroupReorderSwapUseCase,
    private val notificationPermissionHelper: NotificationPermissionHelper,
) : ViewModel(), ContainerHost<PairAlertScreenState, PairAlertEffect> {
    override val container: Container<PairAlertScreenState, PairAlertEffect> =
        container(
            PairAlertScreenState(),
        )

    init {
        intent {
            if (pairAlertRepo.getAll().isNotEmpty() &&
                notificationPermissionHelper.isGranted().not()
            ) {
                postSideEffect(PairAlertEffect.AskNotificationPermissionOnScreenOpen)
            }
        }

        init()
    }

    private fun init() =
        intent {
            initPages()

            groupRepo.allFlow(GroupFeatureType.PairAlert).drop(1).onEach {
                initPages()
            }.launchIn(viewModelScope)

            pairAlertRepo.getAllFlow().drop(1).onEach {
                initPages()
            }.launchIn(viewModelScope)
        }

    fun onReturnFromAddScreen(newPairId: Long) =
        intent {
            val pair = pairAlertRepo.getById(newPairId) ?: return@intent
            initPages()
            postSideEffect(PairAlertEffect.SelectTab(pair.group.id))
            postSideEffect(PairAlertEffect.ShowSnackbarAdded(pair))
        }

    fun onNewPair(pairId: Long? = null) =
        intent {
            if (notificationPermissionHelper.isGranted()) {
                postSideEffect(PairAlertEffect.NavigateToAdd(pairId))
            } else {
                reduce {
                    state.copy(askNotificationPermissionPairId = pairId)
                }
                postSideEffect(PairAlertEffect.AskNotificationPermissionOnNewPair)
            }
        }

    fun onNotificationPermissionGrantedOnNewPair() =
        intent {
            postSideEffect(PairAlertEffect.NavigateToAdd(state.askNotificationPermissionPairId))
            reduce {
                state.copy(askNotificationPermissionPairId = null)
            }
        }

    fun onEnableToggle(
        pairAlert: PairAlert,
        enabled: Boolean,
    ) = intent {
        val newPairAlert = pairAlert.copy(enabled = enabled)
        pairAlertRepo.insert(newPairAlert)
    }

    fun onDelete(pairAlert: PairAlert) =
        intent {
            val deleted = pairAlertRepo.delete(pairAlert.id)
            if (deleted)
                postSideEffect(PairAlertEffect.ShowRemovedSnackbar(pairAlert))
        }

    fun undoDelete(pair: PairAlert) =
        intent {
            pairAlertRepo.insert(pair)
        }

    private suspend fun initPages() =
        intent {
            val pairs = pairAlertRepo.getAll()
            val groups = groupRepo.getAllSorted(GroupFeatureType.PairAlert)
            val pages =
                groups.map { group ->
                    val filteredPairs = pairs.filter { it.group.id == group.id }.toMutableList()
                    filteredPairs.reverse()
                    val oneTimeTriggered =
                        filteredPairs.filter {
                            it.triggered() && it.oneTimeNotRecurrent && !it.enabled
                        }
                    val created = filteredPairs - oneTimeTriggered.toSet()

                    PairAlertScreenPage(group, created, oneTimeTriggered)
                }.filter { it.created.isNotEmpty() || it.oneTimeTriggered.isNotEmpty() }
            reduce {
                state.copy(pages = pages, initialized = true)
            }
        }

    //region Group Management

    fun onShowGroupsReorder() =
        intent {
            val groups = groupRepo.getAllSorted(GroupFeatureType.PairAlert)
            reduce {
                state.copy(
                    editGroupReorderSheetState = EditGroupReorderSheetState(groups),
                )
            }
        }

    fun onSwapGroups(
        from: Int,
        to: Int,
    ) = intent {
        val newGroups =
            groupReorderSwapUseCase(
                state.editGroupReorderSheetState!!.groups,
                from,
                to,
                GroupFeatureType.PairAlert,
            )

        reduce {
            state.copy(
                editGroupReorderSheetState =
                    state.editGroupReorderSheetState?.copy(
                        groups = newGroups,
                    ),
            )
        }
    }

    fun onDismissGroupsReorder() =
        intent {
            reduce { state.copy(editGroupReorderSheetState = null) }
        }

    fun onShowGroupOptions(group: Group) =
        intent {
            reduce { state.copy(editGroupOptionsSheetState = EditGroupOptionsSheetState(group)) }
        }

    fun onGroupDelete(group: Group) =
        intent {
            groupRepo.delete(group.id)
            val groups = groupRepo.getAllSorted(GroupFeatureType.PairAlert)
            reduce {
                state.copy(
                    editGroupReorderSheetState =
                        state.editGroupReorderSheetState!!.copy(
                            groups = groups,
                        ),
                    editGroupOptionsSheetState = null,
                    editGroupRenameSheetState = null,
                )
            }
        }

    fun onDismissGroupOptions() =
        intent {
            reduce { state.copy(editGroupOptionsSheetState = null) }
        }

    fun onShowGroupRename(group: Group) =
        intent {
            reduce { state.copy(editGroupRenameSheetState = EditGroupRenameSheetState(group)) }
        }

    fun onGroupRename(
        group: Group,
        newName: String,
    ) = intent {
        val renamed = group.copy(name = newName)
        groupRepo.update(renamed, GroupFeatureType.PairAlert)
        val groups = groupRepo.getAllSorted(GroupFeatureType.PairAlert)
        reduce {
            state.copy(
                editGroupReorderSheetState =
                    state.editGroupReorderSheetState!!.copy(
                        groups = groups,
                    ),
                editGroupOptionsSheetState = null,
                editGroupRenameSheetState = null,
            )
        }
    }

    fun onDismissGroupRename() =
        intent {
            reduce { state.copy(editGroupRenameSheetState = null) }
        }

    //endregion
}

@PairAlertScope
class PairAlertViewModelFactory @Inject constructor(
    private val pairAlertRepo: PairAlertRepo,
    private val currencyRepo: CurrencyRepo,
    private val groupRepo: GroupRepo,
    private val analyticsManager: AnalyticsManager,
    private val groupReorderSwapUseCase: GroupReorderSwapUseCase,
    private val notificationPermissionHelper: NotificationPermissionHelper,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PairAlertViewModel(
            pairAlertRepo,
            currencyRepo,
            groupRepo,
            analyticsManager,
            groupReorderSwapUseCase,
            notificationPermissionHelper,
        ) as T
    }
}
