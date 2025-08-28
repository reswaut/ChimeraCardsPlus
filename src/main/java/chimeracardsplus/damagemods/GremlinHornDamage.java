package chimeracardsplus.damagemods;

import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class GremlinHornDamage extends CardModifierDamageModifier {
    @Override
    public void onLastDamageTakenUpdate(DamageInfo info, int lastDamageTaken, int overkillAmount, AbstractCreature target) {
        if (!killedEnemy(info, lastDamageTaken, overkillAmount, target)) {
            return;
        }
        addToBot(new GainEnergyAction(1));
        addToBot(new DrawCardAction(1));
    }

    @Override
    public AbstractDamageModifier makeCopy() {
        return new GremlinHornDamage();
    }
}
