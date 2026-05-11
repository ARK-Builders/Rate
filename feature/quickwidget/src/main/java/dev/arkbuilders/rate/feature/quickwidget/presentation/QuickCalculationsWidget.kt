package dev.arkbuilders.rate.feature.quickwidget.presentation

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import dev.arkbuilders.rate.core.domain.model.GroupFeatureType
import dev.arkbuilders.rate.core.presentation.CoreRDrawable
import dev.arkbuilders.rate.core.presentation.CoreRString
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import dev.arkbuilders.rate.feature.quickwidget.di.QuickWidgetComponentHolder
import dev.arkbuilders.rate.feature.quickwidget.presentation.action.AddNewCalculationAction
import dev.arkbuilders.rate.feature.quickwidget.presentation.action.NextPageAction
import dev.arkbuilders.rate.feature.quickwidget.presentation.action.OpenAppAction
import dev.arkbuilders.rate.feature.quickwidget.presentation.action.PreviousPageAction

class QuickCalculationsWidget : GlanceAppWidget() {
    override suspend fun provideGlance(
        context: Context,
        id: GlanceId,
    ) {
        val quickComponent = QuickWidgetComponentHolder.provide(context)
        val getPinnedUseCase = quickComponent.getPinnedQuickCalculationUseCase()
        val pinned = getPinnedUseCase.invoke()
        val groups = quickComponent.groupRepo().getAllSorted(GroupFeatureType.Quick)
        provideContent {
            val prefs = currentState<Preferences>()
            val groupId = prefs[QuickCalculationsWidgetReceiver.currentGroupIdKey]
            val quickCalculationsList = pinned.filter { it.calculation.group.id == groupId }
            val displayGroup = groups.find { it.id == groupId }
            displayGroup ?: return@provideContent
            Column(
                modifier =
                    GlanceModifier.fillMaxSize().background(Color.White)
                        .padding(horizontal = 12.dp),
            ) {
                Row(
                    modifier = GlanceModifier.fillMaxWidth().padding(top = 8.dp),
                    verticalAlignment = Alignment.Vertical.CenterVertically,
                ) {
                    Image(
                        modifier =
                            GlanceModifier.size(24.dp).padding(4.dp)
                                .clickable(actionRunCallback<AddNewCalculationAction>()),
                        provider = ImageProvider(CoreRDrawable.ic_app_logo),
                        contentDescription = null,
                    )
                    Text(
                        modifier = GlanceModifier.defaultWeight(),
                        text = context.getString(CoreRString.quick_pinned_calculations),
                        style =
                            TextStyle(
                                color = ColorProvider(ArkColor.TextTertiary),
                                fontWeight = FontWeight.Medium,
                            ),
                    )
                    Text(
                        modifier = GlanceModifier.defaultWeight(),
                        text = displayGroup.name,
                        style =
                            TextStyle(
                                color = ColorProvider(ArkColor.TextTertiary),
                                fontWeight = FontWeight.Medium,
                            ),
                    )
                    Image(
                        modifier =
                            GlanceModifier.size(24.dp).padding(4.dp)
                                .clickable(actionRunCallback<PreviousPageAction>()),
                        provider = ImageProvider(CoreRDrawable.ic_chevron_left),
                        contentDescription = null,
                    )

                    Image(
                        modifier =
                            GlanceModifier.size(24.dp).padding(4.dp)
                                .clickable(actionRunCallback<NextPageAction>()),
                        provider = ImageProvider(CoreRDrawable.ic_chevron_right),
                        contentDescription = null,
                    )
                    Image(
                        modifier =
                            GlanceModifier.size(24.dp).padding(4.dp)
                                .clickable(actionRunCallback<AddNewCalculationAction>()),
                        provider =
                            ImageProvider(
                                CoreRDrawable.ic_add_circle,
                            ),
                        contentDescription = null,
                    )
                    Text(
                        modifier =
                            GlanceModifier
                                .clickable(actionRunCallback<OpenAppAction>()),
                        text = context.getString(CoreRString.quick_open_app),
                        style =
                            TextStyle(
                                color =
                                    ColorProvider(
                                        ArkColor.Primary,
                                    ),
                                fontWeight = FontWeight.Medium,
                            ),
                    )
                }
                LazyColumn(modifier = GlanceModifier.fillMaxHeight()) {
                    items(quickCalculationsList) { quick ->
                        Column {
                            QuickCalculationItem(
                                quick = quick,
                                context = context,
                            )
                            Spacer(
                                modifier =
                                    GlanceModifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(Color.Gray.copy(alpha = 0.2f))
                                        .padding(vertical = 2.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}
