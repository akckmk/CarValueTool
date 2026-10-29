package com.carvaluetool.core

import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun formatCurrency(amount: Int): String =
    NumberFormat.getCurrencyInstance(Locale.US).apply { maximumFractionDigits = 0 }.format(amount)

fun formatSignedCurrency(amount: Int): String =
    (if (amount >= 0) "+" else "-") + formatCurrency(kotlin.math.abs(amount))

fun formatNumber(value: Int): String = NumberFormat.getIntegerInstance(Locale.US).format(value)

fun ValueRange.format(): String = "${formatCurrency(low)} – ${formatCurrency(high)}"

data class ReportSection(val title: String, val rows: List<Pair<String, String>>)

/** Builds a printable, self-contained report from a valuation result. */
class ValuationReport(val result: ValuationResult, val generatedAt: LocalDateTime) {

    val title: String
        get() = with(result.input) { "$year ${make.name} ${model.name}" }

    val generatedLabel: String
        get() = generatedAt.format(DateTimeFormatter.ofPattern("MMMM d, yyyy 'at' h:mm a", Locale.US))

    /** Suggested filename without extension, e.g. "CarValue_2019_Honda_Civic_20261029". */
    val suggestedFileName: String
        get() {
            val raw = with(result.input) { "CarValue_${year}_${make.name}_${model.name}" }
            val date = generatedAt.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
            return raw.replace(Regex("[^A-Za-z0-9_-]+"), "_") + "_" + date
        }

    val sections: List<ReportSection>
        get() {
            val input = result.input
            return listOf(
                ReportSection("Estimated values", listOf(
                    "Trade-in" to "${formatCurrency(result.tradeIn.mid)}  (${result.tradeIn.format()})",
                    "Private party" to "${formatCurrency(result.privateParty.mid)}  (${result.privateParty.format()})",
                    "Dealer retail" to "${formatCurrency(result.dealerRetail.mid)}  (${result.dealerRetail.format()})",
                    "Confidence" to result.confidence,
                )),
                ReportSection("Vehicle details", listOf(
                    "Year" to input.year.toString(),
                    "Make" to input.make.name,
                    "Model" to input.model.name,
                    "Trim level" to input.trim.label,
                    "Segment" to input.model.segment.label,
                    "Mileage" to "${formatNumber(input.mileage)} mi (expected ~${formatNumber(result.expectedMileage)} mi)",
                    "Condition" to input.condition.label,
                    "Drivetrain" to input.drivetrain.label,
                    "Transmission" to input.transmission.label,
                    "Exterior color" to input.color.label,
                    "Region" to input.region.label,
                )),
                ReportSection("History", listOf(
                    "Accidents" to input.accidentHistory.label,
                    "Title" to input.titleStatus.label,
                    "Owners" to input.owners.label,
                    "Service records" to input.serviceRecords.label,
                )),
                ReportSection(
                    "Reported wear & tear",
                    if (input.wearItems.isEmpty()) listOf("None reported" to "")
                    else WearItem.entries.filter { it in input.wearItems }.map { it.label to "" },
                ),
                ReportSection(
                    "Value breakdown (private party)",
                    listOf(
                        "Estimated original MSRP" to formatCurrency(result.estimatedOriginalMsrp),
                        "Depreciated base value" to formatCurrency(result.baseValue),
                    ) + result.adjustments.map { it.label to formatSignedCurrency(it.amount) } +
                        listOf("Private party value" to formatCurrency(result.privateParty.mid)),
                ),
            )
        }

    fun toPlainText(): String = buildString {
        appendLine("CAR VALUE REPORT")
        appendLine(title)
        appendLine("Generated $generatedLabel")
        appendLine("=".repeat(48))
        sections.forEach { section ->
            appendLine()
            appendLine(section.title.uppercase())
            appendLine("-".repeat(48))
            section.rows.forEach { (label, value) ->
                if (value.isEmpty()) appendLine("• $label") else appendLine("$label: $value")
            }
        }
        appendLine()
        appendLine(DISCLAIMER)
    }

    companion object {
        const val DISCLAIMER =
            "Disclaimer: Values are estimates produced offline by Car Value Tool's built-in " +
                "depreciation model. They are not live market quotes and are not affiliated with " +
                "Kelley Blue Book or any other pricing service. Actual offers vary by local supply, " +
                "demand, options and inspection."
    }
}
