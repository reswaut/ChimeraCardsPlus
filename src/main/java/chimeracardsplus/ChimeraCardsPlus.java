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
import chimeracardsplus.util.GeneralUtils;
import chimeracardsplus.util.TextureLoader;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglFileHandle;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.evacipated.cardcrawl.mod.stslib.Keyword;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.Patcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.scannotation.AnnotationDB;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@SpireInitializer
public class ChimeraCardsPlus implements
        EditKeywordsSubscriber,
        EditStringsSubscriber,
        PostInitializeSubscriber {
    public static ModInfo info;
    private static final String EVENT_ADDONS_PLUS_KEY = "EventAddonsPlus";
    static { loadModInfo(); }
    private static final String resourcesFolder = checkResourcesPath();
    private static final String FILE_NAME = "chimera_cards_plus_config";
    private static final String DEFAULT_LANGUAGE = "eng";
    public static String modID;
    public static final Logger logger = LogManager.getLogger(modID);
    public static SpireConfig config;
    public static ModPanel settingsPanel;
    private static boolean enableEventAddonsPlus = true;
    private static UIStrings uiStrings;
    private static String[] TEXT;

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

        try {
            config = new SpireConfig(modID, FILE_NAME, defaultSettings);
            enableEventAddonsPlus = config.getBool(EVENT_ADDONS_PLUS_KEY);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean enableEventAddons() {
        return enableEventAddonsPlus;
    }

    private static void setupSettingsPanel() {
        settingsPanel = new ModPanel();
        uiStrings = CardCrawlGame.languagePack.getUIString(makeID("ModConfigs"));
        TEXT = uiStrings.TEXT;

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
    }

    private static String getLangString() {
        return Settings.language.name().toLowerCase();
    }

    /*----------Localization----------*/

    private static String checkResourcesPath() {
        String name = ChimeraCardsPlus.class.getName(); //getPackage can be iffy with patching, so class name is used instead.
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

    private static void loadModInfo() {
        Optional<ModInfo> infos = Arrays.stream(Loader.MODINFOS).filter((modInfo)->{
            AnnotationDB annotationDB = Patcher.annotationDBMap.get(modInfo.jarURL);
            if (annotationDB == null) {
                return false;
            }
            Set<String> initializers = annotationDB.getAnnotationIndex().getOrDefault(SpireInitializer.class.getName(), Collections.emptySet());
            return initializers.contains(ChimeraCardsPlus.class.getName());
        }).findFirst();
        if (!infos.isPresent()) {
            throw new RuntimeException("Failed to determine mod info/ID based on initializer.");
        }
        info = infos.get();
        modID = info.ID;
    }

    @Override
    public void receivePostInitialize() {
        Texture badgeTexture = TextureLoader.getTexture(imagePath("badge.png"));

        setupSettingsPanel();
        BaseMod.registerModBadge(badgeTexture, info.Name, GeneralUtils.arrToString(info.Authors), info.Description, settingsPanel);

        CardAugmentsMod.registerMod(modID, TEXT[0]);

        new AutoAdd(modID)
                .packageFilter("chimeracardsplus.cardmods")
                .any(AbstractAugment.class, (info, abstractAugment) -> CardAugmentsMod.registerAugment(abstractAugment, modID));
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
        BaseMod.loadCustomStringsFile(UIStrings.class,
                localizationPath(lang, "ModifierStrings.json"));
        BaseMod.loadCustomStringsFile(UIStrings.class,
                localizationPath(lang, "UIStrings.json"));
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
}
