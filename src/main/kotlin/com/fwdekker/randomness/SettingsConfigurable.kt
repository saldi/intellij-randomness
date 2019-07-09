package com.fwdekker.randomness

import com.intellij.openapi.options.Configurable
import javax.swing.JComponent
import javax.swing.JPanel


/**
 * Randomness' root configurable; all other configurables are its children.
 */
// TODO Populate this.
class RandomnessConfigurable : Configurable {
    /**
     * Returns the name of the configurable as displayed in the settings window.
     *
     * @return the name of the configurable as displayed in the settings window
     */
    override fun getDisplayName() = "Randomness"

    /**
     * Returns false because there is nothing to be modified.
     *
     * @return false because there is nothing to be modified
     */
    override fun isModified() = false

    /**
     * Does nothing because nothing can be done.
     */
    override fun apply() = Unit

    /**
     * Returns an empty panel.
     *
     * @return an empty panel
     */
    override fun createComponent() = JPanel()
}


/**
 * A configurable to change settings of type [S].
 *
 * Allows the settings to be displayed in IntelliJ's settings window.
 *
 * @param S the type of settings the configurable changes.
 */
abstract class SettingsConfigurable<S : Settings<S>> : Configurable {
    /**
     * The user interface for changing the settings.
     */
    protected abstract val component: SettingsComponent<S>


    /**
     * Returns the name of the configurable as displayed in the settings window.
     *
     * @return the name of the configurable as displayed in the settings window
     */
    abstract override fun getDisplayName(): String

    /**
     * Returns true if the settings were modified since they were loaded.
     *
     * @return true if the settings were modified since they were loaded
     */
    override fun isModified() = component.isModified()

    /**
     * Saves the changes in the settings component to the default settings object.
     */
    override fun apply() = component.saveSettings()

    /**
     * Discards unsaved changes in the settings component.
     */
    override fun reset() = component.reset()

    /**
     * Returns the root pane of the settings component.
     *
     * @return the root pane of the settings component
     */
    override fun createComponent(): JComponent? = component.getRootPane()
}
