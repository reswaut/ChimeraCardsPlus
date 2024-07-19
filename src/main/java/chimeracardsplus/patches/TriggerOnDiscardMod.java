package chimeracardsplus.patches;

import com.megacrit.cardcrawl.cards.AbstractCard;

public interface TriggerOnDiscardMod {
    void onManualDiscard(AbstractCard card);

    void onMoveToDiscard(AbstractCard card);
}
