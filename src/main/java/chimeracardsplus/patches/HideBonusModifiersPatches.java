package chimeracardsplus.patches;

import CardAugments.cardmods.AbstractAugment;
import CardAugments.screens.ModifierScreen;
import basemod.ReflectionHacks;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import chimeracardsplus.cardmods.AbstractAugmentPlus.AugmentBonusLevel;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.lib.Matcher.MethodCallMatcher;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.Settings;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;
import java.util.Map;

@SpirePatch(
        clz = ModifierScreen.class,
        method = "getAugmentStrings"
)
public class HideBonusModifiersPatches {
    @SpireInsertPatch(locator = Locator.class, localvars = "ret")
    public static void Insert(ModifierScreen __instance, @ByRef ArrayList<String>[] ret) {
        if (Settings.isDebug) {
            return;
        }
        Map<String, AbstractAugment> augmentMap = ReflectionHacks.getPrivate(__instance, ModifierScreen.class, "augmentMap");
        ArrayList<String> tmp = new ArrayList<>(512);
        for (String s : ret[0]) {
            AbstractAugment augment = augmentMap.get(s);
            if (!(augment instanceof AbstractAugmentPlus)) {
                tmp.add(s);
                continue;
            }
            AbstractAugmentPlus augmentPlus = (AbstractAugmentPlus) augment;
            if (augmentPlus.getModBonusLevel() != AugmentBonusLevel.BONUS) {
                tmp.add(s);
            }
        }
        ret[0] = tmp;
    }

    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctBehavior) throws CannotCompileException, PatchingException {
            Matcher finalMatcher = new MethodCallMatcher(ArrayList.class, "sort");
            return LineFinder.findInOrder(ctBehavior, finalMatcher);
        }
    }
}
