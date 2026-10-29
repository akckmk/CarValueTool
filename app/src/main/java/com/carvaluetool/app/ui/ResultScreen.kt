package com.carvaluetool.app.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.carvaluetool.app.ExportFormat
import com.carvaluetool.app.ReportExporter
import com.carvaluetool.core.ValuationReport
import com.carvaluetool.core.ValueRange
import com.carvaluetool.core.format
import com.carvaluetool.core.formatCurrency
import com.carvaluetool.core.formatNumber
import com.carvaluetool.core.formatSignedCurrency
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(report: ValuationReport, onBack: () -> Unit, onStartOver: () -> Unit) {
    val result = report.result
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }

    fun save(uri: Uri?, format: ExportFormat) {
        if (uri == null) return
        scope.launch {
            val message = try {
                withContext(Dispatchers.IO) { ReportExporter.export(context, uri, report, format) }
                "${format.label} report saved"
            } catch (e: Exception) {
                "Could not save report: ${e.message ?: "unknown error"}"
            }
            snackbar.showSnackbar(message)
        }
    }

    val pdfLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument(ExportFormat.PDF.mimeType)) {
        save(it, ExportFormat.PDF)
    }
    val textLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument(ExportFormat.TEXT.mimeType)) {
        save(it, ExportFormat.TEXT)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your car's value") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Edit details")
                    }
                },
                actions = { TextButton(onClick = onStartOver) { Text("New") } },
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column {
                Text(report.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(
                    "${result.input.trim.label} · ${formatNumber(result.input.mileage)} mi · ${result.input.condition.label} condition",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            ValueCard(
                title = "Private party value",
                subtitle = "What you could expect selling to another individual",
                range = result.privateParty,
                container = MaterialTheme.colorScheme.primaryContainer,
                content = MaterialTheme.colorScheme.onPrimaryContainer,
                large = true,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ValueCard(
                    title = "Trade-in",
                    subtitle = "Typical dealer offer",
                    range = result.tradeIn,
                    container = MaterialTheme.colorScheme.secondaryContainer,
                    content = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.weight(1f),
                )
                ValueCard(
                    title = "Dealer retail",
                    subtitle = "Typical lot asking price",
                    range = result.dealerRetail,
                    container = MaterialTheme.colorScheme.tertiaryContainer,
                    content = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.weight(1f),
                )
            }
            Text(
                "Estimate confidence: ${result.confidence}",
                style = MaterialTheme.typography.labelLarge,
            )

            Section("How we got this number") {
                BreakdownRow("Estimated original MSRP", formatCurrency(result.estimatedOriginalMsrp))
                BreakdownRow("After depreciation (${result.input.model.segment.label})", formatCurrency(result.baseValue))
                HorizontalDivider()
                result.adjustments.forEach { BreakdownRow(it.label, formatSignedCurrency(it.amount), highlight = it.amount < 0) }
                HorizontalDivider()
                BreakdownRow("Private party value", formatCurrency(result.privateParty.mid), bold = true)
            }

            Section("Export report") {
                Text(
                    "Choose where to save a copy on your device. The app itself keeps nothing.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = {
                            pdfLauncher.launch("${report.suggestedFileName}.${ExportFormat.PDF.extension}")
                        },
                        modifier = Modifier.weight(1f),
                    ) { Text("Save PDF") }
                    OutlinedButton(
                        onClick = {
                            textLauncher.launch("${report.suggestedFileName}.${ExportFormat.TEXT.extension}")
                        },
                        modifier = Modifier.weight(1f),
                    ) { Text("Save text") }
                }
            }

            Text(
                ValuationReport.DISCLAIMER,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ValueCard(
    title: String,
    subtitle: String,
    range: ValueRange,
    container: Color,
    content: Color,
    modifier: Modifier = Modifier,
    large: Boolean = false,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = container, contentColor = content),
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = if (large) Alignment.CenterHorizontally else Alignment.Start,
        ) {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Text(
                formatCurrency(range.mid),
                style = if (large) MaterialTheme.typography.displaySmall else MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(range.format(), style = MaterialTheme.typography.bodySmall)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                textAlign = if (large) TextAlign.Center else TextAlign.Start,
            )
        }
    }
}

@Composable
private fun BreakdownRow(label: String, value: String, highlight: Boolean = false, bold: Boolean = false) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp),
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.SemiBold,
            color = if (highlight) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
        )
    }
}
