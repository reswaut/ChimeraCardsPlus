package chimeracardsplus.util;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

public class CardCheckHelpers {
    public static boolean hasCardWithKeywordInDeck(AbstractPlayer p, String keyword) {
        for (AbstractCard card : p.masterDeck.group) {
            if (card.rawDescription.toLowerCase().contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
