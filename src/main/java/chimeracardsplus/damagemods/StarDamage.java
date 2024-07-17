package chimeracardsplus.damagemods;

import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.evacipated.cardcrawl.mod.stslib.damagemods.DamageModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class StarDamage extends AbstractDamageModifier {
    public StarDamage() {
        this.priority = 32767;
    }

    public void onLastDamageTakenUpdate(DamageInfo info, int lastDamageTaken, int overkillAmount, AbstractCreature targetHit) {
        if (DamageModifierManager.getInstigator(info) instanceof AbstractCard
                && targetHit.currentHealth > 0
                && targetHit.currentHealth - lastDamageTaken <= 0) {
            if (!(targetHit instanceof AbstractMonster)) {
                return;
            }
            if (((AbstractMonster)targetHit).type != AbstractMonster.EnemyType.ELITE) {
                return;
            }
            if (Settings.isEndless && AbstractDungeon.player.hasBlight("MimicInfestation")) {
                return;
            }
            int roll = AbstractDungeon.relicRng.random(0, 99);
            if (ModHelper.isModEnabled("Elite Swarm")) {
                roll += 10;
            }
            AbstractRelic.RelicTier tier = AbstractRelic.RelicTier.COMMON;
            if (roll >= 50) {
                tier = (roll > 82 ? AbstractRelic.RelicTier.RARE : AbstractRelic.RelicTier.UNCOMMON);
            }
            AbstractDungeon.getCurrRoom().addNoncampRelicToRewards(tier);
        }
    }

    public boolean isInherent() {
        return true;
    }

    public AbstractDamageModifier makeCopy() {
        return new StarDamage();
    }
}
