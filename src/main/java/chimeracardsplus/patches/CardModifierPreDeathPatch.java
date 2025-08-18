package chimeracardsplus.patches;

import basemod.helpers.CardModifierManager;
import chimeracardsplus.interfaces.TriggerPreDeathMod;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.lib.Matcher.MethodCallMatcher;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import javassist.CannotCompileException;
import javassist.CtBehavior;

@SpirePatch(
        clz = AbstractPlayer.class,
        method = "damage"
)
public class CardModifierPreDeathPatch {
    @SpireInsertPatch(
            locator = Locator.class
    )
    public static SpireReturn<Void> Insert(AbstractPlayer __instance, DamageInfo info) {
        boolean updated = true;
        while (updated) {
            updated = false;
            for (AbstractCard card : __instance.masterDeck.group) {
                if (CardModifierManager.modifiers(card).stream().filter(mod -> mod instanceof TriggerPreDeathMod).anyMatch(mod -> ((TriggerPreDeathMod) mod).preDeath(card, __instance))) {
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
