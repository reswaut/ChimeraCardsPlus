package chimeracardsplus.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class SetEnergyAction extends AbstractGameAction {
    public SetEnergyAction(int energy) {
        amount = energy;
    }

    @Override
    public void update() {
        EnergyPanel.setEnergy(amount);
        isDone = true;
    }
}
