package chimeracardsplus.helpers;

import basemod.BaseMod;
import basemod.IUIElement;
import basemod.ModLabeledToggleButton;
import basemod.ModPanel;
import chimeracardsplus.ChimeraCardsPlus;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.UIStrings;

import java.io.IOException;
import java.util.Properties;

public class ModConfigs {
    private static final String FILE_NAME = "chimera_cards_plus_config";
    private static final String BASE_GAME_FIXES_KEY = "CardFixes";
    private static final String EVENT_ADDONS_PLUS_KEY = "EventAddonsPlus";
    private static final String MODIFY_NAMES_KEY = "ModifyNames";
    private static final String SPECIAL_NAMING_KEY = "CompactNaming";
    private static final String WHITELIST_MODE_KEY = "WhitelistMode";
    private boolean enableBaseGameFixes = true;
    private boolean enableEventAddonsPlus = true;
    private boolean enableModifyNames = true;
    private boolean enableSpecialNaming = false;
    private boolean enableWhitelistMode = false;
    private SpireConfig config = null;
    private String labelText = null;

    public boolean enableBaseGameFixes() {
        return enableBaseGameFixes;
    }

    public boolean enableEventAddons() {
        return enableEventAddonsPlus;
    }

    public boolean enableModifyNames() {
        return enableModifyNames;
    }

    public boolean enableSpecialNaming() {
        return enableSpecialNaming;
    }

    public boolean enableWhitelist() {
        return enableWhitelistMode;
    }

    public void initialize() {
        Properties defaultSettings = getDefaultSettings();

        try {
            config = new SpireConfig(ChimeraCardsPlus.MOD_ID, FILE_NAME, defaultSettings);
            enableEventAddonsPlus = config.getBool(EVENT_ADDONS_PLUS_KEY);
            enableSpecialNaming = config.getBool(SPECIAL_NAMING_KEY);
            enableBaseGameFixes = config.getBool(BASE_GAME_FIXES_KEY);
            enableWhitelistMode = config.getBool(WHITELIST_MODE_KEY);
            enableModifyNames = config.getBool(MODIFY_NAMES_KEY);
            ChimeraCardsPlus.logger.info("Loaded mod configs.");
        } catch (IOException e) {
            ChimeraCardsPlus.logger.warn("Failed to load mod config, using default settings instead.", e);
        }
    }

    private Properties getDefaultSettings() {
        Properties defaultSettings = new Properties();
        defaultSettings.setProperty(EVENT_ADDONS_PLUS_KEY, String.valueOf(enableEventAddonsPlus));
        defaultSettings.setProperty(SPECIAL_NAMING_KEY, String.valueOf(enableSpecialNaming));
        defaultSettings.setProperty(BASE_GAME_FIXES_KEY, String.valueOf(enableBaseGameFixes));
        defaultSettings.setProperty(WHITELIST_MODE_KEY, String.valueOf(enableWhitelistMode));
        defaultSettings.setProperty(MODIFY_NAMES_KEY, String.valueOf(enableModifyNames));
        return defaultSettings;
    }

    public void setupModPanel(Texture badgeTexture) {
        UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ChimeraCardsPlus.makeID(ModConfigs.class.getSimpleName()));
        String[] TEXT = uiStrings.TEXT;
        String[] EXTRA_TEXT = uiStrings.EXTRA_TEXT;
        labelText = TEXT[0];

        ModPanel settingsPanel = new ModPanel();

        float yPos = Settings.HEIGHT * 0.5f / Settings.scale + 200.0f;
        IUIElement enableCardFixesButton = new ModLabeledToggleButton(TEXT[1], TEXT[2], 350.0F, yPos, Settings.CREAM_COLOR, FontHelper.charDescFont, enableBaseGameFixes, settingsPanel, label -> {
        }, button -> {
            config.setBool(BASE_GAME_FIXES_KEY, button.enabled);
            enableBaseGameFixes = button.enabled;
            try {
                config.save();
            } catch (IOException e) {
                ChimeraCardsPlus.logger.warn("Failed to save mod config.", e);
            }
        });
        settingsPanel.addUIElement(enableCardFixesButton);

        yPos -= 50.0f;
        IUIElement enableEventsButton = new ModLabeledToggleButton(TEXT[3], TEXT[4], 350.0F, yPos, Settings.CREAM_COLOR, FontHelper.charDescFont, enableEventAddonsPlus, settingsPanel, label -> {
        }, button -> {
            config.setBool(EVENT_ADDONS_PLUS_KEY, button.enabled);
            enableEventAddonsPlus = button.enabled;
            try {
                config.save();
            } catch (IOException e) {
                ChimeraCardsPlus.logger.warn("Failed to save mod config.", e);
            }
        });
        settingsPanel.addUIElement(enableEventsButton);

        yPos -= 50.0f;
        IUIElement enableModifyNamesButton = new ModLabeledToggleButton(TEXT[5], TEXT[6], 350.0F, yPos, Settings.CREAM_COLOR, FontHelper.charDescFont, enableModifyNames, settingsPanel, label -> {
        }, button -> {
            config.setBool(MODIFY_NAMES_KEY, button.enabled);
            enableModifyNames = button.enabled;
            try {
                config.save();
            } catch (IOException e) {
                ChimeraCardsPlus.logger.warn("Failed to save mod config.", e);
            }
        });
        settingsPanel.addUIElement(enableModifyNamesButton);

        yPos -= 50.0f;
        IUIElement enableSpecialNamingButton = new ModLabeledToggleButton(TEXT[7], TEXT[8], 350.0F, yPos, Settings.CREAM_COLOR, FontHelper.charDescFont, enableSpecialNaming, settingsPanel, label -> {
        }, button -> {
            config.setBool(SPECIAL_NAMING_KEY, button.enabled);
            enableSpecialNaming = button.enabled;
            try {
                config.save();
            } catch (IOException e) {
                ChimeraCardsPlus.logger.warn("Failed to save mod config.", e);
            }
        });
        settingsPanel.addUIElement(enableSpecialNamingButton);

        yPos -= 50.0f;
        IUIElement enableWhitelistModeButton = new ModLabeledToggleButton(TEXT[9], TEXT[10], 350.0F, yPos, Settings.CREAM_COLOR, FontHelper.charDescFont, enableWhitelistMode, settingsPanel, label -> {
        }, button -> {
            config.setBool(WHITELIST_MODE_KEY, button.enabled);
            enableWhitelistMode = button.enabled;
            try {
                config.save();
            } catch (IOException e) {
                ChimeraCardsPlus.logger.warn("Failed to save mod config.", e);
            }
        });
        settingsPanel.addUIElement(enableWhitelistModeButton);

        BaseMod.registerModBadge(badgeTexture, EXTRA_TEXT[0], EXTRA_TEXT[1], EXTRA_TEXT[2], settingsPanel);
    }

    public String getLabelText() {
        return labelText;
    }
}
