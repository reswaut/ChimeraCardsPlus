package chimeracardsplus.cards.preview;

import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;

public class BitingPreview extends AbstractPreview {
    private static final String ID = ChimeraCardsPlus.makeID(BitingPreview.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    private static final String NAME = cardStrings.NAME;
    private static final String DESCRIPTION = cardStrings.DESCRIPTION;

    public BitingPreview() {
        super(ID, NAME, DESCRIPTION, CardType.ATTACK, "colorless/attack/bite");
    }

    @Override
    public AbstractCard makeCopy() {
        return new BitingPreview();
    }
}
