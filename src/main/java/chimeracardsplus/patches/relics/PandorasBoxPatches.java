package chimeracardsplus.patches.relics;

import chimeracardsplus.ChimeraCardsPlus;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.lib.Matcher.MethodCallMatcher;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.relics.PandorasBox;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.Iterator;

@SpirePatch(
        clz = PandorasBox.class,
        method = "onEquip"
)
public class PandorasBoxPatches {
    @SpireInsertPatch(locator = Locator.class, localvars = "e")
    public static void Insert(PandorasBox __instance, AbstractCard e) {
        if (!ChimeraCardsPlus.configs.enableBaseGameFixes()) {
            return;
        }
        e.onRemoveFromMasterDeck();
    }

    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctBehavior) throws CannotCompileException, PatchingException {
            Matcher finalMatcher = new MethodCallMatcher(Iterator.class, "remove");
            return LineFinder.findInOrder(ctBehavior, finalMatcher);
        }
    }
}
