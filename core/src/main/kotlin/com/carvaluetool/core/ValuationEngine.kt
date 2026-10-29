package com.carvaluetool.core

import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * Offline fair-market-value estimator.
 *
 * The model starts from an estimated original MSRP, applies a depreciation curve
 * based on segment and brand retention, then adjusts for mileage, condition,
 * history and wear-and-tear. Values are estimates, not live market quotes.
 */
class ValuationEngine(private val currentYear: Int) {

    fun appraise(input: VehicleInput): ValuationResult {
        require(input.mileage >= 0) { "Mileage cannot be negative" }
        require(input.year in VehicleCatalog.EARLIEST_YEAR..currentYear + 1) { "Unsupported model year ${input.year}" }

        val age = max(0, currentYear - input.year)
        val segment = input.model.segment

        // Older model years sold for less in nominal dollars.
        val originalMsrp = input.model.baseMsrp * input.trim.factor / INFLATION.pow(age)

        val retention = retention(age, segment.annualDepreciation * input.make.depreciationFactor)
        val floor = max(MIN_VALUE, originalMsrp * FLOOR_FRACTION)
        val baseValue = max(originalMsrp * retention, floor)

        val adjustments = mutableListOf<Adjustment>()
        var value = baseValue

        fun applyFactor(label: String, factor: Double) {
            if (factor == 1.0) return
            val next = value * factor
            adjustments += Adjustment(label, (next - value).roundToInt())
            value = next
        }

        val expectedMileage = segment.expectedMilesPerYear * max(age, 1)
        applyFactor(mileageLabel(input.mileage, expectedMileage), mileageFactor(input.mileage, expectedMileage, age))
        applyFactor("Condition: ${input.condition.label}", input.condition.factor)
        applyFactor("Drivetrain: ${input.drivetrain.label}", drivetrainFactor(input))
        applyFactor("Transmission: ${input.transmission.label}", transmissionFactor(input))
        applyFactor("Accident history: ${input.accidentHistory.label}", input.accidentHistory.factor)
        applyFactor("Title: ${input.titleStatus.label}", input.titleStatus.factor)
        applyFactor("Ownership: ${input.owners.label}", input.owners.factor)
        applyFactor("Service history: ${input.serviceRecords.label}", input.serviceRecords.factor)
        applyFactor("Color: ${input.color.label}", input.color.factor)
        applyFactor("Region: ${input.region.label}", regionFactor(input))

        WearItem.entries.filter { it in input.wearItems }.forEach { item ->
            if (item.percentDeduction > 0) applyFactor(item.label, 1.0 - item.percentDeduction)
            if (item.fixedCost > 0) {
                adjustments += Adjustment("${item.label} (repair cost)", -item.fixedCost)
                value -= item.fixedCost
            }
        }

        val privateMid = roundTo(max(value, MIN_VALUE))
        val tradeMid = roundTo(max(privateMid * TRADE_IN_RATIO, MIN_VALUE * TRADE_IN_RATIO))
        val retailMid = roundTo(privateMid * RETAIL_RATIO + RETAIL_PREP)

        val spread = spread(age, input)
        return ValuationResult(
            input = input,
            estimatedOriginalMsrp = roundTo(originalMsrp),
            baseValue = roundTo(baseValue),
            adjustments = adjustments,
            tradeIn = range(tradeMid, spread),
            privateParty = range(privateMid, spread),
            dealerRetail = range(retailMid, spread),
            expectedMileage = expectedMileage,
            confidence = when {
                spread <= 0.05 -> "High"
                spread <= 0.08 -> "Moderate"
                else -> "Low"
            },
        )
    }

    /** Fraction of original MSRP retained after [age] years at the given base yearly rate. */
    internal fun retention(age: Int, baseRate: Double): Double {
        var retained = INITIAL_RETENTION
        for (yearIndex in 1..age) {
            val rate = when {
                yearIndex == 1 -> baseRate * 1.2
                yearIndex <= 5 -> baseRate
                yearIndex <= 10 -> baseRate * 0.95
                else -> baseRate * 0.85
            }
            retained *= 1.0 - min(rate, 0.5)
        }
        return retained
    }

