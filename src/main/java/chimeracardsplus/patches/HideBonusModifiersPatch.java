package chimeracardsplus.patches;

import CardAugments.cardmods.AbstractAugment;
import CardAugments.screens.ModifierScreen;
import basemod.ReflectionHacks;
import chimeracardsplus.interfaces.BonusMod;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.lib.Matcher.MethodCallMatcher;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.Settings;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

@SpirePatch(
        clz = ModifierScreen.class,
        method = "getAugmentStrings"
)
public class HideBonusModifiersPatch {
    @SpireInsertPatch(locator = Locator.class, localvars = "ret")
    public static void Insert(ModifierScreen __instance, @ByRef ArrayList<String>[] ret) {
        if (!Settings.isDebug) {
            Map<String, AbstractAugment> augmentMap = ReflectionHacks.getPrivate(__instance, ModifierScreen.class, "augmentMap");
            ret[0] = ret[0].stream().filter(s -> !(augmentMap.get(s) instanceof BonusMod)).collect(Collectors.toCollection(ArrayList::new));
        }
    }

    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctBehavior) throws CannotCompileException, PatchingException {
            Matcher finalMatcher = new MethodCallMatcher(ArrayList.class, "sort");
            return LineFinder.findInOrder(ctBehavior, finalMatcher);
        }
    }
}
