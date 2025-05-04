package chimeracardsplus.interfaces;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

public interface TriggerPreDeathMod extends HealingMod {
    boolean preDeath(AbstractCard card, AbstractPlayer player);
}
