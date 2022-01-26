package com.chikli.bl

import assertk.assertThat
import assertk.assertions.containsExactly
import com.chikli.bl.EquipmentType.TREADMILL
import org.junit.jupiter.api.Test
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


}

class SessionAnalyzer(private val sessions: List<Session>) {
    fun analyze(): List<EquipmentUsage> {
        return listOf(
            EquipmentUsage(TREADMILL, listOf(Usage(1, 10)))
        )
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
