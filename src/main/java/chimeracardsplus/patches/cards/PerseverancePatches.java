package chimeracardsplus.patches.cards;

import chimeracardsplus.ChimeraCardsPlus;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.purple.Perseverance;

@SpirePatch(
        clz = Perseverance.class,
        method = "onRetained"
)
public class PerseverancePatches {
    @SpirePrefixPatch
    public static SpireReturn<Void> Prefix(Perseverance __instance) {
        if (!ChimeraCardsPlus.configs.enableBaseGameFixes()) {
            return SpireReturn.Continue();
        }
        __instance.baseBlock += __instance.magicNumber;
        __instance.upgradedBlock = true;
        return SpireReturn.Return();
    }
}
