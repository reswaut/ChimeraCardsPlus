package chimeracardsplus.patches;

import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.rewards.AbstractModificationReward;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.lib.Matcher.MethodCallMatcher;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;
import com.megacrit.cardcrawl.ui.buttons.ProceedButton;
import javassist.CannotCompileException;
import javassist.CtBehavior;

public class AddModificationRewardsPatches {
    @SpirePatch(
            clz = CombatRewardScreen.class,
            method = "setupItemReward"
    )
    public static class GenerateModificationReward {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void Insert(CombatRewardScreen __instance) {
            if (AbstractDungeon.getCurrRoom() instanceof MonsterRoom) {
                ChimeraCardsPlus.modificationRewardsGenerator.addModificationRewards(__instance);
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws CannotCompileException, PatchingException {
                MethodCallMatcher methodCallMatcher = new MethodCallMatcher(ProceedButton.class, "show");
                return LineFinder.findInOrder(ctBehavior, methodCallMatcher);
            }
        }
    }

    @SpirePatch(
            clz = CardRewardScreen.class,
            method = "takeReward"
    )
    public static class RemoveLinkedModificationReward {
        @SpirePrefixPatch
        public static void Prefix(CardRewardScreen __instance) {
            if (__instance.rItem != null && __instance.rItem.relicLink instanceof AbstractModificationReward) {
                AbstractDungeon.combatRewardScreen.rewards.remove(__instance.rItem.relicLink);
            }
        }
    }
}
