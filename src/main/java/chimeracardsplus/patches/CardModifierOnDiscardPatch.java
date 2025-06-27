package chimeracardsplus.patches;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.interfaces.TriggerOnDiscardMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import javassist.*;
import org.clapper.util.classutil.*;

import java.util.ArrayList;

public class CardModifierOnDiscardPatch {
    public static class PostTriggerOnManualDiscardHook {
        public static void patch(ClassFinder finder, ClassPool pool) throws NotFoundException, CannotCompileException {
            ChimeraCardsPlus.logger.info("- Trigger on Manual Discard Patch:");
            AndClassFilter filter = new AndClassFilter(new NotClassFilter(new InterfaceOnlyClassFilter()), new SubclassClassFilter(AbstractCard.class));
            ArrayList<ClassInfo> clzList = new ArrayList<>();
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
            if (method.getName().equals("triggerOnManualDiscard")) {
                ChimeraCardsPlus.logger.info("- Patching {}", ctClass.getName());
                method.insertAfter(PostTriggerOnManualDiscardHook.class.getName() + ".Postfix(this);");
            }
        }

        public static void Postfix(AbstractCard __instance) {
            for (AbstractCardModifier mod : CardModifierManager.modifiers(__instance)) {
                if (mod instanceof TriggerOnDiscardMod) {
                    ((TriggerOnDiscardMod) mod).onManualDiscard(__instance);
                }
            }
        }
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = "onMoveToDiscard"
    )
    public static class PostOnMoveToDisCardHook {
        public static void Postfix(AbstractCard __instance) {
            for (AbstractCardModifier mod : CardModifierManager.modifiers(__instance)) {
                if (mod instanceof TriggerOnDiscardMod) {
                    ((TriggerOnDiscardMod) mod).onMoveToDiscard(__instance);
                }
            }
        }
    }
}
