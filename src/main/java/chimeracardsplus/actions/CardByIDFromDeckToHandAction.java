package chimeracardsplus.actions;

import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;

public class CardByIDFromDeckToHandAction extends AbstractGameAction {
    private static final String ID = ChimeraCardsPlus.makeID(CardByIDFromDeckToHandAction.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private final String cardID;

    public CardByIDFromDeckToHandAction(int amount, String cardID) {
        this.cardID = cardID;
        this.amount = amount;
        actionType = ActionType.CARD_MANIPULATION;
        duration = Settings.ACTION_DUR_MED;
    }

    @Override
    public void update() {
        AbstractCard card;
        if (duration >= Settings.ACTION_DUR_MED) {
            CardGroup tmp = new CardGroup(CardGroupType.UNSPECIFIED);

            for (AbstractCard c : AbstractDungeon.player.drawPile.group) {
                if (c.cardID.equals(cardID)) {
                    tmp.addToRandomSpot(c);
                }
            }

            if (tmp.isEmpty()) {
                isDone = true;
            } else if (tmp.size() == 1) {
                card = tmp.getTopCard();
                if (AbstractDungeon.player.hand.size() == 10) {
                    AbstractDungeon.player.drawPile.moveToDiscardPile(card);
                    AbstractDungeon.player.createHandIsFullDialog();
                } else {
                    card.unhover();
                    card.lighten(true);
                    card.setAngle(0.0F);
                    card.drawScale = 0.12F;
                    card.targetDrawScale = 0.75F;
                    card.current_x = CardGroup.DRAW_PILE_X;
                    card.current_y = CardGroup.DRAW_PILE_Y;
                    AbstractDungeon.player.drawPile.removeCard(card);
                    AbstractDungeon.player.hand.addToTop(card);
                    AbstractDungeon.player.hand.refreshHandLayout();
                    AbstractDungeon.player.hand.applyPowers();
                }

                isDone = true;
            } else {
                AbstractDungeon.gridSelectScreen.open(tmp, amount, TEXT[0], false);
                tickDuration();
            }
        } else {
            if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                for (AbstractCard abstractCard : AbstractDungeon.gridSelectScreen.selectedCards) {
                    card = abstractCard;
                    card.unhover();
                    if (AbstractDungeon.player.hand.size() == 10) {
                        AbstractDungeon.player.drawPile.moveToDiscardPile(card);
                        AbstractDungeon.player.createHandIsFullDialog();
                    } else {
                        AbstractDungeon.player.drawPile.removeCard(card);
                        AbstractDungeon.player.hand.addToTop(card);
                    }

                    AbstractDungeon.player.hand.refreshHandLayout();
                    AbstractDungeon.player.hand.applyPowers();
                }

                AbstractDungeon.gridSelectScreen.selectedCards.clear();
                AbstractDungeon.player.hand.refreshHandLayout();
            }

            tickDuration();
        }
    }
}