package chimeracardsplus.patches;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import chimeracardsplus.helpers.Constants;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.lib.Matcher.MethodCallMatcher;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.Soul;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;
import java.util.Collection;

public class AbstractAugmentPlusPatches {
    public static void onManualDiscard(AbstractCard card) {
        for (AbstractCardModifier mod : CardModifierManager.modifiers(card)) {
            if (mod instanceof AbstractAugmentPlus) {
                ((AbstractAugmentPlus) mod).onManualDiscard(card);
            }
        }
    }

    public static void onRemoveFromMasterDeck(AbstractCard card) {
        for (AbstractCardModifier mod : CardModifierManager.modifiers(card)) {
            if (mod instanceof AbstractAugmentPlus) {
                ((AbstractAugmentPlus) mod).onRemoveFromMasterDeck(card);
            }
        }
    }

    public static void onMoveToDiscard(AbstractCard card) {
        for (AbstractCardModifier mod : CardModifierManager.modifiers(card)) {
            if (mod instanceof AbstractAugmentPlus) {
                ((AbstractAugmentPlus) mod).onMoveToDiscard(card);
            }
        }
    }

    @SpirePatch(
            clz = Soul.class,
            method = "obtain"
    )
    public static class CardModifierOnObtainPatch {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void Insert(Soul __instance, AbstractCard card) {
            Collection<AbstractAugmentPlus> augmentsToRemove = new ArrayList<>(Constants.DEFAULT_LIST_SIZE);
            for (AbstractCardModifier modifier : CardModifierManager.modifiers(card)) {
                if (!(modifier instanceof AbstractAugmentPlus)) {
                    continue;
                }
                AbstractAugmentPlus augmentPlus = (AbstractAugmentPlus) modifier;
                if (augmentPlus.onObtain(card)) {
                    augmentsToRemove.add(augmentPlus);
                }
            }
            for (AbstractAugmentPlus augmentPlus : augmentsToRemove) {
                CardModifierManager.removeSpecificModifier(card, augmentPlus, true);
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

    @SpirePatch(
            clz = AbstractRoom.class,
            method = "updateObjects"
    )
    public static class CardModifierOnUpdateObjectsPatch {
        @SpirePostfixPatch
        public static void Postfix(AbstractRoom __instance) {
            boolean updated = true;
            while (updated) {
                updated = false;
                for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
                    for (AbstractCardModifier mod : CardModifierManager.modifiers(card)) {
                        if (!(mod instanceof AbstractAugmentPlus)) {
                            continue;
                        }
                        AbstractAugmentPlus augmentPlus = (AbstractAugmentPlus) mod;
                        if (augmentPlus.onRoomUpdateObjects(card)) {
                            updated = true;
                            break;
                        }
                    }
                    if (updated) {
                        break;
                    }
                }
            }
        }
    }

    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "damage"
    )
    public static class CardModifierPreDeathPatch {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static SpireReturn<Void> Insert(AbstractPlayer __instance, DamageInfo info) {
            boolean updated = true;
            while (updated) {
                updated = false;
                for (AbstractCard card : __instance.masterDeck.group) {
                    for (AbstractCardModifier mod : CardModifierManager.modifiers(card)) {
                        if (!(mod instanceof AbstractAugmentPlus)) {
                            continue;
                        }
                        AbstractAugmentPlus augmentPlus = (AbstractAugmentPlus) mod;
                        if (augmentPlus.preDeath(card)) {
                            updated = true;
                            break;
                        }
                    }
                    if (updated) {
                        break;
                    }
                }
                if (__instance.currentHealth > 0) {
                    return SpireReturn.Return();
                }
            }
            return SpireReturn.Continue();
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new MethodCallMatcher(AbstractPlayer.class, "hasPotion");
                return LineFinder.findInOrder(ctBehavior, finalMatcher);
            }
        }
    }
}