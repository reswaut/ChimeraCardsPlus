package chimeracardsplus.patches;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import chimeracardsplus.ChimeraCardsPlus;
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
import javassist.*;
import org.clapper.util.classutil.*;

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

    public static class DynamicAbstractCardPatches {
        public static void doPatches(ClassFinder finder, ClassPool pool) throws NotFoundException, CannotCompileException {
            ChimeraCardsPlus.logger.info("- Dynamic AbstractCard patches started.");
            ClassFilter filter = new AndClassFilter(new NotClassFilter(new InterfaceOnlyClassFilter()), new SubclassClassFilter(AbstractCard.class));
            ArrayList<ClassInfo> clzList = new ArrayList<>(Constants.EXPECTED_CARDS);
            finder.findClasses(clzList, filter);
            ChimeraCardsPlus.logger.info("- Potential targets found ({}).", clzList.size());
            CtClass ctClass = pool.get(AbstractCard.class.getName());
            for (CtMethod m : ctClass.getDeclaredMethods()) {
                checkMethodPatches(ctClass, m);
            }
            for (ClassInfo classInfo : clzList) {
                try {
                    ctClass = pool.get(classInfo.getClassName());
                    for (CtMethod m : ctClass.getDeclaredMethods()) {
                        checkMethodPatches(ctClass, m);
                    }
                } catch (NotFoundException e) {
                    ChimeraCardsPlus.logger.error("- Class not found: {}", classInfo.getClassName(), e);
                }
            }
            ChimeraCardsPlus.logger.info("- Dynamic AbstractCard patches complete.");
        }

        private static void checkMethodPatches(CtClass ctClass, CtMethod method) throws CannotCompileException {
            if ("triggerOnManualDiscard".equals(method.getName())) {
                ChimeraCardsPlus.logger.info("- Patching {}.triggerOnManualDiscard", ctClass.getName());
                method.insertBefore(AbstractAugmentPlusPatches.class.getName() + ".onManualDiscard(this);");
            } else if ("onMoveToDiscard".equals(method.getName())) {
                ChimeraCardsPlus.logger.info("- Patching {}.onMoveToDiscard", ctClass.getName());
                method.insertBefore(AbstractAugmentPlusPatches.class.getName() + ".onMoveToDiscard(this);");
            } else if ("onRemoveFromMasterDeck".equals(method.getName())) {
                ChimeraCardsPlus.logger.info("- Patching {}.onRemoveFromMasterDeck", ctClass.getName());
                method.insertBefore(AbstractAugmentPlusPatches.class.getName() + ".onRemoveFromMasterDeck(this);");
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