/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.repl

import arrow.core.Either.Left
import arrow.core.Either.Right
import dev.ligature.Dataset
import dev.ligature.inmemory.InMemoryLigature
import kotlinx.coroutines.runBlocking
import javax.script.ScriptEngineManager

val exitTask = Task("exit", "Exit REPL", null) { args ->
  if (args.isEmpty()) {
    ReplResult.ExitRepl("Exiting...")
  } else {
    ReplResult.Text(":exit takes no arguments.")
  }
}

fun createInstanceTask(ligatureInstance: LigatureInstance) = Task(
  "instance",
  "Displays information on currently referenced Ligature instance.",
  null) {
    ReplResult.Text(ligatureInstance.displayName)
  }

fun createUseInMemoryTask(ligatureInstance: LigatureInstance) = Task(
  "use-in-memory",
  "Switches current Ligature instance to a newly created in-memory instance.",
  null) {
    ligatureInstance.displayName = "In-Memory"
    ligatureInstance.instance = InMemoryLigature()
    ReplResult.Text("Using in-memory instance.")
  }

fun createHelpTask(commands: List<Command>) = Task(
  "help",
  "Displays this message.",
  null) {
    val sb = StringBuilder()
    commands.forEach {
      sb.append("  - :${it.name}\t${it.description}\n")
      if (it.usage != null) {
        sb.append("\t${it.usage}\n")
      }
    }
    if (sb.isNotEmpty()) sb.deleteCharAt(sb.lastIndex)
    ReplResult.Text(sb.toString())
  }

fun createDatasetsTask(ligatureInstance: LigatureInstance) = Task(
  "datasets",
  "Show all of the Datasets in the current Ligature instance.",
  null) {
    val sb = StringBuilder()
    runBlocking {
      ligatureInstance.instance.allDatasets().collect {
        sb.append(" - ${it.name}\n")
      }
    }
    if (sb.isNotEmpty()) sb.deleteCharAt(sb.lastIndex)
    else sb.append("No Datasets in Instance.")
    ReplResult.Text(sb.toString())
  }

fun createAddDatasetTask(ligatureInstance: LigatureInstance) = Task(
  "add-dataset",
  "Creates a new Dataset in the current Ligature instance.",
  ":add-dataset {datasetName}") { args ->
  if (args.size == 1) {
    val sb = StringBuilder()
    runBlocking {
      when (val ds = Dataset.create(args.first())) {
        is Right -> {
          ligatureInstance.instance.createDataset(ds.value)
          sb.append("Added Dataset - ${args.first()}")
        }
        is Left  -> {
          sb.append("Invalid Dataset name - ${args.first()}")
        }
      }
    }
    ReplResult.Text(sb.toString())
  } else {
    ReplResult.ReplError("add-dataset task takes dataset name parameter.")
  }
}

fun createRemoveDatasetTask(ligatureInstance: LigatureInstance) = Task(
  "remove-dataset",
  "Removes a new Dataset in the current Ligature instance.",
  ":remove-dataset {datasetName}") { args ->
  if (args.size == 1) {
    val sb = StringBuilder()
    runBlocking {
      when (val ds = Dataset.create(args.first())) {
        is Right -> {
          ligatureInstance.instance.deleteDataset(ds.value)
          sb.append("Removed Dataset - ${args.first()}")
        }
        is Left  -> {
          sb.append("Invalid Dataset name - ${args.first()}")
        }
      }
    }
    ReplResult.Text(sb.toString())
  } else {
    ReplResult.ReplError("remove-dataset task takes dataset name parameter.")
  }
}

fun createKtsMode(): Mode {
  val engine = ScriptEngineManager().getEngineByExtension("kts")!!

  return Mode(
    "kts",
    "kts",
    "Run Kotlin code",
    null,
    { args: List<String> ->
      if (args.isEmpty()) {
        ModeSwitchResult.Success("Entering KTS Mode")
      } else {
        ModeSwitchResult.Failure("Kotlin mode takes no arguments.")
      }
    },
    { input: String -> ReplResult.Text(engine.eval(input)?.toString() ?: " -- no result") }
  )
}
