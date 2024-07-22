package chimeracardsplus.patches;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import chimeracardsplus.interfaces.TriggerOnDiscardMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;

public class CardModifierOnDiscardPatch {
    @SpirePatch(
            clz = AbstractCard.class,
            method = "triggerOnManualDiscard"
    )
    public static class PostTriggerOnManualDiscardHook {
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
