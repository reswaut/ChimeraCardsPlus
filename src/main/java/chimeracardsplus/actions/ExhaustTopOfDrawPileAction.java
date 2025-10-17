package chimeracardsplus.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class ExhaustTopOfDrawPileAction extends AbstractGameAction {
    public ExhaustTopOfDrawPileAction(int numCards) {
        amount = numCards;
        actionType = ActionType.EXHAUST;
    }

    @Override
    public void update() {
        int realAmount = Math.min(amount, AbstractDungeon.player.drawPile.size());
        for (int i = 0; i < realAmount; ++i) {
            AbstractDungeon.player.drawPile.moveToExhaustPile(AbstractDungeon.player.drawPile.group.get(AbstractDungeon.player.drawPile.size() - i - 1));
        }
        isDone = true;
    }
}