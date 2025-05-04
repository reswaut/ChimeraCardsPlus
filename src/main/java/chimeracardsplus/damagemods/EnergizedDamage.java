package chimeracardsplus.damagemods;

import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.evacipated.cardcrawl.mod.stslib.damagemods.DamageModifierManager;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class EnergizedDamage extends AbstractDamageModifier {
    public EnergizedDamage() {
        this.priority = 32767;
    }

    public void onLastDamageTakenUpdate(DamageInfo info, int lastDamageTaken, int overkillAmount, AbstractCreature targetHit) {
        if (DamageModifierManager.getInstigator(info) instanceof AbstractCard
                && targetHit.currentHealth > 0 && !targetHit.halfDead && lastDamageTaken > 0) {
            addToBot(new GainEnergyAction(1));
        }
    }

    public boolean isInherent() {
        return true;
    }

    public AbstractDamageModifier makeCopy() {
        return new EnergizedDamage();
    }
}
