package chimeracardsplus.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.defect.AnimateOrbAction;
import com.megacrit.cardcrawl.actions.defect.EvokeOrbAction;
import com.megacrit.cardcrawl.actions.defect.EvokeWithoutRemovingOrbAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.EmptyOrbSlot;

public class EvokeAllOrbsAction extends AbstractGameAction {
    private final int times;

    public EvokeAllOrbsAction(int times) {
        this.times = times;
    }

    @Override
    public void update() {
        int orbs = Math.toIntExact(AbstractDungeon.player.orbs.stream().filter(orb -> !(orb instanceof EmptyOrbSlot)).count());
        // addToTop is LIFO, so each evocation is queued in reverse order.
        for (int i = 0; i < orbs; ++i) {
            addToTop(new EvokeOrbAction(1));
            addToTop(new AnimateOrbAction(1));
            for (int j = 1; j < times; ++j) {
                addToTop(new EvokeWithoutRemovingOrbAction(1));
                addToTop(new AnimateOrbAction(1));
            }
        }
        isDone = true;
    }
}
