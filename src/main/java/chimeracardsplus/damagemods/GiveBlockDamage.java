package chimeracardsplus.damagemods;

import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.evacipated.cardcrawl.mod.stslib.damagemods.DamageModifierManager;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class GiveBlockDamage extends AbstractDamageModifier {
    public GiveBlockDamage() {
        this.priority = 32767;
    }

    public void onLastDamageTakenUpdate(DamageInfo info, int lastDamageTaken, int overkillAmount, AbstractCreature targetHit) {
        if (targetHit instanceof AbstractMonster && DamageModifierManager.getInstigator(info) instanceof AbstractCard
                && targetHit.currentHealth > 0
                && targetHit.currentHealth - lastDamageTaken > 0 && !targetHit.halfDead && lastDamageTaken >= 1) {
            addToBot(new GainBlockAction(targetHit, lastDamageTaken));
        }
    }

    public boolean isInherent() {
        return true;
    }

    public AbstractDamageModifier makeCopy() {
        return new GiveBlockDamage();
    }
}
