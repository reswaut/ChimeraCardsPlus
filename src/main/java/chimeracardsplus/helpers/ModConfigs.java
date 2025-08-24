package chimeracardsplus.helpers;

import basemod.IUIElement;
import basemod.ModLabeledToggleButton;
import basemod.ModPanel;
import chimeracardsplus.ChimeraCardsPlus;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.UIStrings;

import java.io.IOException;
import java.util.Properties;

public class ModConfigs {
    private static final String FILE_NAME = "chimera_cards_plus_config";
    private static final String EVENT_ADDONS_PLUS_KEY = "EventAddonsPlus";
    private static final String SPECIAL_NAMING_KEY = "CompactNaming";
    private static final String BASE_GAME_FIXES_KEY = "CardFixes";
    private boolean enableEventAddonsPlus = true;
    private boolean enableSpecialNaming = false;
    private boolean enableBaseGameFixes = true;
    private SpireConfig config = null;
    private String name = null, author = null, desc = null, labelText = null;

    public boolean enableEventAddons() {
        return enableEventAddonsPlus;
    }

    public boolean enableSpecialNaming() {
        return enableSpecialNaming;
    }

    public boolean enableBaseGameFixes() {
        return enableBaseGameFixes;
    }

    public void initialize() {
        Properties defaultSettings = new Properties();
        defaultSettings.setProperty(EVENT_ADDONS_PLUS_KEY, String.valueOf(enableEventAddonsPlus));
        defaultSettings.setProperty(SPECIAL_NAMING_KEY, String.valueOf(enableSpecialNaming));
        defaultSettings.setProperty(BASE_GAME_FIXES_KEY, String.valueOf(enableBaseGameFixes));

        try {
            config = new SpireConfig(ChimeraCardsPlus.MOD_ID, FILE_NAME, defaultSettings);
            enableEventAddonsPlus = config.getBool(EVENT_ADDONS_PLUS_KEY);
            enableSpecialNaming = config.getBool(SPECIAL_NAMING_KEY);
            enableBaseGameFixes = config.getBool(BASE_GAME_FIXES_KEY);
            ChimeraCardsPlus.logger.info("Loaded mod configs.");
        } catch (IOException e) {
            ChimeraCardsPlus.logger.warn("Failed to load mod config, using default settings instead.", e);
        }
    }

    public ModPanel setupModPanel() {
        UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ChimeraCardsPlus.makeID(ModConfigs.class.getSimpleName()));
        String[] TEXT = uiStrings.TEXT;
        String[] EXTRA_TEXT = uiStrings.EXTRA_TEXT;
        name = EXTRA_TEXT[0];
        author = EXTRA_TEXT[1];
        desc = EXTRA_TEXT[2];
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
        IUIElement enableSpecialNamingButton = new ModLabeledToggleButton(TEXT[5], TEXT[6], 350.0F, yPos, Settings.CREAM_COLOR, FontHelper.charDescFont, enableSpecialNaming, settingsPanel, label -> {
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

        return settingsPanel;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public String getDesc() {
        return desc;
    }

    public String getLabelText() {
        return labelText;
    }
}
