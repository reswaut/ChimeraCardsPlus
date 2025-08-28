package chimeracardsplus.damagemods;

import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.vfx.combat.FlyingOrbEffect;

public class ReaperDamage extends CardModifierDamageModifier {
    @Override
    public void onLastDamageTakenUpdate(DamageInfo info, int lastDamageTaken, int overkillAmount, AbstractCreature target) {
        if (!dealtUnblockedDamage(info, lastDamageTaken, overkillAmount, target)) {
            return;
        }
        for (int i = Math.min(lastDamageTaken / 2, 10); i > 0; --i) {
            addToBot(new VFXAction(new FlyingOrbEffect(target.hb.cX, target.hb.cY)));
        }
        addToBot(new HealAction(info.owner, info.owner, lastDamageTaken));
    }

    @Override
    public AbstractDamageModifier makeCopy() {
        return new ReaperDamage();
    }
}
