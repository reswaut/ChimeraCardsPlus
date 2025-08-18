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
import com.megacrit.cardcrawl.powers.MinionPower;
import com.megacrit.cardcrawl.powers.UnawakenedPower;
import com.megacrit.cardcrawl.relics.AbstractRelic.RelicTier;

public class StarDamage extends AbstractDamageModifier {
    public StarDamage() {
        priority = 32767;
    }

    @Override
    public void onLastDamageTakenUpdate(DamageInfo info, int lastDamageTaken, int overkillAmount, AbstractCreature target) {
        if (target instanceof AbstractMonster && DamageModifierManager.getInstigator(info) instanceof AbstractCard
                && target.currentHealth > 0
                && target.currentHealth - lastDamageTaken <= 0
                && !target.halfDead
                && !target.hasPower(MinionPower.POWER_ID)
                && !target.hasPower(UnawakenedPower.POWER_ID)) {
            if (!AbstractDungeon.getCurrRoom().eliteTrigger) {
                return;
            }
            if (Settings.isEndless && AbstractDungeon.player.hasBlight("MimicInfestation")) {
                return;
            }
            int roll = AbstractDungeon.relicRng.random(0, 99);
            if (ModHelper.isModEnabled("Elite Swarm")) {
                roll += 10;
            }
            RelicTier tier = RelicTier.COMMON;
            if (roll >= 50) {
                tier = roll > 82 ? RelicTier.RARE : RelicTier.UNCOMMON;
            }
            AbstractDungeon.getCurrRoom().addNoncampRelicToRewards(tier);
        }
    }

    @Override
    public boolean isInherent() {
        return true;
    }

    @Override
    public AbstractDamageModifier makeCopy() {
        return new StarDamage();
    }
}
