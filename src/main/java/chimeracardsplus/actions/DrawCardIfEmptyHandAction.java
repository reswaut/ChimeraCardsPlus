package chimeracardsplus.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class DrawCardIfEmptyHandAction extends AbstractGameAction {
    private final AbstractCard cardToExclude;

    public DrawCardIfEmptyHandAction(AbstractCard card) {
        cardToExclude = card;
    }

    @Override
    public void update() {
        for (AbstractCard c : AbstractDungeon.player.hand.group) {
            if (!cardToExclude.uuid.equals(c.uuid)) {
                isDone = true;
                return;
            }
        }
        addToTop(new DrawCardAction(1));
        isDone = true;
    }
}
