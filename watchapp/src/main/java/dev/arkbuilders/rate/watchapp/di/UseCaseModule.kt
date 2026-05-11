package dev.arkbuilders.rate.watchapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.arkbuilders.rate.core.domain.BuildConfigFieldsProvider
import dev.arkbuilders.rate.core.domain.repo.CodeUseStatRepo
import dev.arkbuilders.rate.core.domain.repo.CurrencyRepo
import dev.arkbuilders.rate.core.domain.repo.GroupRepo
import dev.arkbuilders.rate.core.domain.repo.InAppReviewManager
import dev.arkbuilders.rate.core.domain.repo.Prefs
import dev.arkbuilders.rate.core.domain.usecase.CalcFrequentCurrUseCase
import dev.arkbuilders.rate.core.domain.usecase.ConvertWithRateUseCase
import dev.arkbuilders.rate.core.domain.usecase.DefaultGroupNameProvider
import dev.arkbuilders.rate.core.domain.usecase.GetGroupByIdOrCreateDefaultUseCase
import dev.arkbuilders.rate.core.domain.usecase.GroupReorderSwapUseCase
import dev.arkbuilders.rate.core.domain.usecase.SearchUseCase
import dev.arkbuilders.rate.core.domain.usecase.ValidateGroupNameUseCase
import dev.arkbuilders.rate.feature.quick.domain.usecase.LaunchInAppReviewUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UseCaseModule {
    @Singleton
    @Provides
    fun calcFrequentCurrUseCase(codeUseStatRepo: CodeUseStatRepo) =
        CalcFrequentCurrUseCase(codeUseStatRepo)

    @Singleton
    @Provides
    fun convertWithRateUseCase(currencyRepo: CurrencyRepo) = ConvertWithRateUseCase(currencyRepo)

    @Singleton
    @Provides
    fun prepopulateDefaultGroupUseCase(
        groupRepo: GroupRepo,
        defaultGroupNameProvider: DefaultGroupNameProvider,
    ) = GetGroupByIdOrCreateDefaultUseCase(groupRepo, defaultGroupNameProvider)

    @Singleton
    @Provides
    fun groupReorderSwapUseCase(groupRepo: GroupRepo) = GroupReorderSwapUseCase(groupRepo)

    @Singleton
    @Provides
    fun validateGroupNameUseCase() = ValidateGroupNameUseCase()

    @Singleton
    @Provides
    fun searchUseCase(buildConfigFieldsProvider: BuildConfigFieldsProvider) =
        SearchUseCase(buildConfigFieldsProvider.provide())


    @Singleton
    @Provides
    fun provideLaunchInAppReviewUseCase(

        inAppReviewManager: InAppReviewManager,
        prefs: Prefs,
        buildConfigFieldsProvider: BuildConfigFieldsProvider

    ) =
        LaunchInAppReviewUseCase(inAppReviewManager, prefs, buildConfigFieldsProvider)
}
