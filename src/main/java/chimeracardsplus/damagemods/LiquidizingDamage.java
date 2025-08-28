package chimeracardsplus.damagemods;

import chimeracardsplus.actions.ObtainLiquidizingPotionAction;
import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class LiquidizingDamage extends CardModifierDamageModifier {
    @Override
    public void onLastDamageTakenUpdate(DamageInfo info, int lastDamageTaken, int overkillAmount, AbstractCreature target) {
        if (!dealtFatalDamage(info, lastDamageTaken, overkillAmount, target)) {
            return;
        }
        addToBot(new ObtainLiquidizingPotionAction());
    }

    @Override
    public AbstractDamageModifier makeCopy() {
        return new LiquidizingDamage();
    }
}
