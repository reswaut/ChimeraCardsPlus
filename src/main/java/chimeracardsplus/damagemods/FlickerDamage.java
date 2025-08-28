package chimeracardsplus.damagemods;

import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class FlickerDamage extends CardModifierDamageModifier {
    private final AbstractCard hiddenCard;
    public boolean addedReturnToHand;

    public FlickerDamage(AbstractCard card) {
        this(card, false);
    }

    private FlickerDamage(AbstractCard card, boolean addedReturnToHand) {
        hiddenCard = card;
        this.addedReturnToHand = addedReturnToHand;
    }

    @Override
    public void onLastDamageTakenUpdate(DamageInfo info, int lastDamageTaken, int overkillAmount, AbstractCreature target) {
        if (!killedEnemy(info, lastDamageTaken, overkillAmount, target)) {
            return;
        }
        if (!hiddenCard.returnToHand) {
            hiddenCard.returnToHand = true;
            addedReturnToHand = true;
        }
    }

    @Override
    public AbstractDamageModifier makeCopy() {
        return new FlickerDamage(hiddenCard, addedReturnToHand);
    }
}
