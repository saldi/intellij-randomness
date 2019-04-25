package com.fwdekker.randomness.uuid

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it


/**
 * Unit tests for [UuidSettings].
 */
object UuidSettingsTest : Spek({
    lateinit var uuidSettings: UuidSettings


    beforeEachTest {
        uuidSettings = UuidSettings()
    }


    describe("state persistence") {
        it("copies state from another instance") {
            uuidSettings.enclosure = "nvpB"

            val newUuidSettings = UuidSettings()
            newUuidSettings.loadState(uuidSettings.state)

            assertThat(newUuidSettings.enclosure).isEqualTo("nvpB")
        }
    }
})
