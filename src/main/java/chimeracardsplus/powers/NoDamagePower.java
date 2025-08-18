package chimeracardsplus.powers;

import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class NoDamagePower extends AbstractPower {
    public static final String POWER_ID = ChimeraCardsPlus.makeID(NoDamagePower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    private static final String NAME = powerStrings.NAME;
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private boolean justApplied = false;

    public NoDamagePower(AbstractCreature creature, int amount, boolean isSourceMonster) {
        name = NAME;
        ID = POWER_ID;
        owner = creature;
        this.amount = amount;
        type = PowerType.DEBUFF;
        isTurnBased = true;
        updateDescription();
        loadRegion("noattack");
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
    public float atDamageFinalGive(float damage, DamageType type) {
        return type == DamageType.NORMAL ? 0.0F : damage;
    }

    @Override
    public void updateDescription() {
        description = powerStrings.DESCRIPTIONS[0];
    }
}
