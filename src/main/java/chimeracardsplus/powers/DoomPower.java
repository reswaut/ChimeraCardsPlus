package chimeracardsplus.powers;

import chimeracardsplus.ChimeraCardsPlus;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.HealthBarRenderPower;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.InstantKillAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.combat.LightningEffect;
import com.megacrit.cardcrawl.vfx.combat.WeightyImpactEffect;

public class DoomPower extends AbstractPower implements HealthBarRenderPower {
    public static final String POWER_ID = ChimeraCardsPlus.makeID(DoomPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    private static final String NAME = powerStrings.NAME;
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public DoomPower(AbstractCreature owner, int amount) {
        name = NAME;
        ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        type = PowerType.DEBUFF;
        updateDescription();
        loadRegion("end_turn_death");
    }

    @Override
    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);
        updateDescription();
    }

    @Override
    public int getHealthBarAmount() {
        return amount;
    }

    @Override
    public Color getColor() {
        return Color.PURPLE.cpy();
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (amount >= owner.currentHealth) {
            if (isPlayer) {
                addToBot(new VFXAction(new LightningEffect(owner.hb.cX, owner.hb.cY)));
                addToBot(new LoseHPAction(owner, owner, 99999));
            } else {
                addToBot(new VFXAction(new WeightyImpactEffect(owner.hb.cX, owner.hb.cY, Color.GOLD.cpy())));
                addToBot(new InstantKillAction(owner));
            }
        }
    }

    @Override
    public void updateDescription() {
        description = String.format(DESCRIPTIONS[0], amount);
    }
}
