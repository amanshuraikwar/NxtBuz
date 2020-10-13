package io.github.amanshuraikwar.nxtbuz.data.starred

import io.github.amanshuraikwar.nxtbuz.common.model.StarredBusArrivalNotification
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap

class StarredBusArrivalNotificationStore {

    private val map = ConcurrentHashMap<String, StarredBusArrivalNotification>()
    private val availableNotificationIdSet = mutableSetOf<Int>()
    private val mutex = Mutex()

    suspend fun getNotification(
        busStopCode: String,
        busServiceNumber: String
    ): StarredBusArrivalNotification? = mutex.withLock {
        return map["$busServiceNumber>$busStopCode"]
    }

    suspend fun putNotification(
        busStopCode: String,
        busServiceNumber: String,
        notification: StarredBusArrivalNotification,
    ) = mutex.withLock {
        map["$busServiceNumber>$busStopCode"] = notification
    }

    fun getSize(): Int = map.size

    suspend fun clear() = mutex.withLock {
        map.clear()
    }

    /**
     * @return map of busServiceNumber: [String] -> [StarredBusArrivalNotification]
     * for the bus stop [busStopCode]
     * for bus service numbers which are not in [busServiceNumberSet]
     */
    suspend fun minus(
        busStopCode: String,
        busServiceNumberSet: Set<String>
    ): Map<String, StarredBusArrivalNotification> = mutex.withLock {
        return map
            .filter { (key, _) ->
                key.endsWith(busStopCode)
            }
            .minus(
                busServiceNumberSet.map { busServiceNumber -> "$busServiceNumber>$busStopCode" }
            )
            .mapKeys { (key, _) ->
                key.substringBefore(">$busStopCode")
            }
    }

    suspend fun remove(busStopCode: String, busServiceNumber: String) = mutex.withLock {
        map.remove("$busServiceNumber>$busStopCode")?.let { notification ->
            availableNotificationIdSet.add(notification.notificationId)
        }
    }

    /**
     * @return  if an id is already available from an element removed from middle before.
     *          else 1 + size of the map.
     *
     * This prevents:
     *  [1] Notification id clashes
     *  [2] Prevents notification id's from overflowing integer range.
     *
     * Note: that `map.size + 1` can be equal to `availableNotificationIdSet.elementAt(0)`
     */
    suspend fun createNewNotificationId(): Int = mutex.withLock {
        if (availableNotificationIdSet.isNotEmpty()) {
            val availableId = availableNotificationIdSet.elementAt(0)
            availableNotificationIdSet.remove(availableId)
            availableId
        } else {
            map.size + 1
        }
    }
}