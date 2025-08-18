package chimeracardsplus.interfaces;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public abstract class TriggerOnUsePotionMod extends AbstractAugment {
    public static boolean usedPotionThisCombat = false;
    public static boolean usedPotionThisTurn = false;

    public static void onPlayerTurnStart() {
        usedPotionThisTurn = false;
    }

    public static void onBattleStart(AbstractRoom room) {
        usedPotionThisCombat = false;
    }

    public static void usedPotion(AbstractPotion potion) {
        usedPotionThisCombat = true;
        usedPotionThisTurn = true;
        for (AbstractCard card : AbstractDungeon.player.hand.group) {
            usedPotion(card, AbstractDungeon.player.hand, potion);
        }
        for (AbstractCard card : AbstractDungeon.player.drawPile.group) {
            usedPotion(card, AbstractDungeon.player.drawPile, potion);
        }
        for (AbstractCard card : AbstractDungeon.player.discardPile.group) {
            usedPotion(card, AbstractDungeon.player.discardPile, potion);
        }
    }

    private static void usedPotion(AbstractCard card, CardGroup group, AbstractPotion potion) {
        for (AbstractCardModifier mod : CardModifierManager.modifiers(card)) {
            if (mod instanceof TriggerOnUsePotionMod) {
                ((TriggerOnUsePotionMod) mod).onUsePotion(card, group, potion);
            }
        }
    }

    abstract void onUsePotion(AbstractCard card, CardGroup group, AbstractPotion potion);
}