    internal fun mileageFactor(mileage: Int, expected: Int, age: Int): Double {
        val perThousand = when {
            age <= 5 -> 0.006
            age <= 10 -> 0.004
            else -> 0.0025
        }
        val delta = (mileage - expected) / 1000.0
        var factor = 1.0 - (delta * perThousand).coerceIn(-0.20, 0.35)
        factor *= when {
            mileage > 200_000 -> 0.90
            mileage > 150_000 -> 0.95
            else -> 1.0
        }
        return factor
    }

    private fun mileageLabel(mileage: Int, expected: Int): String {
        val diff = mileage - expected
        val relation = if (diff >= 0) "above" else "below"
        return "Mileage: ${formatNumber(mileage)} mi (${formatNumber(kotlin.math.abs(diff))} $relation expected)"
    }

    private fun drivetrainFactor(input: VehicleInput): Double {
        val fourWheel = input.drivetrain == Drivetrain.AWD || input.drivetrain == Drivetrain.FOUR_WD
        return when {
            input.model.segment == Segment.PICKUP && input.drivetrain == Drivetrain.FOUR_WD -> 1.06
            input.model.segment == Segment.PICKUP && !fourWheel -> 0.97
            fourWheel -> 1.03
            else -> 1.0
        }
    }

    private fun transmissionFactor(input: VehicleInput): Double = when {
        input.transmission == Transmission.AUTOMATIC -> 1.0
        input.model.segment == Segment.SPORTS -> 1.04
        else -> 0.96
    }

    private fun regionFactor(input: VehicleInput): Double {
        val segment = input.model.segment
        val region = input.region
        var factor = when (segment) {
            Segment.PICKUP, Segment.FULLSIZE_SUV -> when (region) {
                Region.SOUTHWEST, Region.MOUNTAIN -> 1.04
                Region.MIDWEST -> 1.03
                Region.SOUTHEAST -> 1.02
                Region.PACIFIC -> 0.98
                Region.NORTHEAST -> 1.0
            }
            Segment.ELECTRIC -> when (region) {
                Region.PACIFIC -> 1.04
                Region.NORTHEAST -> 1.0
                Region.SOUTHWEST -> 0.99
                Region.SOUTHEAST, Region.MOUNTAIN -> 0.98
                Region.MIDWEST -> 0.96
            }
            Segment.SPORTS -> when (region) {
                Region.PACIFIC, Region.SOUTHWEST -> 1.03
                Region.NORTHEAST, Region.MIDWEST -> 0.98
                else -> 1.0
            }
            else -> when (region) {
                Region.PACIFIC -> 1.02
                Region.NORTHEAST -> 1.01
                else -> 1.0
            }
        }
        val fourWheel = input.drivetrain == Drivetrain.AWD || input.drivetrain == Drivetrain.FOUR_WD
        if (fourWheel && (region == Region.NORTHEAST || region == Region.MOUNTAIN || region == Region.MIDWEST)) {
            factor *= 1.02
        }
        return factor
    }

    private fun spread(age: Int, input: VehicleInput): Double {
        var spread = when {
            age <= 8 -> 0.05
            age <= 15 -> 0.07
            else -> 0.10
        }
        if (input.titleStatus != TitleStatus.CLEAN) spread += 0.05
        if (input.condition == Condition.POOR || WearItem.MECHANICAL in input.wearItems) spread += 0.03
        return spread
    }

    private fun range(mid: Int, spread: Double) =
        ValueRange(low = roundTo(mid * (1 - spread)), mid = mid, high = roundTo(mid * (1 + spread)))

    private fun roundTo(value: Double, step: Int = 50): Int = ((value / step).roundToInt() * step)
    private fun roundTo(value: Int): Int = roundTo(value.toDouble())

    companion object {
        private const val INFLATION = 1.01
        private const val INITIAL_RETENTION = 0.94
        private const val FLOOR_FRACTION = 0.08
        private const val MIN_VALUE = 1_000.0
        private const val TRADE_IN_RATIO = 0.84
        private const val RETAIL_RATIO = 1.12
        private const val RETAIL_PREP = 500.0
    }
}
