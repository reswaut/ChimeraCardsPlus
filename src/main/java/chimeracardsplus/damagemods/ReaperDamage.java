package chimeracardsplus.damagemods;

import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.evacipated.cardcrawl.mod.stslib.damagemods.DamageModifierManager;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.vfx.combat.FlyingOrbEffect;

public class ReaperDamage extends AbstractDamageModifier {
    public ReaperDamage() {
        this.priority = 32767;
    }

    public void onLastDamageTakenUpdate(DamageInfo info, int lastDamageTaken, int overkillAmount, AbstractCreature targetHit) {
        if (DamageModifierManager.getInstigator(info) instanceof AbstractCard
                && targetHit.currentHealth > 0 && !targetHit.halfDead) {
            for (int j = 0; j < lastDamageTaken / 2 && j < 10; j++) {
                addToBot(new VFXAction(new FlyingOrbEffect(targetHit.hb.cX, targetHit.hb.cY)));
            }
            addToBot(new HealAction(info.owner, info.owner, lastDamageTaken));
        }
    }

    public boolean isInherent() {
        return true;
    }

    public AbstractDamageModifier makeCopy() {
        return new ReaperDamage();
    }
}
