package chimeracardsplus.actions;

import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;

public class ExhaustCardInDrawPileAction extends AbstractGameAction {
    private static final String ID = ChimeraCardsPlus.makeID(ExhaustCardInDrawPileAction.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private boolean first = true;

    public ExhaustCardInDrawPileAction() {
        actionType = ActionType.EXHAUST;
    }

    @Override
    public void update() {
        if (first) {
            first = false;
            CardGroup tmpGroup = new CardGroup(CardGroupType.UNSPECIFIED);
            for (AbstractCard c : AbstractDungeon.player.drawPile.group) {
                tmpGroup.addToRandomSpot(c);
            }
            if (tmpGroup.isEmpty()) {
                isDone = true;
            } else if (tmpGroup.size() == 1) {
                exhaust(tmpGroup.getTopCard());
                isDone = true;
            } else {
                AbstractDungeon.gridSelectScreen.open(tmpGroup, 1, TEXT[0], false);
            }
            return;
        }
        if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            for (AbstractCard card : AbstractDungeon.gridSelectScreen.selectedCards) {
                card.unhover();
                exhaust(card);
            }
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
        }
        isDone = true;
    }

    private void exhaust(AbstractCard card) {
        addToTop(new ExhaustSpecificCardAction(card, AbstractDungeon.player.drawPile));
    }
}
