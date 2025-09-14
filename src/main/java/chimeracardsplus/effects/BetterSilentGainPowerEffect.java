package chimeracardsplus.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class BetterSilentGainPowerEffect extends AbstractGameEffect {
    private static final float EFFECT_DUR = 2.0F;
    private Texture img = null;
    private AtlasRegion region48 = null;
    private int img_width = 0, img_height = 0;

    public BetterSilentGainPowerEffect(Texture img) {
        this(img, img.getWidth(), img.getHeight());
    }

    public BetterSilentGainPowerEffect(Texture img, int img_width, int img_height) {
        this.img = img;
        this.img_width = img_width;
        this.img_height = img_height;
        duration = EFFECT_DUR;
        startingDuration = EFFECT_DUR;
        scale = Settings.scale;
        color = new Color(1.0F, 1.0F, 1.0F, 0.5F);
    }

    public BetterSilentGainPowerEffect(AtlasRegion region48) {
        this.region48 = region48;
        duration = EFFECT_DUR;
        startingDuration = EFFECT_DUR;
        scale = Settings.scale;
        color = new Color(1.0F, 1.0F, 1.0F, 0.5F);
    }

    public static BetterSilentGainPowerEffect makeNewEffect(AbstractPower power) {
        if (power.img != null) {
            return new BetterSilentGainPowerEffect(power.img);
        }
        return new BetterSilentGainPowerEffect(power.region48);
    }

    @Override
    public void update() {
        duration -= Gdx.graphics.getDeltaTime();
        if (duration > 0.5F) {
            scale = Interpolation.exp5Out.apply(3.0F * Settings.scale, Settings.scale, -(duration - EFFECT_DUR) / 1.5F);
        } else if (duration > 0.0F) {
            color.a = Interpolation.fade.apply(0.5F, 0.0F, 1.0F - duration);
        } else {
            isDone = true;
            color.a = 0.0F;
        }
    }

    @Override
    public void render(SpriteBatch sb, float x, float y) {
        sb.setColor(color);
        sb.setBlendFunction(770, 1);
        if (img != null) {
            // sb.draw(img, x - 16.0F, y - 16.0F, 16.0F, 16.0F, 32.0F, 32.0F, Settings.scale * 1.875F, Settings.scale * 1.875F, 0.0F, 0, 0, img_width, img_height, false, false);
            sb.draw(img, x - 16.0F, y - 16.0F, 16.0F, 16.0F, 32.0F, 32.0F, scale * 1.875F, scale * 1.875F, 0.0F, 0, 0, img_width, img_height, false, false);
        } else {
            // sb.draw(region48, x - region48.packedWidth / 2.0F, y - region48.packedHeight / 2.0F, region48.packedWidth / 2.0F, region48.packedHeight / 2.0F, region48.packedWidth, region48.packedHeight, Settings.scale, Settings.scale, 0.0F);
            sb.draw(region48, x - region48.packedWidth / 2.0F, y - region48.packedHeight / 2.0F, region48.packedWidth / 2.0F, region48.packedHeight / 2.0F, region48.packedWidth, region48.packedHeight, scale, scale, 0.0F);
        }

        sb.setBlendFunction(770, 771);
    }

    @Override
    public void dispose() {
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
    }
}
