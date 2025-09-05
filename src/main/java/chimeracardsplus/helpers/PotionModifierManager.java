package chimeracardsplus.helpers;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.lib.Matcher.MethodCallMatcher;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.ui.panels.PotionPopUp;
import com.megacrit.cardcrawl.ui.panels.TopPanel;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.Arrays;

public class PotionModifierManager {
    public static boolean usedPotionThisCombat = false;
    public static boolean usedPotionThisTurn = false;

    public static void onPlayerTurnStart() {
        usedPotionThisTurn = false;
    }

    public static void onBattleStart() {
        usedPotionThisCombat = false;
    }

    public static void onUsePotion(AbstractPotion potion) {
        usedPotionThisCombat = true;
        usedPotionThisTurn = true;
        for (CardGroup group : Arrays.asList(AbstractDungeon.player.masterDeck, AbstractDungeon.player.drawPile, AbstractDungeon.player.hand, AbstractDungeon.player.discardPile, AbstractDungeon.player.exhaustPile)) {
            boolean modified;
            do {
                modified = false;
                for (AbstractCard card : group.group) {
                    if (onUsePotion(card, group, potion)) {
                        modified = true;
                        break;
                    }
                }
            } while (modified);
        }
    }

    private static boolean onUsePotion(AbstractCard card, CardGroup group, AbstractPotion potion) {
        for (AbstractCardModifier mod : CardModifierManager.modifiers(card)) {
            if (mod instanceof AbstractAugmentPlus) {
                if (((AbstractAugmentPlus) mod).onUsePotion(card, group, potion)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void preDiscardPotion(AbstractPotion potion) {
        for (CardGroup group : Arrays.asList(AbstractDungeon.player.masterDeck, AbstractDungeon.player.drawPile, AbstractDungeon.player.hand, AbstractDungeon.player.discardPile, AbstractDungeon.player.exhaustPile)) {
            boolean modified;
            do {
                modified = false;
                for (AbstractCard card : group.group) {
                    if (preDiscardPotion(card, group, potion)) {
                        modified = true;
                        break;
                    }
                }
            } while (modified);
        }
    }

    private static boolean preDiscardPotion(AbstractCard card, CardGroup group, AbstractPotion potion) {
        for (AbstractCardModifier mod : CardModifierManager.modifiers(card)) {
            if (mod instanceof AbstractAugmentPlus) {
                if (((AbstractAugmentPlus) mod).preDiscardPotion(card, group, potion)) {
                    return true;
                }
            }
        }
        return false;
    }

    @SpirePatch(
            clz = PotionPopUp.class,
            method = "updateInput"
    )
    public static class OnDestroyPotionPatches {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void Insert(PotionPopUp __instance, int ___slot) {
            preDiscardPotion(AbstractDungeon.player.potions.get(___slot));
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new MethodCallMatcher(TopPanel.class, "destroyPotion");
                int[] tmp = LineFinder.findAllInOrder(ctBehavior, finalMatcher);
                return new int[]{tmp[1]};
            }
        }
    }
}
