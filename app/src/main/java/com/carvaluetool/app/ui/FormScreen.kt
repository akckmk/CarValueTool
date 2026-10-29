package com.carvaluetool.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.carvaluetool.app.ValuationViewModel
import com.carvaluetool.core.AccidentHistory
import com.carvaluetool.core.ColorCategory
import com.carvaluetool.core.Condition
import com.carvaluetool.core.Drivetrain
import com.carvaluetool.core.Owners
import com.carvaluetool.core.Region
import com.carvaluetool.core.ServiceRecords
import com.carvaluetool.core.TitleStatus
import com.carvaluetool.core.Transmission
import com.carvaluetool.core.Trim
import com.carvaluetool.core.WearItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(vm: ValuationViewModel) {
    val form = vm.form
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Car Value Tool") },
                actions = { TextButton(onClick = vm::startOver) { Text("Clear") } },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                "Estimate the fair market value of a used car. Everything stays on this device " +
                    "and is erased when you close the app.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Section("Vehicle") {
                DropdownField("Year", vm.years, form.year, { it.toString() }, vm::selectYear)
                DropdownField("Make", vm.makes, form.make, { it.name }, vm::selectMake)
                DropdownField(
                    label = "Model",
                    options = vm.models,
                    selected = form.model,
                    optionLabel = { it.name },
                    onSelected = vm::selectModel,
                    enabled = form.make != null,
                )
                DropdownField("Trim level", Trim.entries, form.trim, { it.label }, { t -> vm.update { it.copy(trim = t) } })
                OutlinedTextField(
                    value = form.mileageText,
                    onValueChange = vm::updateMileage,
                    label = { Text("Mileage") },
                    suffix = { Text("mi") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                    modifier = Modifier.fillMaxWidth(),
                )
                DropdownField("Drivetrain", Drivetrain.entries, form.drivetrain, { it.label }, { d -> vm.update { it.copy(drivetrain = d) } })
                DropdownField("Transmission", Transmission.entries, form.transmission, { it.label }, { t -> vm.update { it.copy(transmission = t) } })
                DropdownField("Exterior color", ColorCategory.entries, form.color, { it.label }, { c -> vm.update { it.copy(color = c) } })
                DropdownField("Region", Region.entries, form.region, { it.label }, { r -> vm.update { it.copy(region = r) } })
            }

            Section("Overall condition") {
                Condition.entries.forEach { condition ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = form.condition == condition,
                                onClick = { vm.update { it.copy(condition = condition) } },
                                role = Role.RadioButton,
                            )
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(selected = form.condition == condition, onClick = null)
                        Column(Modifier.padding(start = 12.dp)) {
                            Text(condition.label, fontWeight = FontWeight.SemiBold)
                            Text(
                                condition.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }

            Section("Wear & tear") {
                Text(
                    "Check anything that applies.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                WearItem.entries.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .toggleable(
                                value = item in form.wearItems,
                                onValueChange = { vm.toggleWear(item) },
                                role = Role.Checkbox,
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Checkbox(checked = item in form.wearItems, onCheckedChange = null)
                        Text(item.label, modifier = Modifier.padding(start = 12.dp, top = 10.dp, bottom = 10.dp))
                    }
                }
            }

            Section("History") {
                DropdownField("Accident history", AccidentHistory.entries, form.accidentHistory, { it.label }, { a -> vm.update { it.copy(accidentHistory = a) } })
                DropdownField("Title status", TitleStatus.entries, form.titleStatus, { it.label }, { t -> vm.update { it.copy(titleStatus = t) } })
                DropdownField("Number of owners", Owners.entries, form.owners, { it.label }, { o -> vm.update { it.copy(owners = o) } })
                DropdownField("Service records", ServiceRecords.entries, form.serviceRecords, { it.label }, { s -> vm.update { it.copy(serviceRecords = s) } })
            }

            Button(
                onClick = vm::calculate,
                enabled = form.isComplete,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
            ) {
                Text("Get my car's value")
            }
            if (!form.isComplete) {
                Text(
                    "Select a year, make and model, and enter the mileage to continue.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
internal fun Section(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> DropdownField(
    label: String,
    options: List<T>,
    selected: T?,
    optionLabel: (T) -> String,
    onSelected: (T) -> Unit,
    enabled: Boolean = true,
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded && enabled,
        onExpandedChange = { if (enabled) expanded = it },
        modifier = Modifier.fillMaxWidth(),
    ) {
        OutlinedTextField(
            value = selected?.let(optionLabel) ?: "",
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            singleLine = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded && enabled) },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled)
                .fillMaxWidth(),
        )
        ExposedDropdownMenu(expanded = expanded && enabled, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(optionLabel(option)) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}
