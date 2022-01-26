package com.chikli.bl

import assertk.assertThat
import assertk.assertions.containsExactly
import com.chikli.bl.EquipmentType.ELLIPTICAL
import com.chikli.bl.EquipmentType.TREADMILL
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class EquipmentUtilizationAnalyzerTest {

    @Test
    fun `single equipment`() {
        val sessions = listOf(
            Session(Equipment("Treadmill 1", TREADMILL), LocalDateTime.parse("2022-01-01T08:40:00"), LocalDateTime.parse("2022-01-01T08:50:00"))
        )

        val analyzer = EquipmentUtilizationAnalyzer(sessions)
        val results = analyzer.analyze()

        assertThat(results).containsExactly(
            EquipmentUsage(TREADMILL, listOf(Usage(1, 10)))
        )
    }

    @Test
    fun `another equipment`() {
        val sessions = listOf(
            Session(Equipment("Elliptical 1", ELLIPTICAL), LocalDateTime.parse("2022-01-01T09:05:00"), LocalDateTime.parse("2022-01-01T10:22:00"))
        )

        val analyzer = EquipmentUtilizationAnalyzer(sessions)
        val results = analyzer.analyze()

        assertThat(results).containsExactly(
            EquipmentUsage(ELLIPTICAL, listOf(Usage(1, 77)))
        )
    }

    @Test
    fun `same equipment type at the same time`() {
        val sessions = listOf(
            Session(Equipment("Elliptical 1", ELLIPTICAL), LocalDateTime.parse("2022-01-01T09:05:00"), LocalDateTime.parse("2022-01-01T10:22:00")),
            Session(Equipment("Elliptical 2", ELLIPTICAL), LocalDateTime.parse("2022-01-01T09:05:00"), LocalDateTime.parse("2022-01-01T10:22:00"))
        )

        val analyzer = EquipmentUtilizationAnalyzer(sessions)
        val results = analyzer.analyze()

        assertThat(results).containsExactly(
            EquipmentUsage(ELLIPTICAL, listOf(Usage(2, 77)))
        )
    }

    @Test
    fun `same equipment at different times`() {
        val sessions = listOf(
            Session(Equipment("Elliptical 1", ELLIPTICAL), LocalDateTime.parse("2022-01-01T09:00:00"), LocalDateTime.parse("2022-01-01T09:30:00")),
            Session(Equipment("Elliptical 2", ELLIPTICAL), LocalDateTime.parse("2022-01-01T10:00:00"), LocalDateTime.parse("2022-01-01T10:45:00"))
        )

        val analyzer = EquipmentUtilizationAnalyzer(sessions)
        val results = analyzer.analyze()

        assertThat(results).containsExactly(
            EquipmentUsage(ELLIPTICAL, listOf(Usage(1, 75)))
        )
    }

    @Test
    fun `same equipment at overlapping times`() {
        val sessions = listOf(
            Session(Equipment("Elliptical 1", ELLIPTICAL), LocalDateTime.parse("2022-01-01T09:00:00"), LocalDateTime.parse("2022-01-01T09:30:00")),
            Session(Equipment("Elliptical 2", ELLIPTICAL), LocalDateTime.parse("2022-01-01T09:26:00"), LocalDateTime.parse("2022-01-01T10:19:00")),
            Session(Equipment("Elliptical 3", ELLIPTICAL), LocalDateTime.parse("2022-01-01T10:00:00"), LocalDateTime.parse("2022-01-01T10:45:00"))
        )

        val analyzer = EquipmentUtilizationAnalyzer(sessions)
        val results = analyzer.analyze()

        assertThat(results).containsExactly(
            EquipmentUsage(ELLIPTICAL, listOf(Usage(1, 82), Usage(2, 23)))
        )
    }

    @Test
    fun `different equipment at overlapping times`() {
        val sessions = listOf(
            Session(Equipment("Elliptical 1", ELLIPTICAL), LocalDateTime.parse("2022-01-01T09:00:00"), LocalDateTime.parse("2022-01-01T09:30:00")),
            Session(Equipment("Treadmill 1", TREADMILL), LocalDateTime.parse("2022-01-01T09:26:00"), LocalDateTime.parse("2022-01-01T10:19:00")),
            Session(Equipment("Elliptical 2", ELLIPTICAL), LocalDateTime.parse("2022-01-01T10:00:00"), LocalDateTime.parse("2022-01-01T10:45:00"))
        )

        val analyzer = EquipmentUtilizationAnalyzer(sessions)
        val results = analyzer.analyze()

        assertThat(results).containsExactly(
            EquipmentUsage(ELLIPTICAL, listOf(Usage(1, 75))),
            EquipmentUsage(TREADMILL, listOf(Usage(1, 53)))
        )
    }
}


