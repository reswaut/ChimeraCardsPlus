package chimeracardsplus.damagemods;

import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class SunderDamage extends CardModifierDamageModifier {
    private final int amount;

    public SunderDamage(int energy) {
        amount = energy;
    }

    @Override
    public void onLastDamageTakenUpdate(DamageInfo info, int lastDamageTaken, int overkillAmount, AbstractCreature target) {
        if (!killedEnemy(info, lastDamageTaken, overkillAmount, target)) {
            return;
        }
        addToBot(new GainEnergyAction(amount));
    }

    @Override
    public AbstractDamageModifier makeCopy() {
        return new SunderDamage(amount);
    }
}
