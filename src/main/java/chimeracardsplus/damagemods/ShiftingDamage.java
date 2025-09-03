package chimeracardsplus.damagemods;

import chimeracardsplus.actions.ApplyStrengthDownAction;
import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class ShiftingDamage extends CardModifierDamageModifier {
    @Override
    public void onLastDamageTakenUpdate(DamageInfo info, int lastDamageTaken, int overkillAmount, AbstractCreature target) {
        if (!dealtUnblockedDamage(info, lastDamageTaken, overkillAmount, target)) {
            return;
        }
        addToBot(new ApplyStrengthDownAction(target, lastDamageTaken));
    }

    @Override
    public AbstractDamageModifier makeCopy() {
        return new ShiftingDamage();
    }
}
