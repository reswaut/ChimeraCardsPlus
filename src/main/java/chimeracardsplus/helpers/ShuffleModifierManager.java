package chimeracardsplus.helpers;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.Arrays;

public class ShuffleModifierManager {
    public static int drawPileShufflesThisCombat = 0;

    public static void onBattleStart() {
        drawPileShufflesThisCombat = 0;
    }

    public static void onShuffle() {
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
}
