package chimeracardsplus.damagemods;

import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class FeedDamage extends CardModifierDamageModifier {
    private final int maxHp;

    public FeedDamage(int maxHp) {
        this.maxHp = maxHp;
    }

    @Override
    public void onLastDamageTakenUpdate(DamageInfo info, int lastDamageTaken, int overkillAmount, AbstractCreature target) {
        if (!dealtFatalDamage(info, lastDamageTaken, overkillAmount, target)) {
            return;
        }
        AbstractDungeon.player.increaseMaxHp(maxHp, false);
    }

    @Override
    public AbstractDamageModifier makeCopy() {
        return new FeedDamage(maxHp);
    }
}
