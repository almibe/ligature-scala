/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.repl

import javax.script.ScriptEngineManager

val exitTask = Task("exit", "Exit REPL") { args ->
  if (args.isEmpty()) {
    ReplResult.ExitRepl("Exiting...")
  } else {
    ReplResult.Text(":exit takes no arguments.")
  }
}

fun createKtsMode(): Mode {
  val engine = ScriptEngineManager().getEngineByExtension("kts")!!

  return Mode(
    "kts",
    "kts",
    "Run Kotlin code",
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
