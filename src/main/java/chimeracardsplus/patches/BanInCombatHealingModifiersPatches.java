package chimeracardsplus.patches;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.cardmods.AbstractAugment.AugmentRarity;
import CardAugments.patches.OnCardGeneratedPatches.CreatedCards;
import CardAugments.patches.OnCardGeneratedPatches.DiscoveryStyleCards;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import chimeracardsplus.cardmods.AbstractAugmentPlus.AugmentBonusLevel;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.lib.Matcher.MethodCallMatcher;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

public class BanInCombatHealingModifiersPatches {
    private static boolean inCombatRoll = false;

    @SpirePatch(
            clz = DiscoveryStyleCards.class,
            method = "roll"
    )
    public static class DiscoverInCombatRollPatch {
        @SpirePrefixPatch
        public static void Prefix() {
            inCombatRoll = true;
        }

        @SpirePostfixPatch
        public static void Postfix() {
            inCombatRoll = false;
        }
    }

    @SpirePatch(
            clz = CreatedCards.class,
            method = "roll"
    )
    public static class TokenInCombatRollPatch {
        @SpirePrefixPatch
        public static void Prefix() {
            inCombatRoll = true;
        }

        @SpirePostfixPatch
        public static void Postfix() {
            inCombatRoll = false;
        }
    }

    @SpirePatch(
            clz = CardAugmentsMod.class,
            method = "applyWeightedCardMod"
    )
    public static class ApplyInCombatModPatch {
        @SpireInsertPatch(locator = Locator.class, localvars = "validMods")
        public static void Insert(AbstractCard c, AugmentRarity rarity, int index, @ByRef ArrayList<AbstractAugment>[] validMods) {
            if (inCombatRoll) {
                validMods[0] = AbstractAugmentPlus.filterModsByBonusLevel(validMods[0], AugmentBonusLevel.NORMAL);
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new MethodCallMatcher(ArrayList.class, "isEmpty");
                return LineFinder.findInOrder(ctBehavior, finalMatcher);
            }
        }
    }
}
