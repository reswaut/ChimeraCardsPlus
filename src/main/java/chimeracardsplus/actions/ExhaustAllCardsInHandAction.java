package chimeracardsplus.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class ExhaustAllCardsInHandAction extends AbstractGameAction {
    private final CardType excludeCardType;

    public ExhaustAllCardsInHandAction(CardType cardType) {
        excludeCardType = cardType;
    }

    @Override
    public void update() {
        for (AbstractCard c : AbstractDungeon.player.hand.group) {
            if (c.type != excludeCardType) {
                addToTop(new ExhaustSpecificCardAction(c, AbstractDungeon.player.hand));
            }
        }
        isDone = true;
    }
}
