package chimeracardsplus.damagemods;

import chimeracardsplus.actions.ReplayCardAction;
import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.evacipated.cardcrawl.mod.stslib.damagemods.DamageModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class EchoingDamage extends CardModifierDamageModifier {
    @Override
    public void onLastDamageTakenUpdate(DamageInfo info, int lastDamageTaken, int overkillAmount, AbstractCreature target) {
        if (!killedEnemy(info, lastDamageTaken, overkillAmount, target)) {
            return;
        }
        Object instigator = DamageModifierManager.getInstigator(info);
        if (instigator instanceof AbstractCard) {
            addToBot(new ReplayCardAction((AbstractCard) instigator, true));
        }
    }

    @Override
    public AbstractDamageModifier makeCopy() {
        return new EchoingDamage();
    }
}
