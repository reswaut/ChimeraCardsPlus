package chimeracardsplus.actions;

import basemod.BaseMod;
import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;

public class CardByIDFromDeckToHandAction extends AbstractGameAction {
    private static final String ID = ChimeraCardsPlus.makeID(CardByIDFromDeckToHandAction.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private final String cardID;
    private boolean first = true;

    public CardByIDFromDeckToHandAction(int amount, String cardID) {
        this.cardID = cardID;
        this.amount = amount;
        actionType = ActionType.CARD_MANIPULATION;
    }

    @Override
    public void update() {
        if (first) {
            first = false;
            CardGroup tmp = new CardGroup(CardGroupType.UNSPECIFIED);

            for (AbstractCard c : AbstractDungeon.player.drawPile.group) {
                if (c.cardID.equals(cardID)) {
                    tmp.addToRandomSpot(c);
                }
            }

            if (tmp.isEmpty()) {
                isDone = true;
            } else if (tmp.size() == 1) {
                AbstractCard card = tmp.getTopCard();
                if (AbstractDungeon.player.hand.size() >= BaseMod.MAX_HAND_SIZE) {
                    AbstractDungeon.player.drawPile.moveToDiscardPile(card);
                    AbstractDungeon.player.createHandIsFullDialog();
                } else {
                    AbstractDungeon.player.drawPile.moveToHand(card);
                }

                isDone = true;
            } else {
                AbstractDungeon.gridSelectScreen.open(tmp, amount, TEXT[0], false);
            }
            return;
        }
        if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            for (AbstractCard card : AbstractDungeon.gridSelectScreen.selectedCards) {
                card.unhover();
                if (AbstractDungeon.player.hand.size() >= BaseMod.MAX_HAND_SIZE) {
                    AbstractDungeon.player.drawPile.moveToDiscardPile(card);
                    AbstractDungeon.player.createHandIsFullDialog();
                } else {
                    AbstractDungeon.player.drawPile.moveToHand(card);
                }
            }
        }
        isDone = true;
    }
}