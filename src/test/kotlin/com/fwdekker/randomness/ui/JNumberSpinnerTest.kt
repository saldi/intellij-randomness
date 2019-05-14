package com.fwdekker.randomness.ui

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.swing.edt.GuiActionRunner
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import javax.swing.JSpinner


/**
 * Unit tests for a sample implementation of [JNumberSpinner].
 */
object JNumberSpinnerTest : Spek({
    class JFloatSpinner(
        value: Float = 0.0f,
        minValue: Float = -Float.MAX_VALUE,
        maxValue: Float = Float.MAX_VALUE
    ) : JNumberSpinner<Float>(value, minValue, maxValue, 0.1f) {
        override val numberToT: (Number) -> Float
            get() = { it.toFloat() }
    }


    fun createJFloatSpinner() =
        GuiActionRunner.execute<JFloatSpinner> { JFloatSpinner() }

    fun createJFloatSpinner(value: Float, minValue: Float, maxValue: Float) =
        GuiActionRunner.execute<JFloatSpinner> { JFloatSpinner(value, minValue, maxValue) }


    describe("constructor failures") {
        it("should fail if the minimum value is greater than the maximum value") {
            assertThatThrownBy { createJFloatSpinner(value = -725.18f, minValue = -602.98f, maxValue = -929.41f) }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessage("(minimum <= value <= maximum) is false")
        }

        it("should fail if the value is not in between the minimum and maximum value") {
            assertThatThrownBy { createJFloatSpinner(value = 1136.57f, minValue = 552.50f, maxValue = 944.18f) }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessage("(minimum <= value <= maximum) is false")
        }
    }

    describe("input handling") {
        it("should get and set the value") {
            val spinner = createJFloatSpinner()

            GuiActionRunner.execute { spinner.value = 179.40f }

            assertThat(spinner.value).isEqualTo(179.40f)
        }

        it("should return a float even if a long is set") {
            val spinner = createJFloatSpinner()

            GuiActionRunner.execute { (spinner as JSpinner).value = 638L }

            assertThat(spinner.value).isEqualTo(638.0f)
        }
    }

    describe("validation") {
        it("should fail if the value is lower than the minimum value") {
            val spinner = createJFloatSpinner(value = 0.0f, minValue = -24.80f, maxValue = 31.51f)

            GuiActionRunner.execute { spinner.value = -88.59f }

            val info = spinner.validateValue()
            assertThat(info).isNotNull()
            assertThat(info?.message).isEqualTo("Please enter a value greater than or equal to -24.8.")
        }

        it("should fail if the value is higher than the maximum value") {
            val spinner = createJFloatSpinner(value = 26.74f, minValue = 16.89f, maxValue = 32.03f)

            GuiActionRunner.execute { spinner.value = 93.83f }

            val info = spinner.validateValue()
            assertThat(info).isNotNull()
            assertThat(info?.message).isEqualTo("Please enter a value less than or equal to 32.03.")
        }

        it("should pass if the minimum value was adjusted") {
            val spinner = createJFloatSpinner(value = 169.92f, minValue = 139.65f, maxValue = 597.87f)

            GuiActionRunner.execute {
                spinner.minValue = 5.83f
                spinner.value = 88.59f
            }

            assertThat(spinner.validateValue()).isNull()
        }

        it("should pass if the maximum value was adjusted") {
            val spinner = createJFloatSpinner(value = -234.80f, minValue = -493.17f, maxValue = 202.87f)

            GuiActionRunner.execute {
                spinner.maxValue = 474.77f
                spinner.value = 345.01f
            }

            assertThat(spinner.validateValue()).isNull()
        }
    }
})


/**
 * Unit tests for [JDoubleSpinner].
 */
