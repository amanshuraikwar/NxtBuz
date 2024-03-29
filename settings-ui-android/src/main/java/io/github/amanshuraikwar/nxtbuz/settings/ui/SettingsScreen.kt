package io.github.amanshuraikwar.nxtbuz.settings.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.statusBarsPadding
import io.github.amanshuraikwar.nxtbuz.common.compose.HeaderView
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.h6Bold
import io.github.amanshuraikwar.nxtbuz.common.compose.util.itemsIndexedSafe
import io.github.amanshuraikwar.nxtbuz.settings.ui.model.SettingsItemData

@ExperimentalAnimationApi
@Composable
fun SettingsScreen(
    vm: SettingsViewModel,
    onBackClick: () -> Unit,
) {
    val listItems by vm.listItemsFlow.collectAsState(emptyList())

    DisposableEffect(key1 = null) {
        vm.fetchSettings()
        onDispose {
            // do nothing
        }
    }

    Column {
        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            color = MaterialTheme.colors.surface,
            elevation = 4.dp
        ) {
            Row(
                Modifier.statusBarsPadding()
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = "Star",
                    tint = MaterialTheme.colors.onSurface,
                    modifier = Modifier
                        .clip(shape = MaterialTheme.shapes.small)
                        .clickable {
                            onBackClick()
                        }
                        .padding(16.dp)
                        .size(24.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "Settings",
                    modifier = Modifier.align(Alignment.CenterVertically),
                    style = MaterialTheme.typography.h6Bold,
                    color = MaterialTheme.colors.onSurface
                )
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(
                bottom = 128.dp,
                top = 0.dp,
            )
        ) {
            itemsIndexedSafe(
                items = listItems,
                key = { _, item ->
                    when (item) {
                        is SettingsItemData.About -> "about"
                        is SettingsItemData.RadioGroup -> item.title
                        is SettingsItemData.Header -> item.title
                        is SettingsItemData.Switch -> item.title
                        SettingsItemData.Oss -> "oss"
                        SettingsItemData.MadeWith -> "made-with"
                        SettingsItemData.MadeBy -> "made-by"
                        SettingsItemData.RequestFeature -> "request-feature"
                        SettingsItemData.RateOnPlayStore -> "rate-on-play-store"
                    }
                },
                errorKey = "bus-route-arrivals-error-key",
            ) { _, item ->
                when (item) {
                    is SettingsItemData.About -> {
                        AboutItem(
                            appName = item.appName,
                            versionName = item.versionName
                        )
                    }
                    is SettingsItemData.RadioGroup -> {
                        RadioGroupSettingItem(radioGroup = item)
                    }
                    is SettingsItemData.Header -> {
                        HeaderView(
                            Modifier.padding(top = 8.dp),
                            title = item.title
                        )
                    }
                    is SettingsItemData.Switch -> {
                        SwitchSettingItem(item)
                    }
                    SettingsItemData.Oss -> {
                        ButtonItem("Open Source Licenses") {
                            vm.onOssClick()
                        }
                    }
                    SettingsItemData.RequestFeature -> {
                        ButtonItem("Request a Feature") {
                            vm.onRequestFeatureClick()
                        }
                    }
                    SettingsItemData.MadeBy -> {
                        ButtonItem("Made by Amanshu Raikwar") {
                            vm.onMadeByClick()
                        }
                    }
                    SettingsItemData.RateOnPlayStore -> {
                        ButtonItem("Rate on Play Store") {
                            vm.onRateOnPlayStoreClick()
                        }
                    }
                    SettingsItemData.MadeWith -> {
                        MadeWithItem()
                    }
                }
            }
        }
    }
}