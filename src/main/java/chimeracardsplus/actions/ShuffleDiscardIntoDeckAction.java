package chimeracardsplus.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.EmptyDeckShuffleAction;
import com.megacrit.cardcrawl.actions.common.ShuffleAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class ShuffleDiscardIntoDeckAction extends AbstractGameAction {
    @Override
    public void update() {
        if (!AbstractDungeon.player.discardPile.isEmpty()) {
            addToBot(new EmptyDeckShuffleAction());
            addToBot(new ShuffleAction(AbstractDungeon.player.drawPile, false));
        }
        isDone = true;
    }
}
