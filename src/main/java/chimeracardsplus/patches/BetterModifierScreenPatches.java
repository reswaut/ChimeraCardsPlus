package chimeracardsplus.patches;

import CardAugments.cardmods.AbstractAugment.AugmentRarity;
import CardAugments.screens.ModifierScreen;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.helpers.Constants;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.lib.Matcher.FieldAccessMatcher;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard.CardColor;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.compendium.CardLibraryScreen;
import com.megacrit.cardcrawl.screens.options.DropdownMenu;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.util.*;

public class BetterModifierScreenPatches {
    private static final String ID = ChimeraCardsPlus.makeID(BetterModifierScreenPatches.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] EXTRA_TEXT = uiStrings.EXTRA_TEXT;
    private static final List<String> modIDs = new ArrayList<>(Constants.DEFAULT_LIST_SIZE);

    @SpirePatch(
            clz = ModifierScreen.class,
            method = "getModStrings"
    )
    public static class ModNamesInModDropdownPatch {
        public static void GetModNames(Collection<String> modNames) {
            List<String> tmpModIDs = new ArrayList<>(modNames.size());
            List<String> tmpModNames = new ArrayList<>(modNames.size());
            ArrayList<Integer> indices = new ArrayList<>(modNames.size());
            int modCount = 0;
            for (String s : modNames) {
                boolean shouldAdd = true;
                for (ModInfo info : Loader.MODINFOS) {
                    if (info.ID.equals(s)) {
                        tmpModNames.add(info.Name);
                        shouldAdd = false;
                        break;
                    }
                }
                if (shouldAdd) {
                    tmpModNames.add(s);
                }
                tmpModIDs.add(s);
                indices.add(modCount);
                modCount += 1;
            }
            indices.sort(Comparator.comparing(tmpModNames::get));
            modIDs.clear();
            modNames.clear();
            modNames.add(TEXT[0]);
            for (int i = 0; i < modCount; ++i) {
                int index = indices.get(i);
                modIDs.add(tmpModIDs.get(index));
                modNames.add(tmpModNames.get(index));
            }
        }

        @SpireInstrumentPatch
        public static ExprEditor Instrument() {
            return new ReplaceModNamesExpr();
        }

        private static class ReplaceModNamesExpr extends ExprEditor {
            @Override
            public void edit(MethodCall m) throws CannotCompileException {
                if (Collections.class.getName().equals(m.getClassName()) && "sort".equals(m.getMethodName())) {
                    m.replace("{ " + ModNamesInModDropdownPatch.class.getName() + ".GetModNames($1); }");
                }
            }
        }
    }

    @SpirePatch(
            clz = ModifierScreen.class,
            method = "changedSelectionTo"
    )
    public static class SetSelectedModIDPatch {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void Insert(ModifierScreen __instance, DropdownMenu dropdownMenu, int i, String s, @ByRef String[] ___selectedModID) {
            if (i == 0) {
                ___selectedModID[0] = null;
            } else {
                ___selectedModID[0] = modIDs.get(i - 1);
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new FieldAccessMatcher(ModifierScreen.class, "selectedModID");
                int[] tmp = LineFinder.findInOrder(ctBehavior, finalMatcher);
                return new int[]{tmp[0] + 1};
            }
        }
    }

    @SpirePatch(
            clz = ModifierScreen.class,
            method = "getAugmentStrings"
    )
    public static class FilterAllModsPatch {
        @SpireInstrumentPatch
        public static ExprEditor Instrument() {
            return new FilterAllModsExpr();
        }

        private static class FilterAllModsExpr extends ExprEditor {
            @Override
            public void edit(MethodCall m) throws CannotCompileException {
                if (String.class.getName().equals(m.getClassName()) && "equals".equals(m.getMethodName())) {
                    m.replace("{ $_ = $proceed($$) || (($1) == null); }");
                }
            }
        }
    }

    @SpirePatch(
            clz = AugmentRarity.class,
            method = "toString"
    )
    private static class LocalizeRarityPatch {
        @SpirePrefixPatch
        public static SpireReturn<String> Prefix(AugmentRarity __instance) {
            switch (__instance) {
                case COMMON:
                    return SpireReturn.Return(TEXT[1]);
                case UNCOMMON:
                    return SpireReturn.Return(TEXT[2]);
                case RARE:
                    return SpireReturn.Return(TEXT[3]);
                case SPECIAL:
                    return SpireReturn.Return(TEXT[4]);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(
            clz = ModifierScreen.class,
            method = "getCharacterStrings"
    )
    public static class LocalizeCharacterStringsPatch {
        @SpirePostfixPatch
        public static ArrayList<String> Postfix(ArrayList<String> __result, ModifierScreen __instance, HashMap<String, CardColor> ___colorMap) {
            ___colorMap.remove(__result.get(5));
            ___colorMap.put(CardLibraryScreen.TEXT[4], CardColor.COLORLESS);
            __result.set(5, CardLibraryScreen.TEXT[4]);

            ___colorMap.remove(__result.get(6));
            ___colorMap.put(CardLibraryScreen.TEXT[5], CardColor.CURSE);
            __result.set(6, CardLibraryScreen.TEXT[5]);
            return __result;
        }
    }
}
