package chimeracardsplus.powers;

import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class StunPlayerPower extends AbstractPower {
    public static final String POWER_ID = ChimeraCardsPlus.makeID(StunPlayerPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    private static final String NAME = powerStrings.NAME;
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public StunPlayerPower(AbstractCreature owner, int amount) {
        name = NAME;
        ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        type = PowerType.DEBUFF;
        isTurnBased = true;
        updateDescription();
        img = ChimeraCardsPlus.resourceLoader.getTexture("images/stslib/powers/32/stun.png");
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (amount == 0) {
            addToBot(new RemoveSpecificPowerAction(owner, owner, POWER_ID));
        } else {
            addToBot(new ReducePowerAction(owner, owner, POWER_ID, 1));
        }
    }

    @Override
    public boolean canPlayCard(AbstractCard card) {
        card.cantUseMessage = DESCRIPTIONS[1];
        return false;
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0];
    }
}
