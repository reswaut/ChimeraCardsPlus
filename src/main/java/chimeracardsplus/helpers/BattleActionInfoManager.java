package chimeracardsplus.helpers;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import basemod.interfaces.*;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import chimeracardsplus.powers.DoomPower;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.lib.Matcher.MethodCallMatcher;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.ui.panels.PotionPopUp;
import com.megacrit.cardcrawl.ui.panels.TopPanel;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.Arrays;

@SpireInitializer
public class BattleActionInfoManager implements
        OnPlayerDamagedSubscriber,
        OnPlayerTurnStartSubscriber,
        OnStartBattleSubscriber,
        PostPotionUseSubscriber,
        PostPowerApplySubscriber {
    public boolean playerDamagedThisTurn = false;
    public boolean usedPotionThisCombat = false;
    public boolean usedPotionThisTurn = false;
    public boolean appliedDoomThisTurn = false;
    public int drawPileShufflesThisCombat = 0;

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

    private static boolean onShuffle(AbstractCard card, CardGroup group) {
        for (AbstractCardModifier mod : CardModifierManager.modifiers(card)) {
            if (mod instanceof AbstractAugmentPlus) {
                if (((AbstractAugmentPlus) mod).onShuffle(card, group)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void receiveOnBattleStart(AbstractRoom abstractRoom) {
        playerDamagedThisTurn = false;
        usedPotionThisCombat = false;
        usedPotionThisTurn = false;
        appliedDoomThisTurn = false;
        drawPileShufflesThisCombat = 0;
    }

    @Override
    public void receiveOnPlayerTurnStart() {
        playerDamagedThisTurn = false;
        usedPotionThisTurn = false;
        appliedDoomThisTurn = false;
    }

    @Override
    public int receiveOnPlayerDamaged(int i, DamageInfo damageInfo) {
        playerDamagedThisTurn = true;
        return i;
    }

    @Override
    public void receivePostPotionUse(AbstractPotion potion) {
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

    @Override
    public void receivePostPowerApplySubscriber(AbstractPower abstractPower, AbstractCreature abstractCreature, AbstractCreature abstractCreature1) {
        if (DoomPower.POWER_ID.equals(abstractPower.ID) && abstractCreature1.isPlayer) {
            appliedDoomThisTurn = true;
        }
    }

    public void onShuffle() {
        drawPileShufflesThisCombat += 1;
        for (CardGroup group : Arrays.asList(AbstractDungeon.player.masterDeck, AbstractDungeon.player.drawPile, AbstractDungeon.player.hand, AbstractDungeon.player.discardPile, AbstractDungeon.player.exhaustPile)) {

            boolean modified;
            do {
                modified = false;
                for (AbstractCard card : group.group) {
                    if (onShuffle(card, group)) {
                        modified = true;
                        break;
                    }
                }
            } while (modified);
        }
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
