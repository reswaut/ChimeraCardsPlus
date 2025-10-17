package chimeracardsplus.damagemods;

import chimeracardsplus.powers.DoomPower;
import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class DoomedDamage extends CardModifierDamageModifier {
    @Override
    public void onLastDamageTakenUpdate(DamageInfo info, int lastDamageTaken, int overkillAmount, AbstractCreature target) {
        if (!dealtUnblockedDamage(info, lastDamageTaken, overkillAmount, target)) {
            return;
        }
        addToBot(new ApplyPowerAction(target, info.owner, new DoomPower(target, lastDamageTaken)));
    }

    @Override
    public AbstractDamageModifier makeCopy() {
        return new DoomedDamage();
    }
}
