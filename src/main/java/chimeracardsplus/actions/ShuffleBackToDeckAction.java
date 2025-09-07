package chimeracardsplus.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class ShuffleBackToDeckAction extends AbstractGameAction {
    private final AbstractCard card;

    public ShuffleBackToDeckAction(AbstractCard card) {
        this.card = card;
    }

    @Override
    public void update() {
        AbstractDungeon.player.hand.moveToDeck(card, true);
        isDone = true;
    }
}
