package chimeracardsplus.uis;

import basemod.IUIElement;
import chimeracardsplus.ChimeraCardsPlus;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.TipHelper;

public class HealingModTooltip implements IUIElement {
    public static final String ID = ChimeraCardsPlus.makeID(HealingModTooltip.class.getSimpleName());
    private static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    private static final String tooltipTitle = TEXT[0];
    private static final String tooltipDescription = TEXT[1];
    private float x = 0.0F, y = 0.0F;

    @Override
    public void render(SpriteBatch spriteBatch) {
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
        x = xPos;
        y = yPos;
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public void setX(float xPos) {
        x = xPos;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public void setY(float yPos) {
        y = yPos;
    }
}
