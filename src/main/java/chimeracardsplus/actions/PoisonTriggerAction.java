package chimeracardsplus.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.PoisonPower;

public class PoisonTriggerAction extends AbstractGameAction {
    public PoisonTriggerAction(AbstractCreature target) {
        this.target = target;
    }

    @Override
    public void update() {
        if (target.hasPower(PoisonPower.POWER_ID)) {
            target.getPower(PoisonPower.POWER_ID).atStartOfTurn();
        }
        isDone = true;
    }
}
