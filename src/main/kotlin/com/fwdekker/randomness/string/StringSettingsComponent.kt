package com.fwdekker.randomness.string

import com.fwdekker.randomness.CapitalizationMode.Companion.getMode
import com.fwdekker.randomness.SchemesPanel
import com.fwdekker.randomness.SettingsComponent
import com.fwdekker.randomness.SettingsComponentListener
import com.fwdekker.randomness.string.StringScheme.Companion.DEFAULT_CAPITALIZATION
import com.fwdekker.randomness.string.StringScheme.Companion.DEFAULT_ENCLOSURE
import com.fwdekker.randomness.string.StringSettings.Companion.default
import com.fwdekker.randomness.ui.JIntSpinner
import com.fwdekker.randomness.ui.JSpinnerRange
import com.fwdekker.randomness.ui.PreviewPanel
import com.fwdekker.randomness.ui.getValue
import com.fwdekker.randomness.ui.setValue
import com.intellij.openapi.ui.ValidationInfo
import java.util.ArrayList
import javax.swing.ButtonGroup
import javax.swing.JPanel


/**
 * Component for settings of random string generation.
 *
 * @param settings the settings to edit in the component
 *
 * @see StringSettingsAction
 * @see SymbolSetTable
 */
@Suppress("LateinitUsage") // Initialized by scene builder
class StringSettingsComponent(settings: StringSettings = default) :
    SettingsComponent<StringSettings, StringScheme>(settings) {
    override lateinit var unsavedSettings: StringSettings
    override lateinit var schemesPanel: SchemesPanel<StringScheme>

    private lateinit var contentPane: JPanel
    private lateinit var previewPanelHolder: PreviewPanel
    private lateinit var previewPanel: JPanel
    private lateinit var lengthRange: JSpinnerRange
    private lateinit var minLength: JIntSpinner
    private lateinit var maxLength: JIntSpinner
    private lateinit var enclosureGroup: ButtonGroup
    private lateinit var capitalizationGroup: ButtonGroup
    private lateinit var symbolSetPanel: JPanel
    private lateinit var symbolSetTable: SymbolSetTable

    override val rootPane get() = contentPane


    init {
        loadSettings()

        previewPanelHolder.updatePreviewOnUpdateOf(
            minLength, maxLength, enclosureGroup, capitalizationGroup, symbolSetTable)
        previewPanelHolder.updatePreview()
    }


    /**
     * Initialises custom UI components.
     *
     * This method is called by the scene builder at the start of the constructor.
     */
    @Suppress("UnusedPrivateMember") // Used by scene builder
    private fun createUIComponents() {
        unsavedSettings = StringSettings()
        schemesPanel = StringSchemesPanel(unsavedSettings)
            .also { it.addListener(SettingsComponentListener(this)) }

        previewPanelHolder = PreviewPanel { StringInsertAction(StringScheme().also { saveScheme(it) }) }
        previewPanel = previewPanelHolder.rootPane

        minLength = JIntSpinner(1, 1, description = "minimum length")
        maxLength = JIntSpinner(1, 1, description = "maximum length")
        lengthRange = JSpinnerRange(minLength, maxLength, Int.MAX_VALUE.toDouble(), "length")
        symbolSetTable = SymbolSetTable()
        symbolSetPanel = symbolSetTable.createComponent()
    }

    override fun loadScheme(scheme: StringScheme) {
        minLength.value = scheme.minLength
        maxLength.value = scheme.maxLength
        enclosureGroup.setValue(scheme.enclosure)
        capitalizationGroup.setValue(scheme.capitalization)
        symbolSetTable.data = scheme.symbolSetList
        symbolSetTable.activeData = scheme.activeSymbolSetList
    }

    override fun saveScheme(scheme: StringScheme) {
        scheme.minLength = minLength.value
        scheme.maxLength = maxLength.value
        scheme.enclosure = enclosureGroup.getValue() ?: DEFAULT_ENCLOSURE
        scheme.capitalization = capitalizationGroup.getValue()?.let { getMode(it) } ?: DEFAULT_CAPITALIZATION
        scheme.symbolSetList = symbolSetTable.data
        scheme.activeSymbolSetList = symbolSetTable.activeData
    }

    /**
     * Returns true if any symbol sets have been reordered.
     *
     * @param settings the settings to check for modifications
     * @return true if any symbol sets have been reordered
     */
    override fun isModified(settings: StringSettings): Boolean {
        val tableSymbolSets: List<SymbolSet> = ArrayList(symbolSetTable.data)
        val settingsSymbolSets: List<SymbolSet> = ArrayList(settings.currentScheme.symbolSetList)

        return tableSymbolSets.size != settingsSymbolSets.size ||
            tableSymbolSets.zip(settingsSymbolSets).any { it.first != it.second }
    }

    override fun doValidate() =
        when {
            symbolSetTable.data.any { it.name.isEmpty() } ->
                ValidationInfo("All symbol sets must have a name.", symbolSetPanel)
            symbolSetTable.data.map { it.name }.distinct().size != symbolSetTable.data.size ->
                ValidationInfo("Symbol sets must have unique names.", symbolSetPanel)
            symbolSetTable.data.any { it.symbols.isEmpty() } ->
                ValidationInfo("Symbol sets must have at least one symbol each.", symbolSetPanel)
            symbolSetTable.activeData.isEmpty() ->
                ValidationInfo("Activate at least one symbol set.", symbolSetPanel)
            else ->
                minLength.validateValue()
                    ?: maxLength.validateValue()
                    ?: lengthRange.validateValue()
        }


    /**
     * A panel to select schemes from.
     *
     * @param settings the settings model backing up the panel
     */
    private class StringSchemesPanel(settings: StringSettings) : SchemesPanel<StringScheme>(settings) {
        override val type: Class<StringScheme>
            get() = StringScheme::class.java

        override fun createDefaultInstance() = StringScheme()
    }
}