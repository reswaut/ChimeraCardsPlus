package chimeracardsplus.powers;

import chimeracardsplus.ChimeraCardsPlus;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class PlatingPower extends AbstractPower {
    public static final String POWER_ID = ChimeraCardsPlus.makeID(PlatingPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    private static final String NAME = powerStrings.NAME;
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public PlatingPower(AbstractCreature owner, int amount) {
        name = NAME;
        ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        type = PowerType.BUFF;
        isTurnBased = true;
        updateDescription();
        Texture texture48 = ChimeraCardsPlus.resourceLoader.getModTexture("powers/plating32.png");
        Texture texture128 = ChimeraCardsPlus.resourceLoader.getModTexture("powers/plating84.png");
        region48 = new AtlasRegion(texture48, 0, 0, texture48.getWidth(), texture48.getHeight());
        region128 = new AtlasRegion(texture128, 0, 0, texture128.getWidth(), texture128.getHeight());
    }

    @Override
    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);
        updateDescription();
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        flash();
        addToBot(new GainBlockAction(owner, owner, amount));
    }

    @Override
    public void atStartOfTurn() {
        addToBot(new ReducePowerAction(owner, owner, this, 1));
    }

    @Override
    public void updateDescription() {
        description = String.format(owner.isPlayer ? DESCRIPTIONS[0] : DESCRIPTIONS[1], amount);
    }
}
