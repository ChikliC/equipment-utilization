package com.chikli.bl

import assertk.assertThat
import assertk.assertions.containsExactly
import com.chikli.bl.EquipmentType.ELLIPTICAL
import com.chikli.bl.EquipmentType.TREADMILL
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.LocalDateTime

class ConcurrentUseTest {

    @Test
    fun `single equipment`() {
        val sessions = listOf(
            Session(Equipment("Treadmill 1", TREADMILL), LocalDateTime.parse("2022-01-01T08:40:00"), LocalDateTime.parse("2022-01-01T08:50:00"))
        )

        val sessionAnalyzer = SessionAnalyzer(sessions)
        val results = sessionAnalyzer.analyze()

        assertThat(results).containsExactly(
            EquipmentUsage(TREADMILL, listOf(Usage(1, 10)))
        )
    }

    @Test
    fun `another equipment`() {
        val sessions = listOf(
            Session(Equipment("Elliptical 1", ELLIPTICAL), LocalDateTime.parse("2022-01-01T09:05:00"), LocalDateTime.parse("2022-01-01T10:22:00"))
        )

        val sessionAnalyzer = SessionAnalyzer(sessions)
        val results = sessionAnalyzer.analyze()

        assertThat(results).containsExactly(
            EquipmentUsage(ELLIPTICAL, listOf(Usage(1, 77)))
        )
    }

    @Test
    fun `multiple equipment at the same time`() {
        val sessions = listOf(
            Session(Equipment("Elliptical 1", ELLIPTICAL), LocalDateTime.parse("2022-01-01T09:05:00"), LocalDateTime.parse("2022-01-01T10:22:00")),
            Session(Equipment("Elliptical 2", ELLIPTICAL), LocalDateTime.parse("2022-01-01T09:05:00"), LocalDateTime.parse("2022-01-01T10:22:00"))
        )

        val sessionAnalyzer = SessionAnalyzer(sessions)
        val results = sessionAnalyzer.analyze()

        assertThat(results).containsExactly(
            EquipmentUsage(ELLIPTICAL, listOf(Usage(2, 77)))
        )
    }

}

class SessionAnalyzer(private val sessions: List<Session>) {
    fun analyze(): List<EquipmentUsage> {
        return sessions.map {
            EquipmentUsage(it.equipment.type, listOf(Usage(sessions.size, Duration.between(it.start, it.end).toMinutes())))
        }.distinct()
    }

}

data class EquipmentUsage(val equipmentType: EquipmentType, val usages: List<Usage>)

data class Usage(val machineCount: Int, val minutes: Long)

data class Session(val equipment: Equipment, val start: LocalDateTime, val end: LocalDateTime)

data class Equipment(val name: String, val type: EquipmentType)


enum class EquipmentType {
    TREADMILL,
    ELLIPTICAL
}
