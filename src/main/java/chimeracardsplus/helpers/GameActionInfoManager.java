package chimeracardsplus.helpers;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import basemod.interfaces.OnPlayerTurnStartSubscriber;
import basemod.interfaces.OnStartBattleSubscriber;
import basemod.interfaces.PostPotionUseSubscriber;
import basemod.interfaces.PostPowerApplySubscriber;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import chimeracardsplus.powers.ChimeraCardsPlusHelperPower;
import chimeracardsplus.powers.DoomPower;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.lib.Matcher.MethodCallMatcher;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

@SpireInitializer
public class GameActionInfoManager implements
        OnPlayerTurnStartSubscriber,
        OnStartBattleSubscriber,
        PostPotionUseSubscriber,
        PostPowerApplySubscriber {
    private boolean playerDamagedThisTurn = false;
    private boolean usedPotionThisTurn = false;
    private boolean appliedDoomThisTurn = false;
    private boolean exhaustedCardThisTurn = false;
    private int drawPileShufflesThisCombat = 0;
    private int timesHPLostThisCombat = 0;
    private final Collection<AbstractGameAction> queuedPlayerTurnStartActions = new ArrayList<>(Constants.DEFAULT_LIST_SIZE);

    private static void preDiscardPotion(AbstractPotion potion) {
        for (CardGroup group : Arrays.asList(AbstractDungeon.player.masterDeck, AbstractDungeon.player.drawPile, AbstractDungeon.player.hand, AbstractDungeon.player.discardPile, AbstractDungeon.player.exhaustPile)) {
            for (AbstractCard card : group.group) {
                for (AbstractCardModifier mod : CardModifierManager.modifiers(card)) {
                    if (mod instanceof AbstractAugmentPlus) {
                        ((AbstractAugmentPlus) mod).preDiscardPotion(card, group, potion);
                    }
                }
            }
        }
    }

    public static void initialize() {
    }

    public void queuePlayerTurnStartAction(AbstractGameAction action) {
        queuedPlayerTurnStartActions.add(action);
    }

    @Override
    public void receiveOnBattleStart(AbstractRoom abstractRoom) {
        playerDamagedThisTurn = false;
        usedPotionThisTurn = false;
        appliedDoomThisTurn = false;
        exhaustedCardThisTurn = false;
        drawPileShufflesThisCombat = 0;
        timesHPLostThisCombat = 0;
        AbstractDungeon.player.addPower(new ChimeraCardsPlusHelperPower(AbstractDungeon.player));
        queuedPlayerTurnStartActions.clear();
    }

    @Override
    public void receiveOnPlayerTurnStart() {
        playerDamagedThisTurn = false;
        usedPotionThisTurn = false;
        appliedDoomThisTurn = false;
        exhaustedCardThisTurn = false;
        for (AbstractGameAction action : queuedPlayerTurnStartActions) {
            AbstractDungeon.actionManager.addToBottom(action);
        }
        queuedPlayerTurnStartActions.clear();
    }

    @Override
    public void receivePostPotionUse(AbstractPotion abstractPotion) {
        usedPotionThisTurn = true;
        for (CardGroup group : Arrays.asList(AbstractDungeon.player.masterDeck, AbstractDungeon.player.drawPile, AbstractDungeon.player.hand, AbstractDungeon.player.discardPile, AbstractDungeon.player.exhaustPile)) {
            for (AbstractCard card : group.group) {
                for (AbstractCardModifier mod : CardModifierManager.modifiers(card)) {
                    if (mod instanceof AbstractAugmentPlus) {
                        ((AbstractAugmentPlus) mod).onUsePotion(card, group, abstractPotion);
                    }
                }
            }
        }
    }

    @Override
    public void receivePostPowerApplySubscriber(AbstractPower abstractPower, AbstractCreature abstractCreature, AbstractCreature abstractCreature1) {
        if (DoomPower.POWER_ID.equals(abstractPower.ID) && abstractCreature1.isPlayer) {
            appliedDoomThisTurn = true;
        }
    }

    public void wasHPLost(DamageInfo damageInfo, int damageAmount) {
        if (damageAmount > 0) {
            playerDamagedThisTurn = true;
            timesHPLostThisCombat += 1;
        }
    }

    public void onExhaust(AbstractCard card) {
        exhaustedCardThisTurn = true;
    }

    public void onShuffle() {
        drawPileShufflesThisCombat += 1;
        for (CardGroup group : Arrays.asList(AbstractDungeon.player.masterDeck, AbstractDungeon.player.drawPile, AbstractDungeon.player.hand, AbstractDungeon.player.discardPile, AbstractDungeon.player.exhaustPile)) {
            for (AbstractCard card : group.group) {
                for (AbstractCardModifier mod : CardModifierManager.modifiers(card)) {
                    if (mod instanceof AbstractAugmentPlus) {
                        ((AbstractAugmentPlus) mod).onShuffle(card, group);
                    }
                }
            }
        }
    }

    public boolean isPlayerDamagedThisTurn() {
        return playerDamagedThisTurn;
    }

    public boolean isUsedPotionThisTurn() {
        return usedPotionThisTurn;
    }

    public boolean isAppliedDoomThisTurn() {
        return appliedDoomThisTurn;
    }

    public boolean isExhaustedCardThisTurn() {
        return exhaustedCardThisTurn;
    }

    public int getDrawPileShufflesThisCombat() {
        return drawPileShufflesThisCombat;
    }

    public int getTimesHPLostThisCombat() {
        return timesHPLostThisCombat;
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
