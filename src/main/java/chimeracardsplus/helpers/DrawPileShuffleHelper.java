package chimeracardsplus.helpers;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class DrawPileShuffleHelper {
    public int drawPileShufflesThisCombat = 0;

    public void onBattleStart(AbstractRoom room) {
        drawPileShufflesThisCombat = 0;
    }

    public void onShuffle() {
        drawPileShufflesThisCombat += 1;
        for (AbstractCard card : AbstractDungeon.player.hand.group) {
            onShuffle(card, AbstractDungeon.player.hand);
        }
        for (AbstractCard card : AbstractDungeon.player.drawPile.group) {
            onShuffle(card, AbstractDungeon.player.drawPile);
        }
        for (AbstractCard card : AbstractDungeon.player.discardPile.group) {
            onShuffle(card, AbstractDungeon.player.discardPile);
        }
    }

    private static void onShuffle(AbstractCard card, CardGroup group) {
        for (AbstractCardModifier mod : CardModifierManager.modifiers(card)) {
            if (mod instanceof AbstractAugmentPlus) {
                ((AbstractAugmentPlus) mod).onShuffle(card, group);
            }
        }
    }
}
