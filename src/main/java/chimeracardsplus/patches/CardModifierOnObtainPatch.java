package chimeracardsplus.patches;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import chimeracardsplus.interfaces.TriggerOnObtainMod;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.Soul;
import javassist.CtBehavior;

public class CardModifierOnObtainPatch {
    @SpirePatch(
            clz = Soul.class,
            method = "obtain"
    )
    public static class PostTriggerOnManualDiscardHook {
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
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(Soul.class, "setSharedVariables");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}
