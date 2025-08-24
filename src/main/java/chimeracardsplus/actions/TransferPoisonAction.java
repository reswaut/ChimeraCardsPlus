package chimeracardsplus.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.PoisonPower;

public class TransferPoisonAction extends AbstractGameAction {
    public TransferPoisonAction(AbstractCreature target) {
        this.target = target;
    }

    @Override
    public void update() {
        if (target.hasPower(PoisonPower.POWER_ID)) {
            AbstractCreature newTarget = AbstractDungeon.getMonsters().getRandomMonster((AbstractMonster) target, true, AbstractDungeon.cardRandomRng);
            if (newTarget != null && !target.equals(newTarget)) {
                AbstractPower poison = target.getPower(PoisonPower.POWER_ID);
                int poisonAmt = poison.amount;
                addToBot(new RemoveSpecificPowerAction(target, AbstractDungeon.player, poison));
                if (poisonAmt > 0) {
                    addToBot(new ApplyPowerAction(newTarget, AbstractDungeon.player, new PoisonPower(newTarget, AbstractDungeon.player, poisonAmt)));
                }
            }
        }
        isDone = true;
    }
}
