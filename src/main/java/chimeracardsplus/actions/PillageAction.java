package chimeracardsplus.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;

import java.util.Collection;

public class PillageAction extends AbstractGameAction {
    private final CardType cardType;

    public PillageAction(CardType cardType) {
        this.cardType = cardType;
    }

    @Override
    public void update() {
        addToTop(new DrawCardAction(1, new PillageCheckAction(cardType), true));
        isDone = true;
    }

    public static class PillageCheckAction extends AbstractGameAction {
        private final CardType cardType;

        public PillageCheckAction(CardType cardType) {
            this.cardType = cardType;
        }

        @Override
        public void update() {
            Collection<AbstractCard> cards = DrawCardAction.drawnCards;
            if (!cards.isEmpty() && cards.stream().allMatch(c -> c.type == cardType)) {
                addToTop(new PillageAction(cardType));
            }
            isDone = true;
        }
    }
}
