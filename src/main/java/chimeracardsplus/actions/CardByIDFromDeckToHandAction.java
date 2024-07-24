package chimeracardsplus.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;

public class CardByIDFromDeckToHandAction extends AbstractGameAction {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("AnyCardFromDeckToHandAction");
    public static final String[] TEXT = uiStrings.TEXT;
    private final String cardID;

    public CardByIDFromDeckToHandAction(int amount, String cardID) {
        this.cardID = cardID;
        this.setValues(AbstractDungeon.player, AbstractDungeon.player, amount);
        this.actionType = ActionType.CARD_MANIPULATION;
        this.duration = Settings.ACTION_DUR_MED;
    }

    public void update() {
        AbstractCard card;
        if (this.duration == Settings.ACTION_DUR_MED) {
            CardGroup tmp = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);

            for (AbstractCard c : AbstractDungeon.player.drawPile.group) {
                if (c.cardID.equals(this.cardID)) {
                    tmp.addToRandomSpot(c);
                }
            }

            if (tmp.isEmpty()) {
                this.isDone = true;
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

                this.isDone = true;
            } else {
                AbstractDungeon.gridSelectScreen.open(tmp, this.amount, TEXT[0], false);
                this.tickDuration();
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

            this.tickDuration();
        }
    }
}