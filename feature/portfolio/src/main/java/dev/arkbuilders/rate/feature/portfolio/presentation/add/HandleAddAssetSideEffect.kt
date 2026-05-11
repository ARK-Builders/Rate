package dev.arkbuilders.rate.feature.portfolio.presentation.add

import com.ramcosta.composedestinations.generated.search.destinations.SearchCurrencyScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import dev.arkbuilders.rate.feature.portfolio.presentation.model.AddAssetsNavResult

suspend fun handleAddAssetSideEffect(
    effect: AddAssetSideEffect,
    navigator: DestinationsNavigator,
    resultNavigator: ResultBackNavigator<AddAssetsNavResult>,
) {
    when (effect) {
        is AddAssetSideEffect.NavigateBackWithResult ->
            resultNavigator.navigateBack(effect.result)

        is AddAssetSideEffect.NavigateSearchAdd ->
            navigator.navigate(
                SearchCurrencyScreenDestination(
                    navKey = SearchNavResultType.ADD.name,
                    prohibitedCodes = effect.prohibitedCodes.toTypedArray(),
                ),
            )

        is AddAssetSideEffect.NavigateSearchSet ->
            navigator.navigate(
                SearchCurrencyScreenDestination(
                    navKey = SearchNavResultType.SET.name,
                    navPos = effect.index,
                    prohibitedCodes = effect.prohibitedCodes.toTypedArray(),
                ),
            )

        AddAssetSideEffect.NavigateBack -> navigator.popBackStack()
    }
}
