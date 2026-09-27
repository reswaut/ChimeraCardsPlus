package chimeracardsplus.damagemods;

import chimeracardsplus.actions.EchoCardAction;
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
        addToBot(new EchoCardAction((AbstractCard) DamageModifierManager.getInstigator(info)));
    }

    @Override
    public AbstractDamageModifier makeCopy() {
        return new EchoingDamage();
    }
}
