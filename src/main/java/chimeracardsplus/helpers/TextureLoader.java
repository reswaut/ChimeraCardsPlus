package chimeracardsplus.helpers;

import chimeracardsplus.ChimeraCardsPlus;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.util.HashMap;

public class TextureLoader {
    private final HashMap<String, Texture> textures = new HashMap<>(16);

    public Texture getTexture(String filePath) {
        return getTexture(filePath, true);
    }

    public Texture getTexture(String filePath, boolean linear) {
        if (textures.get(filePath) == null) {
            try {
                loadTexture(filePath, linear);
            } catch (GdxRuntimeException e) {
                ChimeraCardsPlus.logger.info("Failed to find texture {}", filePath, e);
                Texture missing = getTextureNull(ChimeraCardsPlus.imagePath("missing.png"), false);
                if (missing == null) {
                    ChimeraCardsPlus.logger.info("missing.png is missing, should be at {}", ChimeraCardsPlus.imagePath("missing.png"));
                }
                return missing;
            }
        }
        Texture t = textures.get(filePath);
        if (t != null && t.getTextureObjectHandle() == 0) {
            textures.remove(filePath);
            t = getTexture(filePath, linear);
        }
        return t;
    }

    public Texture getTextureNull(String filePath) {
        return getTextureNull(filePath, true);
    }

    public Texture getTextureNull(String filePath, boolean linear) {
        if (!textures.containsKey(filePath)) {
            try {
                loadTexture(filePath, linear);
            } catch (GdxRuntimeException e) {
                textures.put(filePath, null);
            }
        }
        Texture t = textures.get(filePath);
        if (t != null && t.getTextureObjectHandle() == 0) {
            textures.remove(filePath);
            t = getTextureNull(filePath, linear);
        }
        return t;
    }

    private void loadTexture(String textureString) {
        loadTexture(textureString, false);
    }

    private void loadTexture(String textureString, boolean linearFilter) {
        Texture texture = new Texture(textureString);
        if (linearFilter) {
            texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        } else {
            texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        }
        ChimeraCardsPlus.logger.info("Loaded texture {}", textureString);
        textures.put(textureString, texture);
    }
}