package chimeracardsplus.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.PlayTopCardAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class DelayedPlayTopCardAction extends AbstractGameAction {
    @Override
    public void update() {
        addToBot(new PlayTopCardAction(AbstractDungeon.getCurrRoom().monsters.getRandomMonster(null, true, AbstractDungeon.cardRandomRng), true));
        isDone = true;
    }
}
