package chimeracardsplus.actions;

import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;

public class ShuffleCardToDrawAction extends AbstractGameAction {
    private static final String ID = ChimeraCardsPlus.makeID(ShuffleCardToDrawAction.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private boolean first = true;

    public ShuffleCardToDrawAction() {
        actionType = ActionType.CARD_MANIPULATION;
    }

    @Override
    public void update() {
        if (first) {
            first = false;
            if (AbstractDungeon.player.hand.isEmpty()) {
                isDone = true;
            } else if (AbstractDungeon.player.hand.size() == 1) {
                AbstractCard c = AbstractDungeon.player.hand.getTopCard();
                AbstractDungeon.player.hand.moveToDeck(c, true);
                AbstractDungeon.player.hand.refreshHandLayout();
                isDone = true;
            } else {
                AbstractDungeon.handCardSelectScreen.open(TEXT[0], 1, false);
            }
            return;
        }
        if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
            for (AbstractCard c : AbstractDungeon.handCardSelectScreen.selectedCards.group) {
                AbstractDungeon.player.hand.moveToDeck(c, true);
            }
            AbstractDungeon.player.hand.refreshHandLayout();
            AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
        }
        isDone = true;
    }
}
