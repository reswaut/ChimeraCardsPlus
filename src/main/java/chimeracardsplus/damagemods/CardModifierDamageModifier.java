package chimeracardsplus.damagemods;

import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.evacipated.cardcrawl.mod.stslib.damagemods.DamageModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.MinionPower;
import com.megacrit.cardcrawl.powers.RegrowPower;
import com.megacrit.cardcrawl.powers.UnawakenedPower;

public abstract class CardModifierDamageModifier extends AbstractDamageModifier {
    protected CardModifierDamageModifier() {
        priority = 32767;
    }

    public static boolean dealtUnblockedDamage(DamageInfo info, int lastDamageTaken, int overkillAmount, AbstractCreature target) {
        return target instanceof AbstractMonster && DamageModifierManager.getInstigator(info) instanceof AbstractCard && target.currentHealth > 0 && !target.halfDead && lastDamageTaken > 0;
    }

    public static boolean killedEnemy(DamageInfo info, int lastDamageTaken, int overkillAmount, AbstractCreature target) {
        return dealtUnblockedDamage(info, lastDamageTaken, overkillAmount, target) && target.currentHealth - lastDamageTaken <= 0;
    }

    public static boolean dealtFatalDamage(DamageInfo info, int lastDamageTaken, int overkillAmount, AbstractCreature target) {
        return killedEnemy(info, lastDamageTaken, overkillAmount, target) && !target.hasPower(MinionPower.POWER_ID) && !target.hasPower(UnawakenedPower.POWER_ID) && isOnlyLifeLinkLeft(target);
    }

    private static boolean isOnlyLifeLinkLeft(AbstractCreature targetHit) {
        return !targetHit.hasPower(RegrowPower.POWER_ID) || AbstractDungeon.getMonsters().monsters.stream().noneMatch(monster -> !monster.equals(targetHit) && !monster.halfDead && monster.hasPower(RegrowPower.POWER_ID));
    }

    @Override
    public boolean isInherent() {
        return true;
    }
}
