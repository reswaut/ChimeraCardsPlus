package chimeracardsplus.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;

public class SlyFieldPatches {
    @SpirePatch(
            clz = AbstractCard.class,
            method = SpirePatch.CLASS
    )
    public static class SlyFieldPatch {
        public static SpireField<Boolean> sly = new SpireField<>(() -> false);
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = "makeStatEquivalentCopy"
    )
    public static class CopySlyFieldPatch {
        @SpirePostfixPatch
        public static AbstractCard Postfix(AbstractCard __result, AbstractCard __instance) {
            SlyFieldPatch.sly.set(__result, SlyFieldPatch.sly.get(__instance));
            return __result;
        }
    }
}
