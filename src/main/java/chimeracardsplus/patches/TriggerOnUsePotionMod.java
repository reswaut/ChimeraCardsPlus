package chimeracardsplus.patches;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.potions.AbstractPotion;

public interface TriggerOnUsePotionMod {
    void onUsePotion(AbstractCard card, CardGroup group, AbstractPotion potion);
}
