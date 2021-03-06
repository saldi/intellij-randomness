package com.fwdekker.randomness.word

import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe


/**
 * Unit tests for [WordSettings].
 */
object WordSettingsTest : Spek({
    lateinit var wordSettings: WordSettings


    beforeEachTest {
        wordSettings = WordSettings()
    }


    describe("state management") {
        it("creates an independent copy") {
            val copy = wordSettings.deepCopy()
            wordSettings.currentScheme.minLength = 156
            copy.currentScheme.minLength = 37

            assertThat(wordSettings.currentScheme.minLength).isEqualTo(156)
        }

        it("copies state from another instance") {
            wordSettings.currentScheme.minLength = 502
            wordSettings.currentScheme.maxLength = 812
            wordSettings.currentScheme.enclosure = "QJ8S4UrFaa"

            val newWordSettings = WordSettings()
            newWordSettings.loadState(wordSettings.state)

            assertThat(newWordSettings.currentScheme.minLength).isEqualTo(502)
            assertThat(newWordSettings.currentScheme.maxLength).isEqualTo(812)
            assertThat(newWordSettings.currentScheme.enclosure).isEqualTo("QJ8S4UrFaa")
        }
    }

    describe("copying") {
        describe("copyFrom") {
            it("makes the two schemes equal") {
                val schemeA = WordScheme()
                val schemeB = WordScheme(myName = "Name")
                assertThat(schemeA).isNotEqualTo(schemeB)

                schemeA.copyFrom(schemeB)

                assertThat(schemeA).isEqualTo(schemeB)
            }
        }

        describe("copyAs") {
            it("makes two schemes equal except for the name") {
                val schemeA = WordScheme()
                val schemeB = schemeA.copyAs("NewName")
                assertThat(schemeA).isNotEqualTo(schemeB)

                schemeB.myName = schemeA.myName

                assertThat(schemeA).isEqualTo(schemeB)
            }
        }
    }
})
