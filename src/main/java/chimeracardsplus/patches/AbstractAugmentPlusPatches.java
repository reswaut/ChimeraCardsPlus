package chimeracardsplus.patches;

import basemod.ReflectionHacks;
import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import chimeracardsplus.helpers.Constants;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.lib.Matcher.MethodCallMatcher;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.shrines.NoteForYourself;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.FastCardObtainEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
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

    public static void onObtainCard(AbstractCard card) {
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


    public static class CardModifierOnObtainPatch {
        @SpirePatch(
                clz = NoteForYourself.class,
                method = "buttonEffect"
        )
        public static class NoteForYourselfPatch {
            @SpireInsertPatch(
                    locator = Locator.class
            )
            public static void Insert(NoteForYourself __instance) {
                AbstractCard obtainCard = ReflectionHacks.getPrivate(__instance, NoteForYourself.class, "obtainCard");
                onObtainCard(obtainCard);
            }
        }

        @SpirePatch(
                clz = ShowCardAndObtainEffect.class,
                method = "update"
        )
        public static class ShowCardAndObtainEffectPatch {
            @SpireInsertPatch(
                    locator = Locator.class
            )
            public static void Insert(ShowCardAndObtainEffect __instance) {
                AbstractCard card = ReflectionHacks.getPrivate(__instance, ShowCardAndObtainEffect.class, "card");
                onObtainCard(card);
            }
        }

        @SpirePatch(
                clz = FastCardObtainEffect.class,
                method = "update"
        )
        public static class FastCardObtainEffectPatch {
            @SpireInsertPatch(
                    locator = Locator.class
            )
            public static void Insert(FastCardObtainEffect __instance) {
                AbstractCard card = ReflectionHacks.getPrivate(__instance, FastCardObtainEffect.class, "card");
                onObtainCard(card);
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new MethodCallMatcher(AbstractRelic.class, "onObtainCard");
                int[] tmp = LineFinder.findInOrder(ctBehavior, finalMatcher);
                return new int[]{tmp[0] + 1};
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


}