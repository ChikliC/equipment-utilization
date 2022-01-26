package com.chikli.bl

import com.chikli.bl.EquipmentType.ELLIPTICAL
import com.chikli.bl.EquipmentType.TREADMILL
import java.time.LocalDateTime

fun main() {
    val sessions = listOf(
        Session(Equipment("Treadmill 1", TREADMILL), LocalDateTime.parse("2022-01-01T08:40:00"), LocalDateTime.parse("2022-01-01T08:50:00")),
        Session(Equipment("Treadmill 2", TREADMILL), LocalDateTime.parse("2022-01-01T09:15:00"), LocalDateTime.parse("2022-01-01T09:46:00")),
        Session(Equipment("Treadmill 3", TREADMILL), LocalDateTime.parse("2022-01-01T11:45:00"), LocalDateTime.parse("2022-01-01T12:04:00")),
        Session(Equipment("Elliptical 1", ELLIPTICAL), LocalDateTime.parse("2022-01-01T09:00:00"), LocalDateTime.parse("2022-01-01T09:30:00")),
        Session(Equipment("Treadmill 4", TREADMILL), LocalDateTime.parse("2022-01-01T09:26:00"), LocalDateTime.parse("2022-01-01T10:19:00")),
        Session(Equipment("Elliptical 2", ELLIPTICAL), LocalDateTime.parse("2022-01-01T10:00:00"), LocalDateTime.parse("2022-01-01T10:45:00")),
        Session(Equipment("Elliptical 3", ELLIPTICAL), LocalDateTime.parse("2022-01-01T08:00:00"), LocalDateTime.parse("2022-01-01T09:30:00")),
        Session(Equipment("Treadmill 5", TREADMILL), LocalDateTime.parse("2022-01-01T08:00:00"), LocalDateTime.parse("2022-01-01T09:35:00")),
    )

    val analyzer = EquipmentUtilizationAnalyzer()

    val results = analyzer.analyze(sessions)

    results.forEach {
        println(it.equipmentType)
        it.usages.forEach { usage ->
            println("\t${usage.machineCount} machines:\t${usage.minutes}")
        }
    }
}

class EquipmentUtilizationAnalyzer {
    fun analyze(sessions: List<Session>): List<EquipmentUsage> =
        sessions
            .groupBy { it.equipment.type }
            .map { (equipmentType, equipmentSessions) -> equipmentUsageFor(equipmentType, equipmentSessions) }

    private fun equipmentUsageFor(equipmentType: EquipmentType, sessions: List<Session>): EquipmentUsage {
        val countsByMinute = countsByMinute(sessions)
        val usages = countsByMinute.groupBy { it }.map { Usage(it.key, it.value.count()) }
        return EquipmentUsage(equipmentType, usages)
    }

    private fun countsByMinute(equipmentSessions: List<Session>): List<Int> {
        val minStart = equipmentSessions.minOf { it.start }
        val maxEnd = equipmentSessions.maxOf { it.end }

        var currentTime = minStart.withSecond(0).withNano(0)
        val countsByMinute = mutableListOf<Int>()
        while (currentTime <= maxEnd) {
            val concurrent = equipmentSessions.count { it.start <= currentTime && it.end > currentTime }
            if (concurrent > 0) countsByMinute += concurrent
            currentTime = currentTime.plusMinutes(1)
        }
        return countsByMinute
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
