package chimeracardsplus.damagemods;

import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.evacipated.cardcrawl.mod.stslib.damagemods.DamageModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.AbstractMonster.EnemyType;
import com.megacrit.cardcrawl.powers.MinionPower;
import com.megacrit.cardcrawl.powers.UnawakenedPower;

public class WheelDamage extends AbstractDamageModifier {
    public WheelDamage() {
        priority = 32767;
    }

    @Override
    public void onLastDamageTakenUpdate(DamageInfo info, int lastDamageTaken, int overkillAmount, AbstractCreature target) {
        if (AbstractDungeon.getCurrRoom().eliteTrigger) {
            return;
        }
        for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            if (m.type == EnemyType.BOSS) {
                return;
            }
        }
        if (target instanceof AbstractMonster && DamageModifierManager.getInstigator(info) instanceof AbstractCard
                && target.currentHealth > 0
                && target.currentHealth - lastDamageTaken <= 0
                && !target.halfDead
                && !target.hasPower(MinionPower.POWER_ID)
                && !target.hasPower(UnawakenedPower.POWER_ID)) {
            AbstractDungeon.getCurrRoom().addCardToRewards();
        }
    }

    @Override
    public boolean isInherent() {
        return true;
    }

    @Override
    public AbstractDamageModifier makeCopy() {
        return new WheelDamage();
    }
}
