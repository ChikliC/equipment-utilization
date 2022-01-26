package com.chikli.bl

import java.time.LocalDateTime

class EquipmentUtilizationAnalyzer(private val sessions: List<Session>) {
    fun analyze(): List<EquipmentUsage> =
        sessions
            .groupBy { it.equipment.type }
            .map { (equipmentType, equipmentSessions) -> equipmentUsageFor(equipmentType, equipmentSessions) }

    private fun equipmentUsageFor(equipmentType: EquipmentType, equipmentSessions: List<Session>): EquipmentUsage {
        val minStart = equipmentSessions.minOf { it.start }
        val maxEnd = equipmentSessions.maxOf { it.end }

        var currentTime = minStart.withSecond(0).withNano(0)
        val countsByMinute = mutableListOf<Int>()
        while (currentTime <= maxEnd) {
            val concurrent = equipmentSessions.count { it.start <= currentTime && it.end > currentTime }
            if (concurrent > 0) countsByMinute += concurrent
            currentTime = currentTime.plusMinutes(1)
        }

        val usages = countsByMinute.groupBy { it }.map { Usage(it.key, it.value.count()) }
        return EquipmentUsage(equipmentType, usages)
    }

}

data class EquipmentUsage(val equipmentType: EquipmentType, val usages: List<Usage>)

data class Usage(val machineCount: Int, val minutes: Int)

data class Session(val equipment: Equipment, val start: LocalDateTime, val end: LocalDateTime)

data class Equipment(val name: String, val type: EquipmentType)

enum class EquipmentType {
    TREADMILL,
    ELLIPTICAL
}
