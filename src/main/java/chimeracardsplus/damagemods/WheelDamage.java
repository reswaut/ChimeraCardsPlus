package chimeracardsplus.damagemods;

import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.AbstractMonster.EnemyType;

public class WheelDamage extends CardModifierDamageModifier {
    @Override
    public void onLastDamageTakenUpdate(DamageInfo info, int lastDamageTaken, int overkillAmount, AbstractCreature target) {
        if (!dealtFatalDamage(info, lastDamageTaken, overkillAmount, target)) {
            return;
        }
        if (AbstractDungeon.getCurrRoom().eliteTrigger) {
            return;
        }
        for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            if (m.type == EnemyType.BOSS) {
                return;
            }
        }
        AbstractDungeon.getCurrRoom().addCardToRewards();
    }

    @Override
    public AbstractDamageModifier makeCopy() {
        return new WheelDamage();
    }
}
