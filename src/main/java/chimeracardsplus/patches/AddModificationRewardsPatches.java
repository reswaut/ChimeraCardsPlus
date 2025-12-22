package chimeracardsplus.patches;

import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.rewards.AbstractModificationReward;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.lib.Matcher.MethodCallMatcher;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
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
                ChimeraCardsPlus.modificationRewardsManager.addRewardToRewardScreen(__instance);
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
            clz = CombatRewardScreen.class,
            method = "rewardViewUpdate"
    )
    public static class RemoveLinkedModificationReward {
        @SpirePrefixPatch
        public static void Prefix(CombatRewardScreen __instance) {
            boolean[] removed = {false};
            __instance.rewards.removeIf(item -> {
                boolean removeThis = item instanceof AbstractModificationReward && ((AbstractModificationReward) item).isInvalid();
                removed[0] = removed[0] || removeThis;
                return removeThis;
            });
            if (removed[0]) {
                __instance.positionRewards();
            }
        }
    }
}
