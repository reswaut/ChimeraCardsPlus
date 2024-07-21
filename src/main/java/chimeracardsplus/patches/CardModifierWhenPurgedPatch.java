package chimeracardsplus.patches;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.events.city.Vampires;
import com.megacrit.cardcrawl.relics.PandorasBox;
import javassist.CtBehavior;

import java.util.ArrayList;
import java.util.Iterator;

public class CardModifierWhenPurgedPatch {
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
            clz = CardGroup.class,
            method = "getPurgeableCards"
    )
    public static class IsRemovableHook {
        public static CardGroup Postfix(CardGroup __result, CardGroup __instance) {
            CardGroup realRet = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
            for (AbstractCard card : __result.group) {
                boolean removable = true;
                for (AbstractCardModifier mod : CardModifierManager.modifiers(card)) {
                    if (mod instanceof TriggerOnPurgeMod && !((TriggerOnPurgeMod) mod).isRemovable(card)) {
                        removable = false;
                        break;
                    }
                }
                if (removable) {
                    realRet.group.add(card);
                }
            }
            return realRet;
        }
    }

    @SpirePatch(
            clz = PandorasBox.class,
            method = "onEquip"
    )
    public static class BetterPandorasBoxOnEquip {
        @SpireInsertPatch(
                locator = Locator1.class,
                localvars = {"e"}
        )
        public static void Insert1(PandorasBox __instance, @ByRef AbstractCard[] e) {
            if (!e[0].hasTag(AbstractCard.CardTags.STARTER_DEFEND) && !e[0].hasTag(AbstractCard.CardTags.STARTER_STRIKE)) {
                return;
            }
            boolean removable = true;
            for (AbstractCardModifier mod : CardModifierManager.modifiers(e[0])) {
                if (mod instanceof TriggerOnPurgeMod && !((TriggerOnPurgeMod) mod).isRemovable(e[0])) {
                    removable = false;
                    break;
                }
            }
            if (removable) {
                return;
            }
            e[0] = e[0].makeCopy();
            e[0].tags.clear();
        }

        @SpireInsertPatch(
                locator = Locator2.class,
                localvars = {"e"}
        )
        public static void Insert2(PandorasBox __instance, AbstractCard e) {
            for (AbstractCardModifier mod : CardModifierManager.modifiers(e)) {
                if (mod instanceof TriggerOnPurgeMod) {
                    ((TriggerOnPurgeMod) mod).onRemoveFromMasterDeck(e);
                }
            }
        }

        private static class Locator1 extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractCard.class, "hasTag");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }

        private static class Locator2 extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(Iterator.class, "remove");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = Vampires.class,
            method = "replaceAttacks"
    )
    public static class betterVampiresReplaceAttacks {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"card"}
        )
        public static void Insert(Vampires __instance, @ByRef AbstractCard[] card) {
            if (!card[0].hasTag(AbstractCard.CardTags.STARTER_STRIKE)) {
                return;
            }
            boolean removable = true;
            for (AbstractCardModifier mod : CardModifierManager.modifiers(card[0])) {
                if (mod instanceof TriggerOnPurgeMod && !((TriggerOnPurgeMod) mod).isRemovable(card[0])) {
                    removable = false;
                    break;
                }
            }
            if (removable) {
                return;
            }
            card[0] = card[0].makeCopy();
            card[0].tags.clear();
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(ArrayList.class, "contains");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

}
