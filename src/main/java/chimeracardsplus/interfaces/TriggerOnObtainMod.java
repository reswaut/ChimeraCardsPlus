package chimeracardsplus.interfaces;

import com.megacrit.cardcrawl.cards.AbstractCard;

@FunctionalInterface
public interface TriggerOnObtainMod extends HealingMod {
    void onObtain(AbstractCard card);
}
