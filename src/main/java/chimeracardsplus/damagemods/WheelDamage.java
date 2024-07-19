package chimeracardsplus.damagemods;

import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.evacipated.cardcrawl.mod.stslib.damagemods.DamageModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class WheelDamage extends AbstractDamageModifier {
    public WheelDamage() {
        this.priority = 32767;
    }

    public void onLastDamageTakenUpdate(DamageInfo info, int lastDamageTaken, int overkillAmount, AbstractCreature targetHit) {
        if (AbstractDungeon.getCurrRoom().eliteTrigger) {
            return;
        }
        for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            if (m.type == AbstractMonster.EnemyType.BOSS) {
                return;
            }
        }
        if (DamageModifierManager.getInstigator(info) instanceof AbstractCard
                && targetHit.currentHealth > 0
                && targetHit.currentHealth - lastDamageTaken <= 0) {
            AbstractDungeon.getCurrRoom().addCardToRewards();
        }
    }

    public boolean isInherent() {
        return true;
    }

    public AbstractDamageModifier makeCopy() {
        return new WheelDamage();
    }
}
