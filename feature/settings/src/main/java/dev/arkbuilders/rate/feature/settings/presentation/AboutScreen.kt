package dev.arkbuilders.rate.feature.settings.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.ExternalModuleGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.arkbuilders.components.about.presentation.ArkAbout
import dev.arkbuilders.rate.core.presentation.CoreRDrawable
import dev.arkbuilders.rate.core.presentation.CoreRString
import dev.arkbuilders.rate.core.presentation.ui.AppTopBarBack
import dev.arkbuilders.rate.feature.settings.di.SettingsComponentHolder

@Destination<ExternalModuleGraph>
@Composable
fun AboutScreen(navigator: DestinationsNavigator) {
    val ctx = LocalContext.current
    val component = SettingsComponentHolder.provide(ctx)
    Scaffold(
        topBar = {
            AppTopBarBack(
                title = stringResource(CoreRString.about),
                onBackClick = { navigator.popBackStack() },
            )
        },
    ) {
        ArkAbout(
            modifier = Modifier.padding(it),
            appName = stringResource(id = CoreRString.app_name),
            appLogoResId = CoreRDrawable.ic_app_logo,
            versionName = component.buildConfigFields().versionName,
            privacyPolicyUrl = stringResource(CoreRString.privacy_policy_url),
        )
    }
}
