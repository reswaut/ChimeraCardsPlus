package chimeracardsplus.patches;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.helpers.TooltipInfo;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.helpers.SpecialNamingRules.SpecialName;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.lib.Matcher.NewExprMatcher;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.List;
import java.util.Map;

public class SpecialNamingPatches {
    @SpirePatch(
            clz = AbstractAugment.class,
            method = SpirePatch.CLASS
    )
    public static class SpecialNamingTooltipField {
        public static final SpireField<TooltipInfo> specialNamingTooltip = new SpireField<>(() -> null);
    }

    @SpirePatch(
            clz = AbstractAugment.class,
            method = "modifyName"
    )
    public static class ModifyNamesPatch {
        @SpirePrefixPatch
        public static SpireReturn<String> Prefix(AbstractAugment __instance, String cardName, AbstractCard card) {
            if (!cardName.isEmpty() && !ChimeraCardsPlus.configs.enableModifyNames()) {
                return SpireReturn.Return(cardName);
            }
            if (!ChimeraCardsPlus.configs.enableSpecialNaming()) {
                return SpireReturn.Continue();
            }
            if (ChimeraCardsPlus.specialNamingRules.NAME_DICT == null) {
                return SpireReturn.Continue();
            }
            Map<String, SpecialName> rules = ChimeraCardsPlus.specialNamingRules.NAME_DICT.get(__instance.identifier(card));
            if (rules == null) {
                return SpireReturn.Continue();
            }
            String[] nameParts = AbstractAugment.removeUpgradeText(cardName);
            SpecialName specialName = rules.get(nameParts[0]);
            if (specialName == null) {
                return SpireReturn.Continue();
            }
            String replacedName = specialName.NAME;
            if (replacedName == null) {
                return SpireReturn.Continue();
            }
            if (CardAugmentsMod.enableTooltips && specialName.DESCRIPTION != null && !specialName.DESCRIPTION.isEmpty()) {
                SpecialNamingTooltipField.specialNamingTooltip.set(__instance, new TooltipInfo(replacedName, specialName.DESCRIPTION));
            }
            return SpireReturn.Return(replacedName + nameParts[1]);
        }
    }

    @SpirePatch(
            clz = AbstractAugment.class,
            method = "additionalTooltips"
    )
    public static class SpecialNamingTooltipPatches {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = "tips"
        )
        public static void Insert(AbstractAugment __instance, AbstractCard card, List<TooltipInfo> tips) {
            TooltipInfo tooltip = SpecialNamingTooltipField.specialNamingTooltip.get(__instance);
            if (tooltip != null) {
                tips.add(tooltip);
            }
        }

        public static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new NewExprMatcher(TooltipInfo.class);
                int[] tmp = LineFinder.findInOrder(ctBehavior, finalMatcher);
                return new int[]{tmp[0] + 1};
            }
        }
    }
}
