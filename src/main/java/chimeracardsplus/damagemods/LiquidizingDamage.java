package chimeracardsplus.damagemods;

import chimeracardsplus.actions.ObtainLiquidizingPotionAction;
import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.evacipated.cardcrawl.mod.stslib.damagemods.DamageModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.MinionPower;
import com.megacrit.cardcrawl.powers.UnawakenedPower;

public class LiquidizingDamage extends AbstractDamageModifier {
    public LiquidizingDamage() {
        this.priority = 32767;
    }

    public void onLastDamageTakenUpdate(DamageInfo info, int lastDamageTaken, int overkillAmount, AbstractCreature targetHit) {
        if (targetHit instanceof AbstractMonster && DamageModifierManager.getInstigator(info) instanceof AbstractCard
                && targetHit.currentHealth > 0
                && targetHit.currentHealth - lastDamageTaken <= 0
                && !targetHit.halfDead
                && !targetHit.hasPower(MinionPower.POWER_ID)
                && !targetHit.hasPower(UnawakenedPower.POWER_ID)) {
            this.addToBot(new ObtainLiquidizingPotionAction());
        }
    }

    public boolean isInherent() {
        return true;
    }

    public AbstractDamageModifier makeCopy() {
        return new LiquidizingDamage();
    }
}
