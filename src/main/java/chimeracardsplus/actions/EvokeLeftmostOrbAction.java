package chimeracardsplus.actions;

import com.evacipated.cardcrawl.mod.stslib.actions.defect.EvokeSpecificOrbAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.orbs.EmptyOrbSlot;

public class EvokeLeftmostOrbAction extends AbstractGameAction {
    @Override
    public void update() {
        int index = AbstractDungeon.player.orbs.size() - 1;
        while (index >= 0 && AbstractDungeon.player.orbs.get(index) instanceof EmptyOrbSlot) {
            index -= 1;
        }
        if (index >= 0) {
            AbstractOrb orb = AbstractDungeon.player.orbs.get(index);
            orb.triggerEvokeAnimation();
            addToTop(new EvokeSpecificOrbAction(orb));
        }
        isDone = true;
    }
}
