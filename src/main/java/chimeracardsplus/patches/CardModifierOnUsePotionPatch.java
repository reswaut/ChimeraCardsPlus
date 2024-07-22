package chimeracardsplus.patches;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import chimeracardsplus.interfaces.TriggerOnUsePotionMod;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.ui.panels.PotionPopUp;
import com.megacrit.cardcrawl.ui.panels.TopPanel;
import javassist.CtBehavior;

public class CardModifierOnUsePotionPatch {
    private static void TriggerOnUsePotion(AbstractPotion potion) {
        for (AbstractCard card : AbstractDungeon.player.hand.group) {
            TriggerOnUsePotion(card, AbstractDungeon.player.hand, potion);
        }
        for (AbstractCard card : AbstractDungeon.player.drawPile.group) {
            TriggerOnUsePotion(card, AbstractDungeon.player.drawPile, potion);
        }
        for (AbstractCard card : AbstractDungeon.player.discardPile.group) {
            TriggerOnUsePotion(card, AbstractDungeon.player.discardPile, potion);
        }
    }

    public static void TriggerOnUsePotion(AbstractCard card, CardGroup group, AbstractPotion potion) {
        for (AbstractCardModifier mod : CardModifierManager.modifiers(card)) {
            if (mod instanceof TriggerOnUsePotionMod) {
                ((TriggerOnUsePotionMod) mod).onUsePotion(card, group, potion);
            }
        }
    }

    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "damage"
    )
    public static class FairyPotion {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"p"}
        )
        public static void Insert(AbstractPlayer __instance, DamageInfo info, AbstractPotion potion) {
            TriggerOnUsePotion(potion);
        }
    }

    @SpirePatch(
            clz = PotionPopUp.class,
            method = "updateInput"
    )
    @SpirePatch(
            clz = PotionPopUp.class,
            method = "updateTargetMode"
    )
    public static class NormalPotions {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"potion"}
        )
        public static void Insert(PotionPopUp __instance, AbstractPotion potion) {
            TriggerOnUsePotion(potion);
        }
    }

    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(TopPanel.class, "destroyPotion");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}
