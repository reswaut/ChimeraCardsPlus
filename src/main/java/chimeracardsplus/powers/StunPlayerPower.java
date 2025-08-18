package chimeracardsplus.powers;

import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class StunPlayerPower extends AbstractPower {
    public static final String POWER_ID = ChimeraCardsPlus.makeID(StunPlayerPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    private static final String NAME = powerStrings.NAME;
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private boolean justApplied = false;

    public StunPlayerPower(AbstractCreature creature, int amount, boolean isSourceMonster) {
        name = NAME;
        ID = POWER_ID;
        owner = creature;
        this.amount = amount;
        type = PowerType.DEBUFF;
        isTurnBased = true;
        updateDescription();
        img = ImageMaster.loadImage("images/stslib/powers/32/stun.png");
        if (AbstractDungeon.actionManager.turnHasEnded && isSourceMonster) {
            justApplied = true;
        }
    }

    @Override
    public void atEndOfRound() {
        if (justApplied) {
            justApplied = false;
        } else {
            if (amount == 0) {
                addToBot(new RemoveSpecificPowerAction(owner, owner, POWER_ID));
            } else {
                addToBot(new ReducePowerAction(owner, owner, POWER_ID, 1));
            }
        }
    }

    @Override
    public boolean canPlayCard(AbstractCard card) {
        return false;
    }

    @Override
    public void updateDescription() {
        description = powerStrings.DESCRIPTIONS[0];
    }
}
