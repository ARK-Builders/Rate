package dev.arkbuilders.rate.feature.quick.presentation.add

import com.ramcosta.composedestinations.generated.search.destinations.SearchCurrencyScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator

suspend fun handleAddQuickSideEffect(
    effect: AddQuickScreenEffect,
    navigator: DestinationsNavigator,
    resultNavigator: ResultBackNavigator<Long>,
) {
    when (effect) {
        is AddQuickScreenEffect.NavigateBackWithResult -> {
            resultNavigator.navigateBack(effect.newCalculationId)
        }

        is AddQuickScreenEffect.NavigateSearchAdd ->
            navigator.navigate(
                SearchCurrencyScreenDestination(
                    navKey = SearchNavResultType.ADD.name,
                    prohibitedCodes = effect.prohibitedCodes.toTypedArray(),
                ),
            )

        is AddQuickScreenEffect.NavigateSearchSet ->
            navigator.navigate(
                SearchCurrencyScreenDestination(
                    navKey = SearchNavResultType.SET.name,
                    navPos = effect.index,
                    prohibitedCodes = effect.prohibitedCodes.toTypedArray(),
                ),
            )

        AddQuickScreenEffect.NavigateBack -> navigator.popBackStack()
    }
}
