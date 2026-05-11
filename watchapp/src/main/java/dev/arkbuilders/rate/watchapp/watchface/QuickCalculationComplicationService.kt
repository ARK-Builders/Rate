package dev.arkbuilders.rate.watchapp.watchface

import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.data.LongTextComplicationData
import androidx.wear.watchface.complications.data.PlainComplicationText
import androidx.wear.watchface.complications.data.ShortTextComplicationData
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import androidx.wear.watchface.complications.datasource.SuspendingComplicationDataSourceService
import dagger.hilt.android.AndroidEntryPoint
import dev.arkbuilders.rate.core.domain.usecase.ConvertWithRateUseCase
import dev.arkbuilders.rate.feature.quick.domain.repo.QuickRepo
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@AndroidEntryPoint
class QuickCalculationComplicationService : SuspendingComplicationDataSourceService() {

    @Inject
    lateinit var quickRepo: QuickRepo

    @Inject
    lateinit var convertUseCase: ConvertWithRateUseCase

    override suspend fun onComplicationRequest(request: ComplicationRequest): ComplicationData? {
        val pairs = quickRepo.allFlow().first()
        val pair = pairs.firstOrNull() ?: return null

        val toAmount = pair.to.firstOrNull() ?: return null
        val (convertedAmt, _) = convertUseCase.invoke(pair.from, pair.amount, toAmount.code)

        val resultStr = String.format("%.2f", convertedAmt)
        val text = "$resultStr ${toAmount.code}"
        val title = "${pair.amount} ${pair.from}"

        return when (request.complicationType) {
            ComplicationType.SHORT_TEXT -> {
                ShortTextComplicationData.Builder(
                    text = PlainComplicationText.Builder(text).build(),
                    contentDescription = PlainComplicationText.Builder("Currency Rate").build()
                ).setTitle(PlainComplicationText.Builder(title).build())
                    .build()
            }
            ComplicationType.LONG_TEXT -> {
                LongTextComplicationData.Builder(
                    text = PlainComplicationText.Builder("$title = $text").build(),
                    contentDescription = PlainComplicationText.Builder("Currency Rate").build()
                ).setTitle(PlainComplicationText.Builder("Quick Pair Rate").build())
                    .build()
            }
            else -> null
        }
    }

    override fun getPreviewData(type: ComplicationType): ComplicationData? {
        val text = "1.00 EUR"
        val title = "1 USD"
        return when (type) {
            ComplicationType.SHORT_TEXT -> {
                ShortTextComplicationData.Builder(
                    text = PlainComplicationText.Builder(text).build(),
                    contentDescription = PlainComplicationText.Builder("Currency Rate Preview").build()
                ).setTitle(PlainComplicationText.Builder(title).build())
                    .build()
            }
            ComplicationType.LONG_TEXT -> {
                LongTextComplicationData.Builder(
                    text = PlainComplicationText.Builder("$title = $text").build(),
                    contentDescription = PlainComplicationText.Builder("Currency Rate Preview").build()
                ).setTitle(PlainComplicationText.Builder("Quick Pair Rate").build())
                    .build()
            }
            else -> null
        }
    }
}
