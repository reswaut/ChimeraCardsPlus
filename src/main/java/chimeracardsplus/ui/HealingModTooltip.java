package chimeracardsplus.ui;

import basemod.IUIElement;
import chimeracardsplus.ChimeraCardsPlus;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.TipHelper;

public class HealingModTooltip implements IUIElement {
    public static final String ID = ChimeraCardsPlus.makeID(HealingModTooltip.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static String tooltipTitle = TEXT[0];
    public static String tooltipDescription = TEXT[1];
    public float x = 0.0F, y = 0.0F;

    @Override
    public void render(SpriteBatch sb) {
        TipHelper.renderGenericTip(x, y, tooltipTitle, tooltipDescription);
    }

    @Override
    public void update() {
    }

    @Override
    public int renderLayer() {
        return 3;
    }

    @Override
    public int updateOrder() {
        return 0;
    }

    @Override
    public void set(float xPos, float yPos) {
        this.x = xPos;
        this.y = yPos;
    }

    @Override
    public float getX() {
        return this.x;
    }

    @Override
    public void setX(float xPos) {
        this.x = xPos;
    }

    @Override
    public float getY() {
        return this.y;
    }

    @Override
    public void setY(float yPos) {
        this.y = yPos;
    }
}
