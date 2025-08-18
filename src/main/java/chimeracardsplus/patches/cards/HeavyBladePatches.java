package chimeracardsplus.patches.cards;

import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.CardModifierPatches.CardModifierOnApplyPowers;
import chimeracardsplus.ChimeraCardsPlus;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.cards.red.HeavyBlade;

@SpirePatch(
        clz = HeavyBlade.class,
        method = "applyPowers"
)
public class HeavyBladePatches {
    @SpirePrefixPatch
    public static void Prefix(HeavyBlade __instance) {
        if (!ChimeraCardsPlus.enableCardFixes()) {
            return;
        }
        CardModifierOnApplyPowers.Prefix(__instance);
    }
}
