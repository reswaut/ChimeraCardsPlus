package chimeracardsplus.helpers;

import com.megacrit.cardcrawl.characters.AbstractPlayer;

import java.util.Locale;

public class CardCheckHelpers {
    public static boolean hasCardWithKeywordInDeck(AbstractPlayer p, String keyword) {
        return p.masterDeck.group.stream().anyMatch(card -> card.rawDescription.toLowerCase(Locale.getDefault()).contains(keyword.toLowerCase(Locale.getDefault())));
    }
}
