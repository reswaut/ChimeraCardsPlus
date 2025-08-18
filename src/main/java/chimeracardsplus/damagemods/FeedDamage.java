package chimeracardsplus.damagemods;

import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.evacipated.cardcrawl.mod.stslib.damagemods.DamageModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.MinionPower;
import com.megacrit.cardcrawl.powers.UnawakenedPower;

public class FeedDamage extends AbstractDamageModifier {
    private final int maxHp;

    public FeedDamage(int maxHp) {
        priority = 32767;
        this.maxHp = maxHp;
    }

    @Override
    public void onLastDamageTakenUpdate(DamageInfo info, int lastDamageTaken, int overkillAmount, AbstractCreature target) {
        if (target instanceof AbstractMonster && DamageModifierManager.getInstigator(info) instanceof AbstractCard
                && target.currentHealth > 0
                && target.currentHealth - lastDamageTaken <= 0
                && !target.halfDead
                && !target.hasPower(MinionPower.POWER_ID)
                && !target.hasPower(UnawakenedPower.POWER_ID)) {
            AbstractDungeon.player.increaseMaxHp(maxHp, false);
        }
    }

    @Override
    public boolean isInherent() {
        return true;
    }

    @Override
    public AbstractDamageModifier makeCopy() {
        return new FeedDamage(maxHp);
    }
}
