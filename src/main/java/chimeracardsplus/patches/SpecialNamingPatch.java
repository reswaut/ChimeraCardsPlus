package chimeracardsplus.patches;

import CardAugments.cardmods.AbstractAugment;
import chimeracardsplus.ChimeraCardsPlus;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.Map;

public class SpecialNamingPatch {
    @SpirePatch(
            clz = AbstractAugment.class,
            method = "modifyName"
    )
    public static class SpecialNamePatch {
        @SpirePrefixPatch
        public static SpireReturn<String> Prefix(AbstractAugment __instance, String cardName, AbstractCard card) {
            if (!ChimeraCardsPlus.enableSpecialNaming()) {
                return SpireReturn.Continue();
            }
            if (ChimeraCardsPlus.specialNamingRules == null || ChimeraCardsPlus.specialNamingRules.NAME_DICT == null) {
                return SpireReturn.Continue();
            }
            Map<String, String> rules = ChimeraCardsPlus.specialNamingRules.NAME_DICT.get(__instance.getClass().getSimpleName());
            if (rules == null) {
                return SpireReturn.Continue();
            }
            String[] nameParts = AbstractAugment.removeUpgradeText(cardName);
            String replacedName = rules.get(nameParts[0]);
            if (replacedName == null) {
                return SpireReturn.Continue();
            }
            return SpireReturn.Return(replacedName + nameParts[1]);
        }
    }
}
