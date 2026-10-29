package com.carvaluetool.app

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.carvaluetool.core.AccidentHistory
import com.carvaluetool.core.ColorCategory
import com.carvaluetool.core.Condition
import com.carvaluetool.core.Drivetrain
import com.carvaluetool.core.Make
import com.carvaluetool.core.ModelSpec
import com.carvaluetool.core.Owners
import com.carvaluetool.core.Region
import com.carvaluetool.core.Segment
import com.carvaluetool.core.ServiceRecords
import com.carvaluetool.core.TitleStatus
import com.carvaluetool.core.Transmission
import com.carvaluetool.core.Trim
import com.carvaluetool.core.ValuationEngine
import com.carvaluetool.core.ValuationReport
import com.carvaluetool.core.VehicleCatalog
import com.carvaluetool.core.VehicleInput
import com.carvaluetool.core.WearItem
import java.time.LocalDateTime
import java.time.Year

data class FormState(
    val year: Int? = null,
    val make: Make? = null,
    val model: ModelSpec? = null,
    val trim: Trim = Trim.MID,
    val mileageText: String = "",
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
) {
    val mileage: Int? get() = mileageText.toIntOrNull()
    val isComplete: Boolean get() = year != null && make != null && model != null && mileage != null
}

/**
 * Holds the form and the latest result in memory only. Nothing is written to disk:
 * when the app process ends, all entered data is gone.
 */
class ValuationViewModel : ViewModel() {

    val currentYear: Int = Year.now().value
    private val engine = ValuationEngine(currentYear)

    var form by mutableStateOf(FormState())
        private set

    var report by mutableStateOf<ValuationReport?>(null)
        private set

    val years: List<Int> = VehicleCatalog.years(currentYear)

    val makes: List<Make> get() = form.year?.let(VehicleCatalog::makesFor) ?: VehicleCatalog.makes

    val models: List<ModelSpec>
        get() {
            val make = form.make ?: return emptyList()
            return form.year?.let { VehicleCatalog.modelsFor(make, it) } ?: make.models.distinctBy { it.name }
        }

    fun selectYear(year: Int) {
        val make = form.make?.takeIf { m -> m.models.any { it.isAvailable(year) } }
        val model = make?.let { VehicleCatalog.modelsFor(it, year).firstOrNull { m -> m.name == form.model?.name } }
        form = form.copy(year = year, make = make, model = model)
    }

    fun selectMake(make: Make) {
        form = form.copy(make = make, model = null)
    }

    fun selectModel(model: ModelSpec) {
        // Default the drivetrain to what's most common for the segment; the user can change it.
        val drivetrain = when (model.segment) {
            Segment.PICKUP -> Drivetrain.FOUR_WD
            Segment.SPORTS -> Drivetrain.RWD
            else -> form.drivetrain
        }
        form = form.copy(model = model, drivetrain = drivetrain)
    }

    fun updateMileage(text: String) {
        val digits = text.filter(Char::isDigit).take(7)
        form = form.copy(mileageText = digits.toIntOrNull()?.toString() ?: "")
    }

    fun update(transform: (FormState) -> FormState) {
        form = transform(form)
    }

    fun toggleWear(item: WearItem) {
        val items = form.wearItems
        form = form.copy(wearItems = if (item in items) items - item else items + item)
    }

    fun calculate() {
        val f = form
        if (!f.isComplete) return
        val input = VehicleInput(
            year = f.year!!,
            make = f.make!!,
            model = f.model!!,
            trim = f.trim,
            mileage = f.mileage!!,
            condition = f.condition,
            drivetrain = f.drivetrain,
            transmission = f.transmission,
            accidentHistory = f.accidentHistory,
            titleStatus = f.titleStatus,
            owners = f.owners,
            serviceRecords = f.serviceRecords,
            color = f.color,
            region = f.region,
            wearItems = f.wearItems,
        )
        report = ValuationReport(engine.appraise(input), LocalDateTime.now())
    }

    fun backToForm() {
        report = null
    }

    fun startOver() {
        report = null
        form = FormState()
    }
}
