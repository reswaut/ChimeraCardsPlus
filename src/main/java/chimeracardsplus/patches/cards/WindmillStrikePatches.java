package chimeracardsplus.patches.cards;

import chimeracardsplus.ChimeraCardsPlus;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.purple.WindmillStrike;

@SpirePatch(
        clz = WindmillStrike.class,
        method = "onRetained"
)
public class WindmillStrikePatches {
    @SpirePrefixPatch
    public static SpireReturn<Void> Prefix(WindmillStrike __instance) {
        if (!ChimeraCardsPlus.enableBaseGameFixes()) {
            return SpireReturn.Continue();
        }
        __instance.baseDamage += __instance.magicNumber;
        __instance.upgradedDamage = true;
        return SpireReturn.Return();
    }
}
