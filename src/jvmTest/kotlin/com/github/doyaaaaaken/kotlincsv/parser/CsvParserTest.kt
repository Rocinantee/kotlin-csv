package com.github.doyaaaaaken.kotlincsv.parser

import com.github.doyaaaaaken.kotlincsv.util.MalformedCSVException
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.WordSpec

class CsvParserTest : WordSpec() {
    init {
        val parser = CsvParser('"', ',', '"')
        val lineTerminators = listOf("\n", "\u2028", "\u2029", "\u0085", "\r", "\r\n")

        "CsvParser.parseRow" should {
            "parseEmptyRow" {
                parser.parseRow("") shouldBe emptyList()
            }
            "return null if line is on the way of csv row" {
                parser.parseRow("a,\"b") shouldBe null
            }
        }

        "ParseStateMachine logic" should {
            "parse delimiter at the start of row" {
                parser.parseRow(",a") shouldBe listOf("", "a")
            }
            "parse line terminator at the start of row" {
                lineTerminators.forEach { lt ->
                    parser.parseRow(lt) shouldBe listOf("")
                }
            }
            "parse row with delimiter at the end" {
                parser.parseRow("a,") shouldBe listOf("a", "")
            }
            "parse line terminator after quote end" {
                lineTerminators.forEach { lt ->
                    parser.parseRow("""a,"b"$lt""") shouldBe listOf("a", "b")
                }
            }
            "parse line terminator after delimiter" {
                lineTerminators.forEach { lt ->
                    parser.parseRow("a,$lt") shouldBe listOf("a", "")
                }
            }
            "parse line terminator after field" {
                lineTerminators.forEach { lt ->
                    parser.parseRow("a$lt") shouldBe listOf("a")
                }
            }
            "parse escape character after field" {
                parser.parseRow("a\"\"") shouldBe listOf("a\"")
            }
            "throw exception when parsing 2 rows" {
                lineTerminators.forEach { lt ->
                    shouldThrow<MalformedCSVException> {
                        parser.parseRow("a${lt}b")
                    }
                }
            }
        }
    }
}