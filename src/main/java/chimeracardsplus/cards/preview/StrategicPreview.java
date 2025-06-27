package chimeracardsplus.cards.preview;

import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;

public class StrategicPreview extends AbstractPreview {
    private static final String ID = ChimeraCardsPlus.makeID(StrategicPreview.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    private static final String NAME = cardStrings.NAME;
    private static final String DESCRIPTION = cardStrings.DESCRIPTION;

    public StrategicPreview() {
        super(ID, NAME, DESCRIPTION, CardType.SKILL, "colorless/skill/master_of_strategy");
    }

    @Override
    public AbstractCard makeCopy() {
        return new StrategicPreview();
    }
}
