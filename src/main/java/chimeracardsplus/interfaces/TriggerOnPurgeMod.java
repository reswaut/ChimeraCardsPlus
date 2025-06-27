package chimeracardsplus.interfaces;

import com.megacrit.cardcrawl.cards.AbstractCard;

public interface TriggerOnPurgeMod extends HealingMod {
    void onRemoveFromMasterDeck(AbstractCard card);
}
