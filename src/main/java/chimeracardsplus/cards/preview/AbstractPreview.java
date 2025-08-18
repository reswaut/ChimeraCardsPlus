package chimeracardsplus.cards.preview;

import CardAugments.util.AugmentPreviewCard;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireOverride;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public abstract class AbstractPreview extends AbstractCard {
    private static final String TYPE = AugmentPreviewCard.MY_TEXT[0];
    private final Color typeColor = new Color(0.35F, 0.35F, 0.35F, 0.0F);
    private Color renderColor = null;

    protected AbstractPreview(String id, String name, String rawDescription, CardType type, String imgUrl) {
        super(id, name, imgUrl, -2, rawDescription, type, CardColor.COLORLESS, CardRarity.SPECIAL, CardTarget.NONE);
    }

    @Override
    public void upgrade() {
    }

    @Override
    public void use(AbstractPlayer abstractPlayer, AbstractMonster abstractMonster) {
    }

    @SpireOverride
    public void renderType(SpriteBatch sb) {
        BitmapFont font = FontHelper.cardTypeFont;
        font.getData().setScale(drawScale);
        if (renderColor == null) {
            renderColor = ReflectionHacks.getPrivateInherited(this, AugmentPreviewCard.class, "renderColor");
        }
        typeColor.a = renderColor.a;
        FontHelper.renderRotatedText(sb, font, TYPE, current_x, current_y - 22.0F * drawScale * Settings.scale, 0.0F, -1.0F * drawScale * Settings.scale, angle, false, typeColor);
    }
}
