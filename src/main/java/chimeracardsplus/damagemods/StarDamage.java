package chimeracardsplus.damagemods;

import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.megacrit.cardcrawl.blights.MimicInfestation;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.daily.mods.BigGameHunter;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic.RelicTier;

public class StarDamage extends CardModifierDamageModifier {
    @Override
    public void onLastDamageTakenUpdate(DamageInfo info, int lastDamageTaken, int overkillAmount, AbstractCreature target) {
        if (!dealtFatalDamage(info, lastDamageTaken, overkillAmount, target)) {
            return;
        }
        if (!AbstractDungeon.getCurrRoom().eliteTrigger) {
            return;
        }
        if (Settings.isEndless && AbstractDungeon.player.hasBlight(MimicInfestation.ID)) {
            return;
        }
        int roll = AbstractDungeon.relicRng.random(0, 99);
        if (ModHelper.isModEnabled(BigGameHunter.ID)) {
            roll += 10;
        }
        RelicTier tier = RelicTier.COMMON;
        if (roll >= 50) {
            tier = roll > 82 ? RelicTier.RARE : RelicTier.UNCOMMON;
        }
        AbstractDungeon.getCurrRoom().addNoncampRelicToRewards(tier);
    }

    @Override
    public AbstractDamageModifier makeCopy() {
        return new StarDamage();
    }
}
