package dev.arkbuilders.rate.feature.quick.di

import dagger.Component
import dev.arkbuilders.rate.core.di.CoreComponent
import dev.arkbuilders.rate.core.domain.repo.GroupRepo
import dev.arkbuilders.rate.core.domain.usecase.ValidateGroupNameUseCase
import dev.arkbuilders.rate.feature.quick.domain.repo.QuickRepo
import dev.arkbuilders.rate.feature.quick.domain.usecase.GetSortedPinnedQuickCalculationsUseCase
import dev.arkbuilders.rate.feature.quick.domain.usecase.LaunchInAppReviewUseCase
import dev.arkbuilders.rate.feature.quick.presentation.add.AddQuickViewModelFactory
import dev.arkbuilders.rate.feature.quick.presentation.main.QuickViewModelFactory

@QuickScope
@Component(dependencies = [CoreComponent::class], modules = [QuickModule::class])
interface QuickComponent {
    fun addQuickVMFactory(): AddQuickViewModelFactory.Factory

    fun quickVMFactory(): QuickViewModelFactory.Factory

    fun getPinnedQuickCalculationUseCase(): GetSortedPinnedQuickCalculationsUseCase

    fun quickRepo(): QuickRepo

    fun groupRepo(): GroupRepo

    fun validateGroupNameUseCase(): ValidateGroupNameUseCase

    fun launchInAppReview(): LaunchInAppReviewUseCase
}
