/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package dev.ligature.gaze

import dev.ligature.gaze.Gaze

trait NoMatch
object NoMatch extends NoMatch

def takeString(toMatch: String): (gaze: Gaze[Char]) => Either[NoMatch, String] = {
    //    let graphemes = to_match.graphemes(true).collect::<Vec<&str>>();
    val chars = toMatch.toVector
    return (gaze) => {
        var offset = 0
        var matched = true
        while (matched && offset < chars.length) {
            val nextChar = gaze.next()
            nextChar match {
                case Some(c) => {
                    if (chars(offset) == c) {
                        offset += 1;
                    } else {
                        matched = false
                    }
                }
                case None => {
                    matched = false
                }
            }
        }
        if (matched) {
            Right(toMatch)
        } else {
            Left(NoMatch)
        }
    }
}

//  pub fn ignore_all<'a>(
//      to_match: Vec<&'a str>, //TODO maybe make this an array instead of Vec
//  ) -> impl Fn(&mut Gaze<&'a str>) -> Result<(), NoMatch> {
//      move |gaze: &mut Gaze<&'a str>| -> Result<(), NoMatch> {
//          while !gaze.is_complete() {
//              let peek = gaze.peek();
//              match peek {
//                  Some(peek) => {
//                      if to_match.contains(&peek) {
//                          gaze.next();
//                      } else {
//                          return Ok(());
//                      }
//                  }
//                  None => return Ok(()),
//              }
//          }
//          Ok(())
//      }
//  }

def takeWhile(matcher: (toMatch: Char) => Boolean): (gaze: Gaze[Char]) => Either[NoMatch, String] = {
    return (gaze: Gaze[Char]) => {
        val res = StringBuilder()
        var matched = true
        var continue = true
        while(continue) {
            val peek = gaze.peek();

            peek match {
                case Some(c) => {
                    if (matcher(c)) {
                        gaze.next();
                        res += c;
                    } else if (res.length == 0) {
                        matched = false
                        continue = false
                    } else {
                        continue = false
                        //return Right(res);
                    }
                }
                case None => {
                    if (res.length == 0) {
                        matched = false
                        continue = false
                    } else {
                        continue = false
                        //return Right(res);
                    }
                }
            }
        }
        if (matched) {
            Right(res.toString())
        } else {
            Left(NoMatch)
        }
    }
}

def takeCharacters(chars: Char*): (gaze: Gaze[Char]) => Either[NoMatch, String] = takeWhile { chars.contains(_) }

// /// A Step that takes values from the String until the predicate passes.
// pub struct TakeUntil<'a>(pub &'a dyn Fn(&str) -> bool);

// impl Tokenizer<String> for TakeUntil<'_> {
//     fn attempt(&self, gaze: &mut Gaze) -> Result<String, GazeErr> {
//         //TODO this will need to be rewritten once handle Unicode better
//         //TODO also this should share code with TakeWhile
//         let mut res = String::new();
//         loop {
//             let next_value = gaze.peek();
//             match next_value {
//                 None => {
//                     return Ok(res);
//                 }
//                 Some(c) => {
//                     if !self.0(c) {
//                         res += c;
//                         gaze.next();
//                     } else {
//                         return Ok(res);
//                     }
//                 }
//             }
//         }
//     }
// }

// pub struct TakeFirst<'a, T>(pub Box<[&'a dyn Tokenizer<T>]>);

// impl<T> Tokenizer<T> for TakeFirst<'_, T> {
//     fn attempt(&self, gaze: &mut Gaze) -> Result<T, GazeErr> {
//         for step in &*self.0 {
//             let res = gaze.run(*step);
//             match res {
//                 Ok(_) => return res,
//                 Err(_) => continue,
//             }
//         }
//         Err(GazeErr::NoMatch)
//     }
// }

// pub struct TakeAll<'a, T>(pub Box<[&'a dyn Tokenizer<T>]>);

// impl<T> Tokenizer<Box<[T]>> for TakeAll<'_, T> {
//     fn attempt(&self, gaze: &mut Gaze) -> Result<Box<[T]>, GazeErr> {
//         let mut res: Vec<T> = Vec::new();
//         for step in &*self.0 {
//             let r = gaze.run(*step);
//             match r {
//                 Ok(r) => res.push(r),
//                 Err(_) => return Err(GazeErr::NoMatch),
//             }
//         }
//         Ok(res.into_boxed_slice())
//     }
// }