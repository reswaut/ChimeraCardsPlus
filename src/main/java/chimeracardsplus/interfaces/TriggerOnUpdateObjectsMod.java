package chimeracardsplus.interfaces;

import com.megacrit.cardcrawl.cards.AbstractCard;

@FunctionalInterface
public interface TriggerOnUpdateObjectsMod extends HealingMod {
    boolean onUpdateObjects(AbstractCard card);
}
