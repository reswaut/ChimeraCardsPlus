package chimeracardsplus.patches.cards;

import basemod.helpers.CardModifierManager;
import chimeracardsplus.ChimeraCardsPlus;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.lib.Matcher.NewExprMatcher;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.watcher.ChooseOneAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.purple.Wish;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.List;

@SpirePatch(
        clz = Wish.class,
        method = "use"
)
public class WishPatches {
    @SpireInsertPatch(
            locator = Locator.class,
            localvars = "stanceChoices"
    )
    public static void Insert(Wish __instance, AbstractPlayer p, AbstractMonster m, List<AbstractCard> stanceChoices) {
        if (!ChimeraCardsPlus.enableBaseGameFixes()) {
            return;
        }
        stanceChoices.get(0).baseMagicNumber = (int) CardModifierManager.onModifyBaseDamage(__instance.baseDamage, __instance, null);
        stanceChoices.get(0).magicNumber = stanceChoices.get(0).baseMagicNumber;
        stanceChoices.get(1).baseMagicNumber = (int) CardModifierManager.onModifyBaseMagic(__instance.baseMagicNumber, __instance);
        stanceChoices.get(1).magicNumber = stanceChoices.get(1).baseMagicNumber;
        stanceChoices.get(2).baseMagicNumber = (int) CardModifierManager.onModifyBaseBlock(__instance.baseBlock, __instance);
        stanceChoices.get(2).magicNumber = stanceChoices.get(2).baseMagicNumber;
    }

    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctBehavior) throws CannotCompileException, PatchingException {
            Matcher finalMatcher = new NewExprMatcher(ChooseOneAction.class);
            return LineFinder.findInOrder(ctBehavior, finalMatcher);
        }
    }
}
