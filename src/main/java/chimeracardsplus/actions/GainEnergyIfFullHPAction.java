package chimeracardsplus.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class GainEnergyIfFullHPAction extends AbstractGameAction {
    public GainEnergyIfFullHPAction(int energyGain) {
        amount = energyGain;
    }

    @Override
    public void update() {
        if (AbstractDungeon.player.currentHealth >= AbstractDungeon.player.maxHealth) {
            addToBot(new GainEnergyAction(amount));
        }
        isDone = true;
    }
}
