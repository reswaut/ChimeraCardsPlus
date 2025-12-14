package chimeracardsplus.actions;

import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.helpers.Constants;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;

import java.util.ArrayList;
import java.util.Collection;

public class FlexibleAction extends AbstractGameAction {
    private static final String ID = ChimeraCardsPlus.makeID(FlexibleAction.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;

    private final Collection<AbstractCard> cannotChoose = new ArrayList<>(Constants.DEFAULT_LIST_SIZE);
    private boolean first = true;

    public FlexibleAction() {
        actionType = ActionType.CARD_MANIPULATION;
    }

    @Override
    public void update() {
        if (first) {
            first = false;
            for (AbstractCard c : AbstractDungeon.player.hand.group) {
                if (c.cost <= 0) {
                    cannotChoose.add(c);
                }
            }

            if (cannotChoose.size() == AbstractDungeon.player.hand.group.size()) {
                isDone = true;
                return;
            }

            if (AbstractDungeon.player.hand.group.size() - cannotChoose.size() == 1) {
                for (AbstractCard c : AbstractDungeon.player.hand.group) {
                    if (c.cost > 0) {
                        c.modifyCostForCombat(-1);
                        isDone = true;
                        return;
                    }
                }
            }

            AbstractDungeon.player.hand.group.removeAll(cannotChoose);
            if (AbstractDungeon.player.hand.group.size() > 1) {
                AbstractDungeon.handCardSelectScreen.open(TEXT[0], 1, false);
                return;
            }

            if (AbstractDungeon.player.hand.group.size() == 1) {
                AbstractDungeon.player.hand.getTopCard().modifyCostForCombat(-1);
                returnCards();
                isDone = true;
            }
            return;
        }

        if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
            for (AbstractCard c : AbstractDungeon.handCardSelectScreen.selectedCards.group) {
                c.modifyCostForCombat(-1);
                AbstractDungeon.player.hand.addToTop(c);
            }
            returnCards();
            AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
            AbstractDungeon.handCardSelectScreen.selectedCards.group.clear();
        }

        isDone = true;
    }

    private void returnCards() {
        for (AbstractCard c : cannotChoose) {
            AbstractDungeon.player.hand.addToTop(c);
        }
        AbstractDungeon.player.hand.refreshHandLayout();
    }
}
