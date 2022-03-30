/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package dev.ligature.repl

import org.jline.terminal.Attributes
import org.jline.terminal.TerminalBuilder
import org.jline.reader.*
import org.jline.utils.OSUtils;

@main def start() = {
  val terminal = TerminalBuilder.builder()
      .system(true)
      .build()  
  val reader = LineReaderBuilder.builder()
      .terminal(terminal)
//      .completer(systemRegistry.completer())
//      .parser(parser)
//      .highlighter(highlighter)
      // .variable(LineReader.SECONDARY_PROMPT_PATTERN, "%M%P > ")
      // .variable(LineReader.INDENTATION, 2)
      // .variable(LineReader.LIST_MAX, 100)
      // .variable(LineReader.HISTORY_FILE, Paths.get(root, "history"))
      // .option(Option.INSERT_BRACKET, true)
      // .option(Option.EMPTY_WORD_OPTIONS, false)
      // .option(Option.USE_FORWARD_SLASH, true)             // use forward slash in directory separator
      // .option(Option.DISABLE_EVENT_EXPANSION, true)
      .build()
    if (OSUtils.IS_WINDOWS) {
      reader.setVariable(LineReader.BLINK_MATCHING_PAREN, 0); // if enabled cursor remains in begin parenthesis (gitbash)
    }

//    LineReader reader = LineReaderBuilder.builder().build();
    val prompt = ">"
    var continue = true
    while (continue) {
        var line: String = null
        // try {
            line = reader.readLine(prompt);
            if (line.trim == ":exit") {
              continue = false
            }
        // } catch (e: UserInterruptException) {
            // Ignore
        // } catch (e: EndOfFileException) {
            // return;
        // }
    }
}
