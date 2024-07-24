package chimeracardsplus.patches;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import chimeracardsplus.interfaces.TriggerPreDeathMod;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import javassist.CtBehavior;

public class CardModifierPreDeathPatch {
    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "damage"
    )
    public static class PreDeathHook {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static SpireReturn<Void> Insert(AbstractPlayer __instance, DamageInfo info) {
            boolean updated = true;
            while (updated) {
                updated = false;
                for (AbstractCard card : __instance.masterDeck.group) {
                    for (AbstractCardModifier mod : CardModifierManager.modifiers(card)) {
                        if (mod instanceof TriggerPreDeathMod) {
                            if (((TriggerPreDeathMod) mod).preDeath(card, __instance)) {
                                updated = true;
                                break;
                            }
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
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "hasPotion");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}
