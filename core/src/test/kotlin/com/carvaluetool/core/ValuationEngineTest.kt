package com.carvaluetool.core

import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ValuationEngineTest {

    private val engine = ValuationEngine(currentYear = 2026)
    private val toyota = VehicleCatalog.findMake("Toyota")!!
    private val camry = toyota.models.first { it.name == "Camry" }

    private fun camry(year: Int = 2021, mileage: Int = 60_000) =
        VehicleInput(year = year, make = toyota, model = camry, mileage = mileage)

    @Test
    fun `value ordering is trade-in below private party below retail`() {
        val result = engine.appraise(camry())
        assertTrue(result.tradeIn.mid < result.privateParty.mid)
        assertTrue(result.privateParty.mid < result.dealerRetail.mid)
        assertTrue(result.privateParty.low < result.privateParty.mid)
        assertTrue(result.privateParty.high > result.privateParty.mid)
    }

    @Test
    fun `five year old camry lands in a plausible range`() {
        val value = engine.appraise(camry()).privateParty.mid
        assertTrue(value in 13_000..24_000, "Unexpected value $value")
    }

    @Test
    fun `newer cars are worth more than older cars`() {
        val newer = engine.appraise(camry(year = 2023, mileage = 36_000)).privateParty.mid
        val older = engine.appraise(camry(year = 2015, mileage = 132_000)).privateParty.mid
        assertTrue(newer > older)
    }

    @Test
    fun `higher mileage lowers value`() {
        val low = engine.appraise(camry(mileage = 30_000)).privateParty.mid
        val high = engine.appraise(camry(mileage = 120_000)).privateParty.mid
        assertTrue(low > high)
    }

    @Test
    fun `condition, wear and title reduce value`() {
        val clean = engine.appraise(camry()).privateParty.mid
        val worn = engine.appraise(
            camry().copy(
                condition = Condition.FAIR,
                wearItems = setOf(WearItem.DENTS, WearItem.TIRES),
                titleStatus = TitleStatus.SALVAGE,
            )
        ).privateParty.mid
        assertTrue(worn < clean * 0.5)
    }

    @Test
    fun `fixed repair costs are itemized in breakdown`() {
        val result = engine.appraise(camry().copy(wearItems = setOf(WearItem.TIRES)))
        assertTrue(result.adjustments.any { it.amount == -WearItem.TIRES.fixedCost })
    }

    @Test
    fun `very old car never drops below floor`() {
        val civic = VehicleCatalog.findMake("Honda")!!.models.first { it.name == "Civic" }
        val result = engine.appraise(
            VehicleInput(year = 1990, make = VehicleCatalog.findMake("Honda")!!, model = civic, mileage = 320_000,
                condition = Condition.POOR)
        )
        assertTrue(result.privateParty.mid >= 1_000)
        assertTrue(result.tradeIn.mid > 0)
    }

    @Test
    fun `luxury brands depreciate faster than toyota`() {
        val bmw = VehicleCatalog.findMake("BMW")!!
        val x5 = bmw.models.first { it.name == "X5" }
        val lexus = VehicleCatalog.findMake("Lexus")!!
        val rx = lexus.models.first { it.name == "RX" }
        val bmwRetention = engine.appraise(VehicleInput(2020, bmw, x5, mileage = 72_000)).let { it.privateParty.mid.toDouble() / it.estimatedOriginalMsrp }
        val lexusRetention = engine.appraise(VehicleInput(2020, lexus, rx, mileage = 72_000)).let { it.privateParty.mid.toDouble() / it.estimatedOriginalMsrp }
        assertTrue(lexusRetention > bmwRetention)
    }

    @Test
    fun `catalog filters models by year`() {
        val ford = VehicleCatalog.findMake("Ford")!!
        val names2026 = VehicleCatalog.modelsFor(ford, 2026).map { it.name }
        assertTrue("Fiesta" !in names2026)
        assertTrue("F-150" in names2026)
        assertEquals(names2026.size, names2026.toSet().size)
        assertTrue(VehicleCatalog.makesFor(1995).none { it.name == "Tesla" })
    }

    @Test
    fun `report contains values and safe file name`() {
        val result = engine.appraise(camry())
        val report = ValuationReport(result, LocalDateTime.of(2026, 10, 29, 9, 30))
        val text = report.toPlainText()
        assertTrue(text.contains("2021 Toyota Camry"))
        assertTrue(text.contains(formatCurrency(result.privateParty.mid)))
        assertEquals("CarValue_2021_Toyota_Camry_20261029", report.suggestedFileName)
    }
}
