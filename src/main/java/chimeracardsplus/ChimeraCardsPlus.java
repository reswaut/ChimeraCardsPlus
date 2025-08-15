package chimeracardsplus;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.AutoAdd;
import basemod.BaseMod;
import basemod.ModLabeledToggleButton;
import basemod.ModPanel;
import basemod.interfaces.EditKeywordsSubscriber;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import chimeracardsplus.interfaces.SpecialNamingRules;
import chimeracardsplus.powers.NoDamagePower;
import chimeracardsplus.powers.RetributionPower;
import chimeracardsplus.powers.StunPlayerPower;
import chimeracardsplus.powers.UntappedPower;
import chimeracardsplus.util.TextureLoader;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglFileHandle;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.evacipated.cardcrawl.mod.stslib.Keyword;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@SpireInitializer
public class ChimeraCardsPlus implements
        EditKeywordsSubscriber,
        EditStringsSubscriber,
        PostInitializeSubscriber {
    private static final String resourcesFolder = checkResourcesPath();
    private static final String FILE_NAME = "chimera_cards_plus_config";
    private static final String DEFAULT_LANGUAGE = "eng";
    public static final String modID = "chimeracardsplus";
    public static final Logger logger = LogManager.getLogger(modID);
    public static SpireConfig config;
    public static ModPanel settingsPanel;
    private static final String EVENT_ADDONS_PLUS_KEY = "EventAddonsPlus";
    private static boolean enableEventAddonsPlus = true;
    private static final String SPECIAL_NAMING_KEY = "CompactNaming";
    public static SpecialNamingRules specialNamingRules;
    private static UIStrings uiStrings;
    private static String[] TEXT;
    private static boolean enableSpecialNaming = false;

    public static String makeID(String id) {
        return modID + ":" + id;
    }

    public static void initialize() {
        new ChimeraCardsPlus();
    }

    public ChimeraCardsPlus() {
        BaseMod.subscribe(this);
        logger.info("{} subscribed to BaseMod.", modID);

        Properties defaultSettings = new Properties();
        defaultSettings.setProperty(EVENT_ADDONS_PLUS_KEY, String.valueOf(enableEventAddonsPlus));
        defaultSettings.setProperty(SPECIAL_NAMING_KEY, String.valueOf(enableSpecialNaming));

        try {
            config = new SpireConfig(modID, FILE_NAME, defaultSettings);
            enableEventAddonsPlus = config.getBool(EVENT_ADDONS_PLUS_KEY);
            enableSpecialNaming = config.getBool(SPECIAL_NAMING_KEY);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean enableEventAddons() {
        return enableEventAddonsPlus;
    }

    public static boolean enableSpecialNaming() {
        return enableSpecialNaming;
    }

    private static void setupSettingsPanel() {
        settingsPanel = new ModPanel();

        float yPos = Settings.HEIGHT * 0.5f / Settings.scale + 200.0f;
        ModLabeledToggleButton enableEventsButton = new ModLabeledToggleButton(TEXT[1], 350.0F, yPos, Settings.CREAM_COLOR, FontHelper.charDescFont, config.getBool(EVENT_ADDONS_PLUS_KEY), settingsPanel, (label) -> {
        }, (button) -> {
            config.setBool(EVENT_ADDONS_PLUS_KEY, button.enabled);
            enableEventAddonsPlus = button.enabled;
            try {
                config.save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        settingsPanel.addUIElement(enableEventsButton);

        yPos -= 50.0f;
        ModLabeledToggleButton enableSpecialNamingButton = new ModLabeledToggleButton(TEXT[2], 350.0F, yPos, Settings.CREAM_COLOR, FontHelper.charDescFont, config.getBool(SPECIAL_NAMING_KEY), settingsPanel, (label) -> {
        }, (button) -> {
            config.setBool(SPECIAL_NAMING_KEY, button.enabled);
            enableSpecialNaming = button.enabled;
            try {
                config.save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        settingsPanel.addUIElement(enableSpecialNamingButton);
    }

    private static String checkResourcesPath() {
        String name = ChimeraCardsPlus.class.getName();
        name = name.substring(0, name.indexOf('.'));

        FileHandle resources = new LwjglFileHandle(name, Files.FileType.Internal);
        if (resources.child("images").exists() && resources.child("localization").exists()) {
            return name;
        }

        throw new RuntimeException("\n\tFailed to find resources folder; expected it to be named \"" + name + "\"." +
                " Either make sure the folder under resources has the same name as your mod's package, or change the line\n" +
                "\t\"private static final String resourcesFolder = checkResourcesPath();\"\n" +
                "\tat the top of the " + ChimeraCardsPlus.class.getSimpleName() + " java file.");
    }

    private static String getLangString() {
        return Settings.language.name().toLowerCase();
    }

    /*----------Localization----------*/

    @Override
    public void receivePostInitialize() {
        Texture badgeTexture = TextureLoader.getTexture(imagePath("badge.png"));
        uiStrings = CardCrawlGame.languagePack.getUIString(makeID("ModConfigs"));
        TEXT = uiStrings.TEXT;

        setupSettingsPanel();
        BaseMod.registerModBadge(badgeTexture, uiStrings.EXTRA_TEXT[0], uiStrings.EXTRA_TEXT[1], uiStrings.EXTRA_TEXT[2], settingsPanel);

        CardAugmentsMod.registerMod(modID, TEXT[0]);

        new AutoAdd(modID)
                .packageFilter("chimeracardsplus.cardmods")
                .any(AbstractAugment.class, (info, abstractAugment) -> CardAugmentsMod.registerAugment(abstractAugment, modID));

        BaseMod.addPower(NoDamagePower.class, NoDamagePower.POWER_ID);
        BaseMod.addPower(RetributionPower.class, RetributionPower.POWER_ID);
        BaseMod.addPower(StunPlayerPower.class, StunPlayerPower.POWER_ID);
        BaseMod.addPower(UntappedPower.class, UntappedPower.POWER_ID);
    }

    @Override
    public void receiveEditStrings() {
        loadLocalization(DEFAULT_LANGUAGE);
        if (!DEFAULT_LANGUAGE.equals(getLangString())) {
            try {
                loadLocalization(getLangString());
            } catch (GdxRuntimeException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadLocalization(String lang) {
        BaseMod.loadCustomStringsFile(CardStrings.class,
                localizationPath(lang, "CardStrings.json"));
        BaseMod.loadCustomStringsFile(EventStrings.class,
                localizationPath(lang, "EventStrings.json"));
        BaseMod.loadCustomStringsFile(PowerStrings.class,
                localizationPath(lang, "PowerStrings.json"));
        BaseMod.loadCustomStringsFile(UIStrings.class,
                localizationPath(lang, "ModifierStrings.json"));
        BaseMod.loadCustomStringsFile(UIStrings.class,
                localizationPath(lang, "UIStrings.json"));
        loadSpecialNamingRules(lang);
    }

    public static String localizationPath(String lang, String file) {
        return resourcesFolder + "/localization/" + lang + "/" + file;
    }

    public static String imagePath(String file) {
        return resourcesFolder + "/images/" + file;
    }

    @Override
    public void receiveEditKeywords() {
        loadKeywords(DEFAULT_LANGUAGE);
        if (!DEFAULT_LANGUAGE.equals(getLangString())) {
            try {
                loadKeywords(getLangString());
            } catch (GdxRuntimeException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadKeywords(String lang) {
        Gson gson = new Gson();
        String json = Gdx.files.internal(localizationPath(lang, "KeywordStrings.json")).readString(String.valueOf(StandardCharsets.UTF_8));
        Keyword[] keywords = gson.fromJson(json, Keyword[].class);
        if (keywords != null) {
            for (Keyword keyword : keywords) {
                BaseMod.addKeyword(modID, keyword.PROPER_NAME, keyword.NAMES, keyword.DESCRIPTION);
            }
        }
    }

    private void loadSpecialNamingRules(String lang) {
        Gson gson = new Gson();
        String json = Gdx.files.internal(localizationPath(lang, "SpecialNamingRules.json")).readString(String.valueOf(StandardCharsets.UTF_8));
        SpecialNamingRules rules = gson.fromJson(json, SpecialNamingRules.class);
        if (rules != null) {
            if (specialNamingRules == null) {
                specialNamingRules = rules;
            } else {
                specialNamingRules.addRules(rules);
            }
        }
    }
}
