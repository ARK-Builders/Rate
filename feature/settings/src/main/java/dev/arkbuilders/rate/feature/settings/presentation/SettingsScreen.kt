package dev.arkbuilders.rate.feature.settings.presentation

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.ExternalModuleGraph
import com.ramcosta.composedestinations.generated.settings.destinations.AboutScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.arkbuilders.rate.core.presentation.CoreRDrawable
import dev.arkbuilders.rate.core.presentation.CoreRString
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import dev.arkbuilders.rate.core.presentation.ui.AppHorDiv
import dev.arkbuilders.rate.core.presentation.ui.AppHorDiv16
import dev.arkbuilders.rate.core.presentation.utils.DateFormatUtils
import dev.arkbuilders.rate.feature.settings.di.SettingsComponentHolder
import dev.arkbuilders.rate.feature.settings.domain.model.AppLanguage
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import java.time.Duration
import java.time.OffsetDateTime

@Destination<ExternalModuleGraph>
@Composable
fun SettingsScreen(navigator: DestinationsNavigator) {
    val ctx = LocalContext.current
    val component =
        remember {
            SettingsComponentHolder.provide(ctx)
        }
    val viewModel: SettingsViewModel =
        viewModel(factory = component.settingsVMFactory())

    val state by viewModel.collectAsState()

    BackHandler {
        viewModel.onBackClick()
    }

    viewModel.collectSideEffect { effect ->
        when (effect) {
            SettingsScreenEffect.NavigateToAbout ->
                navigator.navigate(AboutScreenDestination)

            SettingsScreenEffect.NavigateBack -> navigator.popBackStack()
        }
    }

    Scaffold {
        Box(modifier = Modifier.padding(it)) {
            Content(
                state = state,
                navigator = navigator,
                onCrashReportsToggle = viewModel::onCrashReportToggle,
                onAnalyticsToggle = viewModel::onAnalyticsToggle,
                onToggleLanguagePopup = viewModel::onToggleLanguagePopup,
                onChangeLanguage = viewModel::onChangeLanguage,
                onAboutClick = viewModel::onAboutClick,
            )
        }
    }
}

@Composable
private fun Content(
    state: SettingsScreenState,
    navigator: DestinationsNavigator,
    onCrashReportsToggle: (Boolean) -> Unit,
    onAnalyticsToggle: (Boolean) -> Unit,
    onToggleLanguagePopup: (Boolean) -> Unit,
    onChangeLanguage: (AppLanguage) -> Unit,
    onAboutClick: () -> Unit,
) {
    val ctx = LocalContext.current
    Column(
        modifier =
            Modifier
                .padding(vertical = 32.dp)
                .verticalScroll(rememberScrollState()),
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = stringResource(CoreRString.settings_quick_portfolio_alerts),
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            color = ArkColor.TextPrimary,
        )
        val now = OffsetDateTime.now()

        fun formatTime(date: OffsetDateTime): String {
            val elapsed = DateFormatUtils.formatElapsedTime(ctx, now, date)
            val time = DateFormatUtils.formatFullDateTime(date)
            return ctx.getString(CoreRString.settings_elapsed_ago, elapsed) + time
        }

        val refreshDesc =
            state.latestRefresh?.let {
                formatTime(it)
            } ?: stringResource(CoreRString.n_a)
        val pairAlertDesc =
            state.latestPairAlertCheck?.let {
                formatTime(it)
            } ?: stringResource(CoreRString.n_a)
        LatestRefresh(
            title = stringResource(CoreRString.settings_latest_rates_refresh),
            description = refreshDesc,
        )
        AppHorDiv16(modifier = Modifier.padding(top = 20.dp))
        if (state.showCrashReports) {
            Row(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(CoreRString.crash_reports),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = ArkColor.TextPrimary,
                )
                Switch(
                    checked = state.crashReportsEnabled,
                    onCheckedChange = { onCrashReportsToggle(it) },
                )
            }
            AppHorDiv16(modifier = Modifier.padding(top = 20.dp))
        }
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(CoreRString.collect_analytics),
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = ArkColor.TextPrimary,
            )
            Switch(
                checked = state.analyticsEnabled,
                onCheckedChange = { onAnalyticsToggle(it) },
            )
        }
        AppHorDiv16(modifier = Modifier.padding(top = 20.dp))
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clickable { onToggleLanguagePopup(true) }
                    .padding(horizontal = 16.dp, vertical = 20.dp),
        ) {
            Text(
                modifier = Modifier.align(Alignment.CenterStart),
                text = stringResource(CoreRString.language),
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = ArkColor.TextTertiary,
            )
            Text(
                modifier = Modifier.align(Alignment.CenterEnd),
                text = state.language.toDisplayName(ctx),
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = ArkColor.TextTertiary,
            )
        }
        AppHorDiv16(modifier = Modifier)
        if (state.showLanguagePopup) {
            Box {
                Popup(
                    offset = IntOffset(0, 0),
                    properties = PopupProperties(focusable = true),
                    onDismissRequest = { onToggleLanguagePopup(false) },
                ) {
                    Spacer(Modifier.height(4.dp))
                    LanguagesPopup(
                        languages = AppLanguage.entries,
                        onChangeLanguage = {
                            onChangeLanguage(it)
                            onToggleLanguagePopup(false)
                        },
                    )
                }
            }
        }
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clickable { onAboutClick() }
                    .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(CoreRDrawable.ic_info),
                contentDescription = null,
                tint = ArkColor.TextTertiary,
            )
            Text(
                modifier = Modifier.padding(start = 6.dp),
                text = stringResource(CoreRString.about),
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = ArkColor.TextTertiary,
            )
        }
        AppHorDiv16(modifier = Modifier)
    }
}

@Composable
private fun LatestRefresh(
    title: String,
    description: String,
) {
    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp)) {
        Text(
            modifier = Modifier,
            text = title,
            fontWeight = FontWeight.Medium,
            color = ArkColor.TextSecondary,
        )
        Text(
            modifier = Modifier,
            text = description,
            color = ArkColor.TextTertiary,
        )
    }
}

@Composable
private fun LanguagesPopup(
    languages: List<AppLanguage>,
    onChangeLanguage: (AppLanguage) -> Unit,
) {
    val context = LocalContext.current
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        languages.forEach { language ->
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable {
                            onChangeLanguage(language)
                        }
                        .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = language.toDisplayName(context),
                    color = ArkColor.TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
            AppHorDiv()
        }
    }
}

private fun AppLanguage.toDisplayName(context: Context) =
    when (this) {
        AppLanguage.SYSTEM -> context.getString(CoreRString.language_system)
        AppLanguage.EN -> context.getString(CoreRString.language_en)
        AppLanguage.RU -> context.getString(CoreRString.language_ru)
    }

private fun mapElapsedTime(
    now: OffsetDateTime,
    date: OffsetDateTime,
): String? {
    var dur = Duration.between(date, now)
    val days = dur.toDays()
    if (days > 0)
        return null

    val hours = dur.toHours()
    if (hours > 0)
        return "$hours hours"
    dur = dur.minusHours(hours)

    val minutes = dur.toMinutes()
    if (minutes > 0)
        return "$minutes minutes"
    dur = dur.minusMinutes(hours)

    val seconds = dur.seconds
    return "$seconds seconds"
}