object JDoubleSpinnerTest : Spek({
    describe("input handling") {
        it("should return a double even if a long is set") {
            val spinner = GuiActionRunner.execute<JDoubleSpinner> { JDoubleSpinner() }

            GuiActionRunner.execute { (spinner as JSpinner).value = -750L }

            assertThat(spinner.value).isEqualTo(-750.0)
        }
    }

    describe("getting surrounding values") {
        describe("previous value") {
            it("returns the previous value") {
                val spinner = GuiActionRunner.execute<JDoubleSpinner> { JDoubleSpinner(741.45) }

                assertThat(spinner.previousValue).isEqualTo(741.35)
            }

            it("returns null if the current value is already at the minimum") {
                val spinner = GuiActionRunner.execute<JDoubleSpinner> { JDoubleSpinner(-637.85, minValue = -637.85) }

                assertThat(spinner.previousValue).isNull()
            }
        }

        describe("next value") {
            it("returns the next value") {
                val spinner = GuiActionRunner.execute<JDoubleSpinner> { JDoubleSpinner(629.90) }

                assertThat(spinner.nextValue).isEqualTo(630.00)
            }

            it("returns null if the current value is already at the maximum") {
                val spinner = GuiActionRunner.execute<JDoubleSpinner> { JDoubleSpinner(-28.31, maxValue = -28.31) }

                assertThat(spinner.nextValue).isNull()
            }
        }
    }
})


/**
 * Unit tests for [JLongSpinner].
 */
object JLongSpinnerTest : Spek({
    describe("input handling") {
        it("truncates when storing a double") {
            val spinner = GuiActionRunner.execute<JLongSpinner> { JLongSpinner() }

            GuiActionRunner.execute { spinner.setValue(786.79) }

            assertThat(spinner.value).isEqualTo(786L)
        }
    }

    describe("getting surrounding values") {
        describe("previous value") {
            it("returns the previous value") {
                val spinner = GuiActionRunner.execute<JLongSpinner> { JLongSpinner(56L) }

                assertThat(spinner.previousValue).isEqualTo(55L)
            }

            it("returns null if the current value is already at the minimum") {
                val spinner = GuiActionRunner.execute<JLongSpinner> { JLongSpinner(203L, minValue = 203L) }

                assertThat(spinner.previousValue).isNull()
            }
        }

        describe("next value") {
            it("returns the next value") {
                val spinner = GuiActionRunner.execute<JLongSpinner> { JLongSpinner(112L) }

                assertThat(spinner.nextValue).isEqualTo(113L)
            }

            it("returns null if the current value is already at the maximum") {
                val spinner = GuiActionRunner.execute<JLongSpinner> { JLongSpinner(119L, maxValue = 119L) }

                assertThat(spinner.nextValue).isNull()
            }
        }
    }
})


/**
 * Unit tests for [JIntSpinner].
 */
object JIntSpinnerTest : Spek({
    describe("input handling") {
        it("truncates when storing a double") {
            val spinner = GuiActionRunner.execute<JIntSpinner> { JIntSpinner() }

            GuiActionRunner.execute { spinner.setValue(850.45) }

            assertThat(spinner.value).isEqualTo(850)
        }

        it("should overflow when storing a large long") {
            val spinner = GuiActionRunner.execute<JIntSpinner> { JIntSpinner() }

            GuiActionRunner.execute { spinner.setValue(Integer.MAX_VALUE.toLong() + 2) }

            assertThat(spinner.value).isEqualTo(Integer.MIN_VALUE + 1)
        }
    }

    describe("getting surrounding values") {
        describe("previous value") {
            it("returns the previous value") {
                val spinner = GuiActionRunner.execute<JIntSpinner> { JIntSpinner(205) }

                assertThat(spinner.previousValue).isEqualTo(204)
            }

            it("returns null if the current value is already at the minimum") {
                val spinner = GuiActionRunner.execute<JIntSpinner> { JIntSpinner(188, minValue = 188) }

                assertThat(spinner.previousValue).isNull()
            }
        }

        describe("next value") {
            it("returns the next value") {
                val spinner = GuiActionRunner.execute<JIntSpinner> { JIntSpinner(96) }

                assertThat(spinner.nextValue).isEqualTo(97)
            }

            it("returns null if the current value is already at the maximum") {
                val spinner = GuiActionRunner.execute<JIntSpinner> { JIntSpinner(182, maxValue = 182) }

                assertThat(spinner.nextValue).isNull()
            }
        }
    }
})
