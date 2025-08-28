package chimeracardsplus.damagemods;

import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.VulnerablePower;

public class DrilledDamage extends CardModifierDamageModifier {
    @Override
    public void onLastDamageTakenUpdate(DamageInfo info, int lastDamageTaken, int overkillAmount, AbstractCreature target) {
        if (!dealtUnblockedDamage(info, lastDamageTaken, overkillAmount, target)) {
            return;
        }
        addToBot(new ApplyPowerAction(target, AbstractDungeon.player, new VulnerablePower(target, 2, false)));
    }

    @Override
    public AbstractDamageModifier makeCopy() {
        return new DrilledDamage();
    }
}
