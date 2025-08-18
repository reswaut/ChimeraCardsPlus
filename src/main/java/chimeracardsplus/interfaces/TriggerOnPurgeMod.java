package chimeracardsplus.interfaces;

import com.megacrit.cardcrawl.cards.AbstractCard;

@FunctionalInterface
public interface TriggerOnPurgeMod extends HealingMod {
    void onRemoveFromMasterDeck(AbstractCard card);
}
