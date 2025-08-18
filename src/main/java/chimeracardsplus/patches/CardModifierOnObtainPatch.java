package chimeracardsplus.patches;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import chimeracardsplus.interfaces.TriggerOnObtainMod;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.lib.Matcher.MethodCallMatcher;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.Soul;
import javassist.CannotCompileException;
import javassist.CtBehavior;

@SpirePatch(
        clz = Soul.class,
        method = "obtain"
)
public class CardModifierOnObtainPatch {
    @SpireInsertPatch(
            locator = Locator.class
    )
    public static void Insert(Soul __instance, AbstractCard card) {
        for (AbstractCardModifier mod : CardModifierManager.modifiers(card)) {
            if (mod instanceof TriggerOnObtainMod) {
                ((TriggerOnObtainMod) mod).onObtain(card);
            }
        }
    }

    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctBehavior) throws CannotCompileException, PatchingException {
            Matcher finalMatcher = new MethodCallMatcher(Soul.class, "setSharedVariables");
            return LineFinder.findInOrder(ctBehavior, finalMatcher);
        }
    }
}
