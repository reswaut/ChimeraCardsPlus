package chimeracardsplus.cards.preview;

import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;

public class DecayingPreview extends AbstractPreview {
    private static final String ID = ChimeraCardsPlus.makeID(DecayingPreview.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    private static final String NAME = cardStrings.NAME;
    private static final String DESCRIPTION = cardStrings.DESCRIPTION;

    public DecayingPreview() {
        super(ID, NAME, DESCRIPTION, CardType.CURSE, "curse/decay");
    }

    @Override
    public AbstractCard makeCopy() {
        return new DecayingPreview();
    }
}
