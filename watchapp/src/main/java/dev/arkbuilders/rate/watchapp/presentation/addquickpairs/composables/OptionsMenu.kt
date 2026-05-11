package dev.arkbuilders.rate.watchapp.presentation.addquickpairs.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material.Text
import dev.arkbuilders.rate.core.presentation.CoreRDrawable
import dev.arkbuilders.rate.core.presentation.CoreRString

@Composable
fun OptionsMenu(modifier: Modifier = Modifier) {
    ScalingLazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                modifier = modifier.fillMaxWidth(),
                text = "Options",
                textAlign = TextAlign.Center
            )
        }
        item {
            OptionItem(
                modifier = Modifier
                    .fillMaxWidth(),
                icon = painterResource(id = CoreRDrawable.ic_update),
                text = stringResource(id = CoreRString.update),
                onClick = {},
            )
        }
        item {
            OptionItem(
                modifier = Modifier
                    .fillMaxWidth(),
                icon = painterResource(id = CoreRDrawable.ic_pin),
                text = stringResource(id = CoreRString.pin),
                onClick = {},
            )
        }

        item {
            OptionItem(
                modifier = Modifier
                    .fillMaxWidth(),
                icon = painterResource(id = CoreRDrawable.ic_edit),
                text = stringResource(id = CoreRString.edit),
                onClick = {},
            )
        }

        item {
            OptionItem(
                modifier = Modifier
                    .fillMaxWidth(),
                icon = painterResource(id = CoreRDrawable.ic_reuse),
                text = stringResource(id = CoreRString.re_use),
                onClick = {},
            )
        }
        item {
            OptionItem(
                modifier = Modifier
                    .fillMaxWidth(),
                icon = painterResource(id = CoreRDrawable.ic_delete),
                text = stringResource(id = CoreRString.delete),
                onClick = {},
                isDeleteButton = true
            )
        }
    }
}

@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Composable
fun AddQuickCalculationsPreview() {
    OptionsMenu()
}
