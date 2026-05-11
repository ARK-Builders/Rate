package dev.arkbuilders.rate.watchapp.watchface

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.view.SurfaceHolder
import androidx.wear.watchface.CanvasType
import androidx.wear.watchface.DrawMode
import androidx.wear.watchface.Renderer
import androidx.wear.watchface.WatchState
import androidx.wear.watchface.style.CurrentUserStyleRepository
import dev.arkbuilders.rate.core.domain.CurrUtils
import dev.arkbuilders.rate.core.presentation.utils.IconUtils
import dev.arkbuilders.rate.feature.quick.domain.model.QuickCalculation
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class QuickCalculationsRenderer(
    private val context: Context,
    surfaceHolder: SurfaceHolder,
    private val watchState: WatchState,
    currentUserStyleRepository: CurrentUserStyleRepository,
) : Renderer.CanvasRenderer(
    surfaceHolder,
    currentUserStyleRepository,
    watchState,
    CanvasType.SOFTWARE,
    1000L // 1 second refresh is enough for HH:mm
) {
    private var quickPairs: List<QuickCalculation> = emptyList()
    private val backgroundPaint = Paint().apply {
        color = Color.WHITE
        isAntiAlias = true
    }

    private val timePaint = Paint().apply {
        color = Color.parseColor("#7F56D9") // ArkColor.Primary
        textSize = 48f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        isAntiAlias = true
    }


    private val itemTitlePaint = Paint().apply {
        color = Color.parseColor("#101828") // ArkColor.TextPrimary
        textSize = 18f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        isAntiAlias = true
    }

    private val itemSubtitlePaint = Paint().apply {
        color = Color.parseColor("#475467") // ArkColor.TextTertiary
        textSize = 14f
        isAntiAlias = true
    }

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")


    fun updateQuickCalculations(pairs: List<QuickCalculation>) {
        quickPairs = pairs
        invalidate()
    }

    override fun render(canvas: Canvas, bounds: Rect, zonedDateTime: ZonedDateTime) {
        val isAmbient = renderParameters.drawMode == DrawMode.AMBIENT

        val centerX = bounds.centerX().toFloat()
        val centerY = bounds.centerY().toFloat()

        if (isAmbient) {
            canvas.drawColor(Color.BLACK)
            timePaint.color = Color.WHITE
            val timeText = zonedDateTime.format(timeFormatter)
            canvas.drawText(timeText, centerX, centerY, timePaint)
            return
        }

        // Draw Background (White like the app)
        canvas.drawColor(Color.WHITE)

        // Draw Time at the top
        val timeText = zonedDateTime.format(timeFormatter)
        canvas.drawText(timeText, centerX, 60f, timePaint)

        // Draw Quick Pairs
        var currentY = 110f
        val pairsToShow = quickPairs

        if (pairsToShow.isNotEmpty()) {
            for (pair in pairsToShow) {
                val toAmount = pair.to.firstOrNull() ?: continue

                // Draw Icons
                val fromIconId = IconUtils.iconForCurrCode(context, pair.from)
                val fromIcon = context.getDrawable(fromIconId)
                fromIcon?.let {
                    it.setBounds(30, currentY.toInt() - 20, 70, currentY.toInt() + 20)
                    it.draw(canvas)
                }

                val toIconId = IconUtils.iconForCurrCode(context, toAmount.code)
                val toIcon = context.getDrawable(toIconId)
                toIcon?.let {
                    it.setBounds(50, currentY.toInt() - 20, 90, currentY.toInt() + 20)
                    it.draw(canvas)
                }

                // Draw Title: "EUR to USD"
                val titleText = "${pair.from} to ${toAmount.code}"
                canvas.drawText(titleText, 100f, currentY, itemTitlePaint)

                // Draw Subtitle: "1 USD = 0.92 EUR"
                currentY += 25f
                val baseAmountStr = CurrUtils.prepareToDisplay(pair.amount)
                val targetAmountStr = CurrUtils.prepareToDisplay(toAmount.value)
                val subtitleText = "$baseAmountStr ${pair.from} = $targetAmountStr ${toAmount.code}"
                canvas.drawText(subtitleText, 100f, currentY, itemSubtitlePaint)

                currentY += 45f
            }
        } else {
            canvas.drawText("No pinned pairs", centerX, centerY + 20f, itemSubtitlePaint)
        }
    }

    override fun renderHighlightLayer(canvas: Canvas, bounds: Rect, zonedDateTime: ZonedDateTime) {
    }
}
