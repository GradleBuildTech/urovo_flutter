package com.example.urovo_flutter.utils

enum class DeviceEnum(val value: String) {
    UROVO("UROVO"),
    OTHER("OTHER");

    companion object {
        fun fromValue(value: String): DeviceEnum {
            return values().find { it.value == value } ?: OTHER
        }
    }
}