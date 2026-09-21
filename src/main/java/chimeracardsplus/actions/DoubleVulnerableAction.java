package chimeracardsplus.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;

public class DoubleVulnerableAction extends AbstractGameAction {
    public DoubleVulnerableAction(AbstractCreature target, AbstractCreature source) {
        this.target = target;
        this.source = source;
    }

    @Override
    public void update() {
        isDone = true;
        AbstractPower power = target.getPower(VulnerablePower.POWER_ID);
        if (power == null) {
            return;
        }
        int powerAmount = power.amount;
        if (powerAmount <= 0) {
            return;
        }
        addToTop(new ApplyPowerAction(target, source, new VulnerablePower(target, powerAmount, false)));
    }
}
