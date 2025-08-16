package chimeracardsplus.interfaces;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public interface TriggerOnUsePotionMod {
    void onUsePotion(AbstractCard card, CardGroup group, AbstractPotion potion);

    class CardModifierOnUsePotionManager {
        public static boolean usedPotionThisCombat, usedPotionThisTurn;

        public static void onPlayerTurnStart() {
            usedPotionThisTurn = false;
        }

        public static void onBattleStart(AbstractRoom room) {
            usedPotionThisCombat = false;
        }

        public static void onUsePotion(AbstractPotion potion) {
            usedPotionThisCombat = true;
            usedPotionThisTurn = true;
            for (AbstractCard card : AbstractDungeon.player.hand.group) {
                onUsePotion(card, AbstractDungeon.player.hand, potion);
            }
            for (AbstractCard card : AbstractDungeon.player.drawPile.group) {
                onUsePotion(card, AbstractDungeon.player.drawPile, potion);
            }
            for (AbstractCard card : AbstractDungeon.player.discardPile.group) {
                onUsePotion(card, AbstractDungeon.player.discardPile, potion);
            }
        }

        private static void onUsePotion(AbstractCard card, CardGroup group, AbstractPotion potion) {
            for (AbstractCardModifier mod : CardModifierManager.modifiers(card)) {
                if (mod instanceof TriggerOnUsePotionMod) {
                    ((TriggerOnUsePotionMod) mod).onUsePotion(card, group, potion);
                }
            }
        }
    }
}
