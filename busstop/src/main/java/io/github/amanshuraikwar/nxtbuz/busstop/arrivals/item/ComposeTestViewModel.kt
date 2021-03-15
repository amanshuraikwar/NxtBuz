package io.github.amanshuraikwar.nxtbuz.busstop.arrivals.item

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.amanshuraikwar.nxtbuz.common.model.BusLoad
import io.github.amanshuraikwar.nxtbuz.common.model.BusType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ComposeTestViewModel : ViewModel() {
    val list = SnapshotStateList<Data>().apply {
        addAll(
            List(20) {
                Data()
            }
        )
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            //delay(2000)
            list.addAll(
                List(20) {
                    Data()
                }
            )
            delay(3000)
            for (i in 0 until list.size) {
                list[i] = Data()
            }
        }
    }
}

data class Data(
    val busServiceNumber: String,
    val destinationBusStopDescription: String,
    val busLoad: BusLoad,
    val wheelchairAccess: Boolean,
    val busType: BusType,
    val arrival: String,
) {
    companion object {
        operator fun invoke() = Data(
            "961M",
            destinationBusStopDescription = "MARINE CTR RD",
            busLoad = BusLoad.values().random(),
            wheelchairAccess = listOf(true, false).random(),
            busType = BusType.values().random(),
            arrival = "in 04 mins"
        )
    }
}