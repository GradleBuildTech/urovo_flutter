package com.example.urovo_flutter.model

data class BeeperModel(
    val cnts: Int? = null,
    val msecTime: Int? = null
)

fun BeeperModel.toJson(): Map<String, Any?> {
    val json = mutableMapOf<String, Any?>()
    cnts?.let { json["cnts"] = it }
    msecTime?.let { json["msecTime"] = it }
    return json
}

fun Map<*, *>.toBeeperModel(): BeeperModel {
    val cnts = this["cnts"] as? Int
    val msecTime = this["msecTime"] as? Int
    return BeeperModel(cnts, msecTime)
}