package chimeracardsplus.damagemods;

import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class FlashyDamage extends CardModifierDamageModifier {
    private final int cardDraw;

    public FlashyDamage(int cardDraw) {
        this.cardDraw = cardDraw;
    }

    @Override
    public void onLastDamageTakenUpdate(DamageInfo info, int lastDamageTaken, int overkillAmount, AbstractCreature target) {
        if (!dealtUnblockedDamage(info, lastDamageTaken, overkillAmount, target)) {
            return;
        }
        addToBot(new DrawCardAction(cardDraw));
    }

    @Override
    public AbstractDamageModifier makeCopy() {
        return new FlashyDamage(cardDraw);
    }
}
