package chimeracardsplus.damagemods;

import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.evacipated.cardcrawl.mod.stslib.damagemods.DamageModifierManager;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.FlyingOrbEffect;

public class ReaperDamage extends AbstractDamageModifier {
    public ReaperDamage() {
        priority = 32767;
    }

    @Override
    public void onLastDamageTakenUpdate(DamageInfo info, int lastDamageTaken, int overkillAmount, AbstractCreature target) {
        if (target instanceof AbstractMonster && DamageModifierManager.getInstigator(info) instanceof AbstractCard
                && AbstractDungeon.player.equals(info.owner)
                && target.currentHealth > 0 && !target.halfDead) {
            for (int j = 0; j < lastDamageTaken / 2 && j < 10; j++) {
                addToBot(new VFXAction(new FlyingOrbEffect(target.hb.cX, target.hb.cY)));
            }
            addToBot(new HealAction(info.owner, info.owner, lastDamageTaken));
        }
    }

    @Override
    public boolean isInherent() {
        return true;
    }

    @Override
    public AbstractDamageModifier makeCopy() {
        return new ReaperDamage();
    }
}
