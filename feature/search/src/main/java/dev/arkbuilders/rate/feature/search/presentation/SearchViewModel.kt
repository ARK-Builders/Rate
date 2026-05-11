package dev.arkbuilders.rate.feature.search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.arkbuilders.rate.core.domain.model.CurrencyCode
import dev.arkbuilders.rate.core.domain.model.CurrencyInfo
import dev.arkbuilders.rate.core.domain.repo.AnalyticsManager
import dev.arkbuilders.rate.core.domain.repo.CurrencyRepo
import dev.arkbuilders.rate.core.domain.usecase.CalcFrequentCurrUseCase
import dev.arkbuilders.rate.core.domain.usecase.SearchUseCase
import dev.arkbuilders.rate.feature.search.di.SearchScope
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

data class SearchScreenState(
    val prohibitedCodes: List<CurrencyCode> = emptyList(),
    val frequent: List<CurrencyInfo> = emptyList(),
    val all: List<CurrencyInfo> = emptyList(),
    val filter: String = "",
    val topResultsFiltered: List<CurrencyInfo> = emptyList(),
    val initialized: Boolean = false,
    val showCodeProhibitedDialog: Boolean = false,
)

sealed class SearchScreenEffect {
    data class NavigateBackWithResult(val result: SearchNavResult) : SearchScreenEffect()

    data object NavigateBack : SearchScreenEffect()
}

class SearchViewModel(
    private val navKey: String?,
    private val navPos: Int?,
    private val prohibitedCodes: List<CurrencyCode>?,
    private val currencyRepo: CurrencyRepo,
    private val calcFrequentCurrUseCase: CalcFrequentCurrUseCase,
    private val searchUseCase: SearchUseCase,
    private val analyticsManager: AnalyticsManager,
) : ContainerHost<SearchScreenState, SearchScreenEffect>,
    ViewModel() {
    override val container: Container<SearchScreenState, SearchScreenEffect> =
        container(SearchScreenState(prohibitedCodes = prohibitedCodes ?: emptyList()))

    init {
        intent {
            val all = currencyRepo.getCurrencyInfo()
            val frequent = calcFrequentCurrUseCase.invoke().map { currencyRepo.infoByCode(it) }

            reduce {
                state.copy(
                    frequent = frequent,
                    all = all,
                    initialized = true,
                )
            }
        }
    }

    fun onInputChange(input: String) =
        blockingIntent {
            reduce {
                state.copy(
                    filter = input,
                    topResultsFiltered =
                        searchUseCase(
                            state.all,
                            state.frequent.map { it.code },
                            input,
                        ),
                )
            }
        }

    fun onClick(model: CurrencyInfo) =
        intent {
            prohibitedCodes?.let {
                if (model.code in prohibitedCodes) {
                    reduce {
                        state.copy(showCodeProhibitedDialog = true)
                    }
                    return@intent
                }
            }

            val result = SearchNavResult(navKey, navPos, model.code)
            analyticsManager.logEvent("search_back_clicked_with_result")
            postSideEffect(SearchScreenEffect.NavigateBackWithResult(result))
        }

    fun onCodeProhibitedDialogDismiss() =
        intent {
            reduce { state.copy(showCodeProhibitedDialog = false) }
        }

    fun onBackClick() =
        intent {
            analyticsManager.logEvent("search_back_clicked")
            postSideEffect(SearchScreenEffect.NavigateBack)
        }
}

class SearchViewModelFactory @AssistedInject constructor(
    @Assisted private val navKey: String?,
    @Assisted private val navPos: Int?,
    @Assisted private val prohibitedCodes: List<CurrencyCode>?,
    private val currencyRepo: CurrencyRepo,
    private val calcFrequentCurrUseCase: CalcFrequentCurrUseCase,
    private val searchUseCase: SearchUseCase,
    private val analyticsManager: AnalyticsManager,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SearchViewModel(
            navKey,
            navPos,
            prohibitedCodes,
            currencyRepo,
            calcFrequentCurrUseCase,
            searchUseCase,
            analyticsManager,
        ) as T
    }

    @SearchScope
    @AssistedFactory
    interface Factory {
        fun create(
            navKey: String?,
            navPos: Int?,
            prohibitedCodes: List<CurrencyCode>?,
        ): SearchViewModelFactory
    }
}
