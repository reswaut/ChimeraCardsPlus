package chimeracardsplus.damagemods;

import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.evacipated.cardcrawl.mod.stslib.damagemods.DamageModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class FeedDamage extends AbstractDamageModifier {
    private final int maxHp;

    public FeedDamage(int maxHp) {
        this.priority = 32767;
        this.maxHp = maxHp;
    }

    public void onLastDamageTakenUpdate(DamageInfo info, int lastDamageTaken, int overkillAmount, AbstractCreature targetHit) {
        if (DamageModifierManager.getInstigator(info) instanceof AbstractCard
                && targetHit.currentHealth > 0
                && targetHit.currentHealth - lastDamageTaken <= 0
                && !targetHit.halfDead && !targetHit.hasPower("Minion")) {
            AbstractDungeon.player.increaseMaxHp(this.maxHp, false);
        }
    }

    public boolean isInherent() {
        return true;
    }

    public AbstractDamageModifier makeCopy() {
        return new chimeracardsplus.damagemods.FeedDamage(this.maxHp);
    }
}
