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
    private final Color renderColor = ReflectionHacks.getPrivateInherited(this, AugmentPreviewCard.class, "renderColor");

    public AbstractPreview(String id, String name, String description, CardType frameType, String imgUrl) {
        super(id, name, imgUrl, -2, description, frameType, CardColor.COLORLESS, CardRarity.SPECIAL, CardTarget.NONE);
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
        font.getData().setScale(this.drawScale);
        this.typeColor.a = this.renderColor.a;
        FontHelper.renderRotatedText(sb, font, TYPE, this.current_x, this.current_y - 22.0F * this.drawScale * Settings.scale, 0.0F, -1.0F * this.drawScale * Settings.scale, this.angle, false, this.typeColor);
    }
}
