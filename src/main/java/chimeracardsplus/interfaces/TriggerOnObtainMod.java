package chimeracardsplus.interfaces;

import com.megacrit.cardcrawl.cards.AbstractCard;

public interface TriggerOnObtainMod extends HealingMod {
    void onObtain(AbstractCard card);
}
