package com.carvaluetool.app

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import com.carvaluetool.core.ValuationReport
import java.io.IOException
import java.io.OutputStream

enum class ExportFormat(val mimeType: String, val extension: String, val label: String) {
    PDF("application/pdf", "pdf", "PDF"),
    TEXT("text/plain", "txt", "Text"),
}

/**
 * Writes a report to a document the user picked with the system "Save as" dialog.
 * The app never keeps its own copy.
 */
object ReportExporter {

    @Throws(IOException::class)
    fun export(context: Context, uri: Uri, report: ValuationReport, format: ExportFormat) {
        val stream = context.contentResolver.openOutputStream(uri, "wt")
            ?: throw IOException("Unable to open the selected file")
        stream.use {
            when (format) {
                ExportFormat.PDF -> writePdf(report, it)
                ExportFormat.TEXT -> it.write(report.toPlainText().toByteArray(Charsets.UTF_8))
            }
        }
    }

    private const val PAGE_WIDTH = 612 // US Letter at 72 dpi
    private const val PAGE_HEIGHT = 792
    private const val MARGIN = 48f

    private fun writePdf(report: ValuationReport, out: OutputStream) {
        val document = PdfDocument()
        val writer = PdfWriter(document)

        writer.text("Car Value Report", size = 22f, bold = true, color = ACCENT)
        writer.text(report.title, size = 16f, bold = true)
        writer.text("Generated ${report.generatedLabel}", size = 10f, color = MUTED)
        writer.space(8f)

        report.sections.forEach { section ->
            writer.space(10f)
            writer.text(section.title, size = 13f, bold = true, color = ACCENT)
            writer.rule()
            section.rows.forEach { (label, value) ->
                if (value.isEmpty()) writer.text("•  $label", size = 10.5f) else writer.row(label, value)
            }
        }

        writer.space(16f)
        writer.text(ValuationReport.DISCLAIMER, size = 8.5f, color = MUTED)
        writer.finish()

        document.writeTo(out)
        document.close()
    }

    private val ACCENT = Color.rgb(11, 79, 138)
    private val MUTED = Color.rgb(100, 100, 100)

    /** Minimal flowing-text layout with automatic page breaks. */
    private class PdfWriter(private val document: PdfDocument) {
        private var pageNumber = 0
        private var page: PdfDocument.Page? = null
        private lateinit var canvas: Canvas
        private var y = 0f
        private val contentWidth = PAGE_WIDTH - 2 * MARGIN

        init {
            newPage()
        }

        private fun newPage() {
            page?.let(document::finishPage)
            pageNumber++
            val info = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
            page = document.startPage(info).also { canvas = it.canvas }
            y = MARGIN
        }

        private fun ensureSpace(height: Float) {
            if (y + height > PAGE_HEIGHT - MARGIN) newPage()
        }

        private fun paint(size: Float, bold: Boolean, color: Int) = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = size
            this.color = color
            typeface = Typeface.create(Typeface.SANS_SERIF, if (bold) Typeface.BOLD else Typeface.NORMAL)
        }

        fun text(content: String, size: Float, bold: Boolean = false, color: Int = Color.BLACK) {
            val p = paint(size, bold, color)
            wrap(content, p, contentWidth).forEach { line ->
                val lineHeight = size * 1.35f
                ensureSpace(lineHeight)
                y += size
                canvas.drawText(line, MARGIN, y, p)
                y += lineHeight - size
            }
        }

        fun row(label: String, value: String) {
            val size = 10.5f
            val labelPaint = paint(size, bold = false, color = MUTED)
            val valuePaint = paint(size, bold = true, color = Color.BLACK)
            val labelWidth = contentWidth * 0.45f
            val labelLines = wrap(label, labelPaint, labelWidth - 8f)
            val valueLines = wrap(value, valuePaint, contentWidth - labelWidth)
            val lineHeight = size * 1.4f
            val lines = maxOf(labelLines.size, valueLines.size)
            ensureSpace(lines * lineHeight)
            for (i in 0 until lines) {
                val baseline = y + size + i * lineHeight
                labelLines.getOrNull(i)?.let { canvas.drawText(it, MARGIN, baseline, labelPaint) }
                valueLines.getOrNull(i)?.let { canvas.drawText(it, MARGIN + labelWidth, baseline, valuePaint) }
            }
            y += lines * lineHeight
        }

        fun rule() {
            ensureSpace(8f)
            y += 3f
            canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, paint(1f, false, Color.LTGRAY).apply { strokeWidth = 1f })
            y += 5f
        }

        fun space(height: Float) {
            y += height
        }

        fun finish() {
            page?.let(document::finishPage)
            page = null
        }

        private fun wrap(text: String, paint: Paint, width: Float): List<String> {
            if (text.isEmpty()) return listOf("")
            val lines = mutableListOf<String>()
            var current = StringBuilder()
            text.split(' ').forEach { word ->
                val candidate = if (current.isEmpty()) word else "$current $word"
                if (paint.measureText(candidate) <= width || current.isEmpty()) {
                    current = StringBuilder(candidate)
                } else {
                    lines += current.toString()
                    current = StringBuilder(word)
                }
            }
            if (current.isNotEmpty()) lines += current.toString()
            return lines
        }
    }
}
