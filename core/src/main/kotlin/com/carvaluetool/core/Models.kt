package com.carvaluetool.core

/** Vehicle body/market segment. Drives the base depreciation rate and expected annual mileage. */
enum class Segment(
    val label: String,
    /** Typical yearly depreciation once past the steep first year. */
    val annualDepreciation: Double,
    val expectedMilesPerYear: Int,
) {
    ECONOMY("Economy / Compact", 0.085, 12_000),
    MIDSIZE("Midsize Sedan", 0.09, 12_000),
    FULLSIZE("Full-size Sedan", 0.1, 12_000),
    LUXURY("Luxury", 0.115, 11_000),
    SPORTS("Sports / Performance", 0.07, 8_000),
    COMPACT_SUV("Compact SUV / Crossover", 0.08, 12_500),
    MIDSIZE_SUV("Midsize SUV", 0.08, 13_000),
    FULLSIZE_SUV("Full-size SUV", 0.085, 13_500),
    LUXURY_SUV("Luxury SUV", 0.11, 12_000),
    PICKUP("Pickup Truck", 0.07, 14_000),
    MINIVAN("Minivan", 0.09, 13_000),
    ELECTRIC("Electric Vehicle", 0.125, 11_000),
}

enum class Condition(val label: String, val description: String, val factor: Double) {
    EXCELLENT("Excellent", "Looks new, no reconditioning needed, complete service history.", 1.05),
    VERY_GOOD("Very Good", "Minor cosmetic defects only, mechanically sound.", 1.00),
    GOOD("Good", "Some repairable cosmetic defects, clean interior, no major issues.", 0.94),
    FAIR("Fair", "Noticeable wear, may need mechanical or cosmetic repairs.", 0.84),
    POOR("Poor", "Severe wear or major mechanical problems.", 0.68),
}

enum class Trim(val label: String, val factor: Double) {
    BASE("Base", 0.92),
    MID("Mid-level", 1.00),
    UPPER("Upper / Premium", 1.12),
    TOP("Top / Performance", 1.25),
}

enum class Drivetrain(val label: String) {
    FWD("Front-wheel drive"),
    RWD("Rear-wheel drive"),
    AWD("All-wheel drive"),
    FOUR_WD("4x4 / Four-wheel drive"),
}

enum class Transmission(val label: String) {
    AUTOMATIC("Automatic"),
    MANUAL("Manual"),
}

enum class AccidentHistory(val label: String, val factor: Double) {
    NONE("No accidents reported", 1.00),
    MINOR("Minor (cosmetic only)", 0.95),
    MODERATE("Moderate (structural repair)", 0.88),
    SEVERE("Severe / airbags deployed", 0.78),
}

enum class TitleStatus(val label: String, val factor: Double) {
    CLEAN("Clean", 1.00),
    REBUILT("Rebuilt / Reconstructed", 0.70),
    LEMON("Lemon buyback", 0.65),
    SALVAGE("Salvage", 0.50),
}

enum class Owners(val label: String, val factor: Double) {
    ONE("1 owner", 1.02),
    TWO("2 owners", 1.00),
    THREE("3 owners", 0.97),
    FOUR_PLUS("4 or more owners", 0.94),
}

enum class ServiceRecords(val label: String, val factor: Double) {
    COMPLETE("Complete records", 1.03),
    PARTIAL("Some records", 1.00),
    NONE("No records", 0.96),
}

enum class ColorCategory(val label: String, val factor: Double) {
    NEUTRAL("White / Black / Gray / Silver", 1.00),
    COMMON("Blue / Red", 0.99),
    UNCOMMON("Other (green, brown, orange, etc.)", 0.97),
}

enum class Region(val label: String) {
    NORTHEAST("Northeast"),
    SOUTHEAST("Southeast"),
    MIDWEST("Midwest"),
    SOUTHWEST("Southwest"),
    MOUNTAIN("Mountain West"),
    PACIFIC("Pacific / West Coast"),
}

/**
 * Wear-and-tear items. Each item either deducts a percentage of value or a fixed
 * reconditioning cost a buyer would expect to pay to fix it.
 */
enum class WearItem(
    val label: String,
    val percentDeduction: Double = 0.0,
    val fixedCost: Int = 0,
) {
    MINOR_SCRATCHES("Minor scratches / scuffs", percentDeduction = 0.01),
    DENTS("Dents or dings", percentDeduction = 0.03),
    RUST("Visible rust", percentDeduction = 0.06),
    PAINT_FADE("Faded or peeling paint", percentDeduction = 0.03),
    WINDSHIELD("Cracked windshield", fixedCost = 350),
    INTERIOR_WEAR("Interior stains, rips or tears", percentDeduction = 0.03),
    SMOKE_ODOR("Smoke or pet odor", percentDeduction = 0.05),
    TIRES("Tires need replacement", fixedCost = 700),
    BRAKES("Brakes need service", fixedCost = 450),
    WARNING_LIGHT("Check-engine / warning light on", percentDeduction = 0.05),
    MECHANICAL("Known mechanical issue", percentDeduction = 0.10),
    ELECTRONICS("Electronics / A/C not working", fixedCost = 800),
}

data class ModelSpec(
    val name: String,
    val segment: Segment,
    /** Typical MSRP of a mid-level trim for a current model year, in today's dollars. */
    val baseMsrp: Int,
    val firstYear: Int,
    val lastYear: Int? = null,
) {
    fun isAvailable(year: Int): Boolean = year >= firstYear && (lastYear == null || year <= lastYear)
}

data class Make(
    val name: String,
    /**
     * Multiplier on the segment depreciation rate. Below 1.0 holds value better
     * than average (e.g. Toyota); above 1.0 depreciates faster.
     */
    val depreciationFactor: Double,
    val models: List<ModelSpec>,
)

data class VehicleInput(
    val year: Int,
    val make: Make,
    val model: ModelSpec,
    val trim: Trim = Trim.MID,
    val mileage: Int,
    val condition: Condition = Condition.VERY_GOOD,
    val drivetrain: Drivetrain = Drivetrain.FWD,
    val transmission: Transmission = Transmission.AUTOMATIC,
    val accidentHistory: AccidentHistory = AccidentHistory.NONE,
    val titleStatus: TitleStatus = TitleStatus.CLEAN,
    val owners: Owners = Owners.TWO,
    val serviceRecords: ServiceRecords = ServiceRecords.PARTIAL,
    val color: ColorCategory = ColorCategory.NEUTRAL,
    val region: Region = Region.MIDWEST,
    val wearItems: Set<WearItem> = emptySet(),
)

/** One line in the valuation breakdown. [amount] is the dollar impact (negative = deduction). */
data class Adjustment(val label: String, val amount: Int)

data class ValueRange(val low: Int, val mid: Int, val high: Int)

data class ValuationResult(
    val input: VehicleInput,
    val estimatedOriginalMsrp: Int,
    val baseValue: Int,
    val adjustments: List<Adjustment>,
    val tradeIn: ValueRange,
    val privateParty: ValueRange,
    val dealerRetail: ValueRange,
    val expectedMileage: Int,
    val confidence: String,
)
