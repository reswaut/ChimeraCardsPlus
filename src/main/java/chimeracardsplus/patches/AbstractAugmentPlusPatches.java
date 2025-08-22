package chimeracardsplus.patches;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.lib.Matcher.MethodCallMatcher;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.Soul;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.PandorasBox;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import javassist.*;
import org.clapper.util.classutil.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class AbstractAugmentPlusPatches {
    @SpirePatch(
            clz = Soul.class,
            method = "obtain"
    )
    public static class CardModifierOnObtainPatch {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void Insert(Soul __instance, AbstractCard card) {
            Collection<AbstractAugmentPlus> augmentsToRemove = new ArrayList<>(4);
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

    public static class CardModifierOnDiscardPatch {
        public static void doPatch(ClassFinder finder, ClassPool pool) throws NotFoundException, CannotCompileException {
            ChimeraCardsPlus.logger.info("- Trigger on Manual Discard Patch:");
            ClassFilter filter = new AndClassFilter(new NotClassFilter(new InterfaceOnlyClassFilter()), new SubclassClassFilter(AbstractCard.class));
            ArrayList<ClassInfo> clzList = new ArrayList<>(512);
            finder.findClasses(clzList, filter);
            ChimeraCardsPlus.logger.info("\t- Potential targets found ({}).", clzList.size());
            CtClass ctClass = pool.get(AbstractCard.class.getName());
            for (CtMethod m : ctClass.getDeclaredMethods()) {
                ManualDiscardTrigger(ctClass, m);
            }
            for (ClassInfo classInfo : clzList) {
                try {
                    ctClass = pool.get(classInfo.getClassName());
                    for (CtMethod m : ctClass.getDeclaredMethods()) {
                        ManualDiscardTrigger(ctClass, m);
                    }
                } catch (NotFoundException e) {
                    ChimeraCardsPlus.logger.error("\t\t- Class not found: {}", classInfo.getClassName());
                    e.printStackTrace();
                }
            }
            ChimeraCardsPlus.logger.info("- Trigger on Manual Discard patch complete.");
        }

        public static void ManualDiscardTrigger(CtClass ctClass, CtMethod method) throws CannotCompileException {
            if ("triggerOnManualDiscard".equals(method.getName())) {
                ChimeraCardsPlus.logger.info("- Patching {}", ctClass.getName());
                method.insertAfter(CardModifierOnDiscardPatch.class.getName() + ".Postfix(this);");
            }
        }

        public static void Postfix(AbstractCard __instance) {
            for (AbstractCardModifier mod : CardModifierManager.modifiers(__instance)) {
                if (mod instanceof AbstractAugmentPlus) {
                    ((AbstractAugmentPlus) mod).onManualDiscard(__instance);
                }
            }
        }
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = "onMoveToDiscard"
    )
    public static class CardModifierOnMoveToDiscardPatch {
        @SpirePostfixPatch
        public static void Postfix(AbstractCard __instance) {
            for (AbstractCardModifier mod : CardModifierManager.modifiers(__instance)) {
                if (mod instanceof AbstractAugmentPlus) {
                    ((AbstractAugmentPlus) mod).onMoveToDiscard(__instance);
                }
            }
        }
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = "onRemoveFromMasterDeck"
    )
    public static class CardModifierOnPurgePatch {
        @SpirePostfixPatch
        public static void Postfix(AbstractCard __instance) {
            for (AbstractCardModifier mod : CardModifierManager.modifiers(__instance)) {
                if (mod instanceof AbstractAugmentPlus) {
                    ((AbstractAugmentPlus) mod).onRemoveFromMasterDeck(__instance);
                }
            }
        }
    }

    @SpirePatch(
            clz = PandorasBox.class,
            method = "onEquip"
    )
    public static class BetterPandorasBoxOnEquip {
        @SpireInsertPatch(locator = Locator.class, localvars = "e")
        public static void Insert(PandorasBox __instance, AbstractCard e) {
            for (AbstractCardModifier mod : CardModifierManager.modifiers(e)) {
                if (mod instanceof AbstractAugmentPlus) {
                    ((AbstractAugmentPlus) mod).onRemoveFromMasterDeck(e);
                }
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new MethodCallMatcher(Iterator.class, "remove");
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
                    if (CardModifierManager.modifiers(card).stream().filter(mod -> mod instanceof AbstractAugmentPlus).anyMatch(mod -> ((AbstractAugmentPlus) mod).onRoomUpdateObjects(card))) {
                        updated = true;
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
                    if (CardModifierManager.modifiers(card).stream().filter(mod -> mod instanceof AbstractAugmentPlus).anyMatch(mod -> ((AbstractAugmentPlus) mod).preDeath(card))) {
                        updated = true;
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