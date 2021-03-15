package io.github.amanshuraikwar.nxtbuz.busstop.arrivals

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import io.github.amanshuraikwar.nxtbuz.busstop.arrivals.item.BusStopArrivalItem
import io.github.amanshuraikwar.nxtbuz.busstop.arrivals.item.BusStopArrivalItems
import io.github.amanshuraikwar.nxtbuz.busstop.theme.NxtBuzTheme
import io.github.amanshuraikwar.nxtbuz.busstop.util.PreviewSurface

class ComposeTestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
//            NxtBuzTheme {
//                BusStopArrivalItems()
//            }
        }
    }
}