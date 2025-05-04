package chimeracardsplus.patches;

import CardAugments.cardmods.AbstractAugment;
import CardAugments.screens.ModifierScreen;
import basemod.ReflectionHacks;
import chimeracardsplus.interfaces.BonusMod;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.Settings;
import javassist.CtBehavior;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public class HideBonusModifiersPatch {
    @SpirePatch(
            clz = ModifierScreen.class,
            method = "getAugmentStrings"
    )
    public static class IsHealingModPatch {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"ret"}
        )
        public static void Insert(ModifierScreen __instance, @ByRef ArrayList<String>[] ret) {
            if (!Settings.isDebug) {
                HashMap<String, AbstractAugment> augmentMap = ReflectionHacks.getPrivate(__instance, ModifierScreen.class, "augmentMap");
                ret[0] = ret[0].stream().filter((s) -> !(augmentMap.get(s) instanceof BonusMod)).collect(Collectors.toCollection(ArrayList::new));
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(ArrayList.class, "sort");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}
