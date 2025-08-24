package chimeracardsplus.helpers;

import basemod.BaseMod;
import chimeracardsplus.ChimeraCardsPlus;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.evacipated.cardcrawl.mod.stslib.Keyword;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.localization.UIStrings;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;

public class ResourceLoader {
    private static final String DEFAULT_LANGUAGE = "eng";
    private static final String RESOURCE_FOLDER = "chimeracardsplus";
    private final Gson gson = new Gson();
    private final HashMap<String, Texture> textures = new HashMap<>(16);

    private static String getLangString() {
        return Settings.language.name().toLowerCase(Locale.getDefault());
    }

    public static String imagePath(String file) {
        return RESOURCE_FOLDER + "/images/" + file;
    }

    public static String localizationPath(String lang, String file) {
        return RESOURCE_FOLDER + "/localization/" + lang + '/' + file;
    }

    public Texture getTexture(String filePath) {
        return getTexture(filePath, true);
    }

    public Texture getTexture(String filePath, boolean linearFilter) {
        String imagePath = imagePath(filePath);
        Texture texture = textures.get(imagePath);
        if (texture == null) {
            try {
                texture = loadTexture(imagePath, linearFilter);
            } catch (GdxRuntimeException e) {
                ChimeraCardsPlus.logger.info("Failed to find texture {}", imagePath, e);
                return null;
            }
        }
        return texture;
    }

    private Texture loadTexture(String filePath, boolean linearFilter) {
        Texture texture = new Texture(filePath);
        if (linearFilter) {
            texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        } else {
            texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        }
        ChimeraCardsPlus.logger.info("Loaded texture {}", filePath);
        textures.put(filePath, texture);
        return texture;
    }

    public void loadStrings() {
        loadStrings(DEFAULT_LANGUAGE);
        if (!DEFAULT_LANGUAGE.equals(getLangString())) {
            loadStrings(getLangString());
        }
    }

    private void loadStrings(String lang) {
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
        ChimeraCardsPlus.specialNamingRules.addRules(gson.fromJson(Gdx.files.internal(localizationPath(lang, "SpecialNamingRules.json")).readString(String.valueOf(StandardCharsets.UTF_8)), SpecialNamingRules.class));
    }

    public void loadKeywords() {
        loadKeywords(DEFAULT_LANGUAGE);
        if (!DEFAULT_LANGUAGE.equals(getLangString())) {
            loadKeywords(getLangString());
        }
    }

    private void loadKeywords(String lang) {
        Keyword[] keywords = gson.fromJson(Gdx.files.internal(localizationPath(lang, "KeywordStrings.json")).readString(String.valueOf(StandardCharsets.UTF_8)), Keyword[].class);
        if (keywords != null) {
            for (Keyword keyword : keywords) {
                BaseMod.addKeyword(ChimeraCardsPlus.MOD_ID, keyword.PROPER_NAME, keyword.NAMES, keyword.DESCRIPTION);
            }
        }
    }
}
