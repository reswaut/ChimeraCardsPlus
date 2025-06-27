package chimeracardsplus.patches;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import chimeracardsplus.interfaces.TriggerOnPurgeMod;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.relics.PandorasBox;
import javassist.CtBehavior;

import java.util.Iterator;

public class CardModifierOnPurgePatch {
    @SpirePatch(
            clz = AbstractCard.class,
            method = "onRemoveFromMasterDeck"
    )
    public static class PostRemoveFromMasterDeckHook {
        public static void Postfix(AbstractCard __instance) {
            for (AbstractCardModifier mod : CardModifierManager.modifiers(__instance)) {
                if (mod instanceof TriggerOnPurgeMod) {
                    ((TriggerOnPurgeMod) mod).onRemoveFromMasterDeck(__instance);
                }
            }
        }
    }

    @SpirePatch(
            clz = PandorasBox.class,
            method = "onEquip"
    )
    public static class BetterPandorasBoxOnEquip {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"e"}
        )
        public static void Insert2(PandorasBox __instance, AbstractCard e) {
            for (AbstractCardModifier mod : CardModifierManager.modifiers(e)) {
                if (mod instanceof TriggerOnPurgeMod) {
                    ((TriggerOnPurgeMod) mod).onRemoveFromMasterDeck(e);
                }
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(Iterator.class, "remove");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}
