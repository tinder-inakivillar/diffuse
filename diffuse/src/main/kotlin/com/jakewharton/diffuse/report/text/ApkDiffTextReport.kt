package com.jakewharton.diffuse.report.text

import com.jakewharton.diffuse.ArchiveFile.Type
import com.jakewharton.diffuse.diff.ApkDiff
import com.jakewharton.diffuse.diff.lint.Notice
import com.jakewharton.diffuse.diff.toDetailReport
import com.jakewharton.diffuse.diff.toSummaryTable
import com.jakewharton.diffuse.diffuseTable
import com.jakewharton.diffuse.report.DiffReport
import com.jakewharton.diffuse.report.toSummaryString

internal class ApkDiffTextReport(private val apkDiff: ApkDiff) : DiffReport {
  override fun write(appendable: Appendable) {
    appendable.apply {
      appendln("Diffuse - Tinder Edition")
      appendln()
      append("OLD: ")
      append(apkDiff.oldApk.filename)
      append(" (signature: ")
      append(apkDiff.oldApk.signatures.toSummaryString())
      appendln(')')

      append("NEW: ")
      append(apkDiff.newApk.filename)
      append(" (signature: ")
      append(apkDiff.newApk.signatures.toSummaryString())
      appendln(')')

      appendln()
      if (apkDiff.lintMessages.isNotEmpty()) {
        appendln(diffuseTable {
          header {
            row("NOTICES")
          }
          body {
            apkDiff.lintMessages.sorted().forEach { notice ->
              row(buildString {
                append(when (notice.type) {
                  Notice.Type.Informational -> 'i'
                  Notice.Type.Warning -> '!'
                  Notice.Type.Resolution -> '✓'
                })
                append("  ")
                append(notice.message)
              })
            }
          }
        }.toString())
        appendln()
      }
      appendln(apkDiff.archive.toSummaryTable("APK", Type.APK_TYPES,
          skipIfEmptyTypes = setOf(Type.Native)))
      appendln()
      appendln(apkDiff.dex.toSummaryTable())
      appendln()
      appendln(apkDiff.arsc.toSummaryTable())
      if (apkDiff.archive.changed || apkDiff.signatures.changed) {
        appendln()
        appendln("=================")
        appendln("====   APK   ====")
        appendln("=================")
        if (apkDiff.archive.changed) {
          appendln(apkDiff.archive.toDetailReport())
        }
        if (apkDiff.signatures.changed) {
          appendln(apkDiff.signatures.toDetailReport())
        }
      }
      if (apkDiff.manifest.changed) {
        appendln()
        appendln("======================")
        appendln("====   MANIFEST   ====")
        appendln("======================")
        appendln(apkDiff.manifest.toDetailReport())
      }
      if (apkDiff.dex.changed) {
        appendln()
        appendln("=================")
        appendln("====   DEX   ====")
        appendln("=================")
        appendln(apkDiff.dex.toDetailReport())
      }
      if (apkDiff.arsc.changed) {
        appendln()
        appendln("==================")
        appendln("====   ARSC   ====")
        appendln("==================")
        appendln(apkDiff.arsc.toDetailReport())
      }
    }
  }

  override fun toString() = buildString { write(this) }
}
