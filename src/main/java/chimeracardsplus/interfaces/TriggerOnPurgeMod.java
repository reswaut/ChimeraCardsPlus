package chimeracardsplus.interfaces;

import com.megacrit.cardcrawl.cards.AbstractCard;

public interface TriggerOnPurgeMod extends HealingMod {
    boolean isRemovable(AbstractCard card);

    void onRemoveFromMasterDeck(AbstractCard card);
}
