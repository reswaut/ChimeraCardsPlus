package chimeracardsplus;

import CardAugments.CardAugmentsMod;
import basemod.*;
import basemod.interfaces.*;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import chimeracardsplus.helpers.PotionUseHelper;
import chimeracardsplus.helpers.SpecialNamingRules;
import chimeracardsplus.helpers.TextureLoader;
import chimeracardsplus.powers.NoDamagePower;
import chimeracardsplus.powers.RetributionPower;
import chimeracardsplus.powers.StunPlayerPower;
import chimeracardsplus.powers.UntappedPower;
import com.badlogic.gdx.Files.FileType;
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
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Properties;

@SpireInitializer
public class ChimeraCardsPlus implements
        EditKeywordsSubscriber,
        EditStringsSubscriber,
        OnStartBattleSubscriber,
        OnPlayerTurnStartSubscriber,
        PostPotionUseSubscriber,
        PostInitializeSubscriber {
    private static final String resourcesFolder = checkResourcesPath();
    private static final String FILE_NAME = "chimera_cards_plus_config";
    private static final String DEFAULT_LANGUAGE = "eng";
    public static final String modID = "chimeracardsplus";
    public static final Logger logger = LogManager.getLogger(modID);
    public static final SpecialNamingRules specialNamingRules = new SpecialNamingRules();
    private static final TextureLoader textureLoader = new TextureLoader();
    private static final String EVENT_ADDONS_PLUS_KEY = "EventAddonsPlus";
    private static final String SPECIAL_NAMING_KEY = "CompactNaming";
    private static final String CARD_FIXES_KEY = "CardFixes";
    private static boolean enableEventAddonsPlus = true;
    private static boolean enableSpecialNaming = false;
    private static boolean enableCardFixes = true;
    private static SpireConfig config = null;
    private static ModPanel settingsPanel = null;
    private static UIStrings uiStrings = null;
    private static String[] TEXT = null;

    public static String makeID(String id) {
        return modID + ':' + id;
    }

    public static void initialize() {
        BaseMod.subscribe(new ChimeraCardsPlus());

        Properties defaultSettings = new Properties();
        defaultSettings.setProperty(EVENT_ADDONS_PLUS_KEY, String.valueOf(enableEventAddonsPlus));
        defaultSettings.setProperty(SPECIAL_NAMING_KEY, String.valueOf(enableSpecialNaming));
        defaultSettings.setProperty(CARD_FIXES_KEY, String.valueOf(enableCardFixes));

        try {
            config = new SpireConfig(modID, FILE_NAME, defaultSettings);
            enableEventAddonsPlus = config.getBool(EVENT_ADDONS_PLUS_KEY);
            enableSpecialNaming = config.getBool(SPECIAL_NAMING_KEY);
            enableCardFixes = config.getBool(CARD_FIXES_KEY);
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
    public static boolean enableCardFixes() {
        return enableCardFixes;
    }

    private static void setupSettingsPanel() {
        settingsPanel = new ModPanel();

        float yPos = Settings.HEIGHT * 0.5f / Settings.scale + 200.0f;
        IUIElement enableCardFixesButton = new ModLabeledToggleButton(TEXT[1], TEXT[2], 350.0F, yPos, Settings.CREAM_COLOR, FontHelper.charDescFont, enableCardFixes, settingsPanel, label -> {
        }, button -> {
            config.setBool(CARD_FIXES_KEY, button.enabled);
            enableCardFixes = button.enabled;
            try {
                config.save();
            } catch (IOException e) {
                e.printStackTrace();
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
                e.printStackTrace();
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
                e.printStackTrace();
            }
        });
        settingsPanel.addUIElement(enableSpecialNamingButton);
    }

    private static String getLangString() {
        return Settings.language.name().toLowerCase(Locale.getDefault());
    }

    private static String checkResourcesPath() {
        String name = ChimeraCardsPlus.class.getName();
        name = name.substring(0, name.indexOf('.'));

        FileHandle resources = new LwjglFileHandle(name, FileType.Internal);
        if (resources.child("images").exists() && resources.child("localization").exists()) {
            return name;
        }

        throw new RuntimeException("\n\tFailed to find resources folder; expected it to be named \"" + name + "\"." +
                " Either make sure the folder under resources has the same name as your mod's package, or change the line\n" +
                "\t\"private static final String resourcesFolder = checkResourcesPath();\"\n" +
                "\tat the top of the " + ChimeraCardsPlus.class.getSimpleName() + " java file.");
    }

    private static void loadUIStrings() {
        uiStrings = CardCrawlGame.languagePack.getUIString(makeID("ModConfigs"));
        TEXT = uiStrings.TEXT;
    }

    private static void loadLocalization(String lang) {
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
        return resourcesFolder + "/localization/" + lang + '/' + file;
    }

    private static void loadKeywords(String lang) {
        Gson gson = new Gson();
        String json = Gdx.files.internal(localizationPath(lang, "KeywordStrings.json")).readString(String.valueOf(StandardCharsets.UTF_8));
        Keyword[] keywords = gson.fromJson(json, Keyword[].class);
        if (keywords != null) {
            for (Keyword keyword : keywords) {
                BaseMod.addKeyword(modID, keyword.PROPER_NAME, keyword.NAMES, keyword.DESCRIPTION);
            }
        }
    }

    private static void loadSpecialNamingRules(String lang) {
        Gson gson = new Gson();
        String json = Gdx.files.internal(localizationPath(lang, "SpecialNamingRules.json")).readString(String.valueOf(StandardCharsets.UTF_8));
        SpecialNamingRules rules = gson.fromJson(json, SpecialNamingRules.class);
        if (rules != null) {
            specialNamingRules.addRules(rules);
        }
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

    @Override
    public void receiveOnPlayerTurnStart() {
        PotionUseHelper.onPlayerTurnStart();
    }

    @Override
    public void receivePostInitialize() {
        Texture badgeTexture = textureLoader.getTexture(imagePath("badge.png"));
        loadUIStrings();

        setupSettingsPanel();
        BaseMod.registerModBadge(badgeTexture, uiStrings.EXTRA_TEXT[0], uiStrings.EXTRA_TEXT[1], uiStrings.EXTRA_TEXT[2], settingsPanel);

        CardAugmentsMod.registerMod(modID, TEXT[0]);

        new AutoAdd(modID).packageFilter("chimeracardsplus.cardmods")
                .any(AbstractAugmentPlus.class, (info, augment) -> CardAugmentsMod.registerAugment(augment, modID));

        BaseMod.addPower(NoDamagePower.class, NoDamagePower.POWER_ID);
        BaseMod.addPower(RetributionPower.class, RetributionPower.POWER_ID);
        BaseMod.addPower(StunPlayerPower.class, StunPlayerPower.POWER_ID);
        BaseMod.addPower(UntappedPower.class, UntappedPower.POWER_ID);
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

    @Override
    public void receiveOnBattleStart(AbstractRoom abstractRoom) {
        PotionUseHelper.onBattleStart(abstractRoom);
    }

    @Override
    public void receivePostPotionUse(AbstractPotion abstractPotion) {
        PotionUseHelper.onUsePotion(abstractPotion);
    }
}
