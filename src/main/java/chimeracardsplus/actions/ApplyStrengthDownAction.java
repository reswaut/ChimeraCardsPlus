package chimeracardsplus.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.GainStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class ApplyStrengthDownAction extends AbstractGameAction {
    public ApplyStrengthDownAction(AbstractCreature target, int amount) {
        this.target = target;
        this.amount = amount;
        actionType = ActionType.DEBUFF;
    }

    @Override
    public void update() {
        if (target != null) {
            if (!target.hasPower(ArtifactPower.POWER_ID)) {
                addToTop(new ApplyPowerAction(target, AbstractDungeon.player, new GainStrengthPower(target, amount), amount, true, AttackEffect.NONE));
            }
            addToTop(new ApplyPowerAction(target, AbstractDungeon.player, new StrengthPower(target, -amount), -amount, true, AttackEffect.NONE));
        }
        isDone = true;
    }
}
