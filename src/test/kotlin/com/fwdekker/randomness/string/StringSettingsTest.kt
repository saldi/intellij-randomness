package com.fwdekker.randomness.string

import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe


/**
 * Unit tests for [StringSettings].
 */
object StringSettingsTest : Spek({
    lateinit var stringSettings: StringSettings


    beforeEachTest {
        stringSettings = StringSettings()
    }


    describe("state management") {
        it("creates an independent copy") {
            val copy = stringSettings.deepCopy()
            stringSettings.currentScheme.minLength = 49
            copy.currentScheme.minLength = 244

            assertThat(stringSettings.currentScheme.minLength).isEqualTo(49)
        }

        it("copies state from another instance") {
            val symbolSets = listOf(SymbolSet.BRACKETS)

            stringSettings.currentScheme.minLength = 730
            stringSettings.currentScheme.maxLength = 891
            stringSettings.currentScheme.enclosure = "Qh7"
            stringSettings.currentScheme.symbolSetList = symbolSets
            stringSettings.currentScheme.excludeLookAlikeSymbols = true

            val newStringSettings = StringSettings()
            newStringSettings.loadState(stringSettings.state)

            assertThat(newStringSettings.currentScheme.minLength).isEqualTo(730)
            assertThat(newStringSettings.currentScheme.maxLength).isEqualTo(891)
            assertThat(newStringSettings.currentScheme.enclosure).isEqualTo("Qh7")
            assertThat(newStringSettings.currentScheme.symbolSetList).isEqualTo(symbolSets)
            assertThat(newStringSettings.currentScheme.excludeLookAlikeSymbols).isEqualTo(true)
        }
    }

    describe("copying") {
        describe("copyFrom") {
            it("makes the two schemes equal") {
                val schemeA = StringScheme()
                val schemeB = StringScheme(myName = "Name")
                assertThat(schemeA).isNotEqualTo(schemeB)

                schemeA.copyFrom(schemeB)

                assertThat(schemeA).isEqualTo(schemeB)
            }
        }

        describe("copyAs") {
            it("makes two schemes equal except for the name") {
                val schemeA = StringScheme()
                val schemeB = schemeA.copyAs("NewName")
                assertThat(schemeA).isNotEqualTo(schemeB)

                schemeB.myName = schemeA.myName

                assertThat(schemeA).isEqualTo(schemeB)
            }
        }
    }

    describe("emoji serialization") {
        it("serializes emoji") {
            val symbolSets = listOf(SymbolSet("emoji", "💆")).toMap()
            val scheme = StringScheme(symbolSets = symbolSets, activeSymbolSets = symbolSets)

            assertThat(scheme.serializedSymbolSets["emoji"]).isEqualTo(":massage:")
            assertThat(scheme.serializedActiveSymbolSets["emoji"]).isEqualTo(":massage:")
        }

        it("deserializes emoji") {
            val symbolSets = listOf(SymbolSet("emoji", ":couple_with_heart_man_man:")).toMap()
            val scheme = StringScheme().apply {
                serializedSymbolSets = symbolSets
                serializedActiveSymbolSets = symbolSets
            }

            assertThat(scheme.symbolSets["emoji"]).isEqualTo("👨‍❤️‍👨")
            assertThat(scheme.activeSymbolSets["emoji"]).isEqualTo("👨‍❤️‍👨")
        }
    }
})
