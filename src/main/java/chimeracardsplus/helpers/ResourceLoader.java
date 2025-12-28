package chimeracardsplus.helpers;

import basemod.BaseMod;
import basemod.interfaces.EditKeywordsSubscriber;
import basemod.interfaces.EditStringsSubscriber;
import chimeracardsplus.ChimeraCardsPlus;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.evacipated.cardcrawl.mod.stslib.Keyword;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.localization.UIStrings;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@SpireInitializer
public class ResourceLoader implements
        EditKeywordsSubscriber,
        EditStringsSubscriber {
    private static final String DEFAULT_LANGUAGE = "eng";
    private static final String RESOURCE_FOLDER = "chimeracardsplus";
    private final Gson gson = new Gson();
    private final Map<String, Texture> textures = new HashMap<>(Constants.EXPECTED_TEXTURES);

    public static void initialize() {
    }

    private static String getLangString() {
        return Settings.language.name().toLowerCase(Locale.getDefault());
    }

    private static String getModImagePath(String filePath) {
        return RESOURCE_FOLDER + "/images/" + filePath;
    }

    private static String getLocalizationPath(String lang, String file) {
        return RESOURCE_FOLDER + "/localization/" + lang + '/' + file;
    }

    public Texture getModTexture(String filePath) {
        return getTexture(getModImagePath(filePath));
    }

    public Texture getTexture(String imagePath) {
        Texture texture = textures.get(imagePath);
        if (texture == null) {
            try {
                texture = loadTexture(imagePath);
            } catch (GdxRuntimeException e) {
                ChimeraCardsPlus.logger.info("Failed to find texture {}", imagePath, e);
                return null;
            }
        }
        return texture;
    }

    private Texture loadTexture(String filePath) {
        Texture texture = new Texture(filePath);
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        ChimeraCardsPlus.logger.info("Loaded texture {}", filePath);
        textures.put(filePath, texture);
        return texture;
    }

    private void loadKeywords(String lang) {
        Keyword[] keywords = gson.fromJson(Gdx.files.internal(getLocalizationPath(lang, "KeywordStrings.json")).readString(String.valueOf(StandardCharsets.UTF_8)), Keyword[].class);
        if (keywords != null) {
            for (Keyword keyword : keywords) {
                BaseMod.addKeyword(ChimeraCardsPlus.MOD_ID, keyword.PROPER_NAME, keyword.NAMES, keyword.DESCRIPTION);
            }
        }
    }

    @Override
    public void receiveEditKeywords() {
        try {
            loadKeywords(getLangString());
        } catch (GdxRuntimeException e) {
            ChimeraCardsPlus.logger.error("Failed to load keywords for language {}, fallback to default language {} instead.", getLangString(), DEFAULT_LANGUAGE, e);
            loadKeywords(DEFAULT_LANGUAGE);
        }
        ChimeraCardsPlus.logger.info("Loaded keywords.");
    }

    private void loadStrings(String lang) {
        BaseMod.loadCustomStringsFile(CardStrings.class,
                getLocalizationPath(lang, "CardStrings.json"));
        BaseMod.loadCustomStringsFile(EventStrings.class,
                getLocalizationPath(lang, "EventStrings.json"));
        BaseMod.loadCustomStringsFile(PowerStrings.class,
                getLocalizationPath(lang, "PowerStrings.json"));
        BaseMod.loadCustomStringsFile(UIStrings.class,
                getLocalizationPath(lang, "ModifierStrings.json"));
        BaseMod.loadCustomStringsFile(UIStrings.class,
                getLocalizationPath(lang, "UIStrings.json"));
        ChimeraCardsPlus.specialNamingRules.addRules(gson.fromJson(Gdx.files.internal(getLocalizationPath(lang, "SpecialNamingRules.json")).readString(String.valueOf(StandardCharsets.UTF_8)), SpecialNamingRules.class));
    }

    @Override
    public void receiveEditStrings() {
        try {
            loadStrings(getLangString());
        } catch (GdxRuntimeException e) {
            ChimeraCardsPlus.logger.error("Failed to load strings for language {}, fallback to default language {} instead.", getLangString(), DEFAULT_LANGUAGE, e);
            loadStrings(DEFAULT_LANGUAGE);
        }
        ChimeraCardsPlus.logger.info("Loaded localization strings.");
    }
}
