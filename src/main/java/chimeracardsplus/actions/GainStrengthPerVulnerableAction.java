package chimeracardsplus.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;

public class GainStrengthPerVulnerableAction extends AbstractGameAction {
    public GainStrengthPerVulnerableAction(AbstractCreature target, AbstractCreature source) {
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
        addToTop(new ApplyPowerAction(source, source, new StrengthPower(source, powerAmount), powerAmount));
    }
}
