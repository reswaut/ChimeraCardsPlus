package chimeracardsplus.actions;

import basemod.BaseMod;
import chimeracardsplus.helpers.Constants;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.CardRewardScreen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class SeekOneOfThreeAction extends AbstractGameAction {
    private boolean first = true;

    public SeekOneOfThreeAction() {
        actionType = ActionType.CARD_MANIPULATION;
    }

    private static ArrayList<AbstractCard> generateCardChoices() {
        Collection<Integer> derp = new ArrayList<>(Constants.DEFAULT_LIST_SIZE);
        for (int i = Math.min(3, AbstractDungeon.player.drawPile.group.size()); i > 0; --i) {
            int tmp;
            do {
                tmp = AbstractDungeon.miscRng.random(0, AbstractDungeon.player.drawPile.size() - 1);
            } while (derp.contains(tmp));
            derp.add(tmp);
        }
        return derp.stream().map(id -> AbstractDungeon.player.drawPile.getNCardFromTop(id)).collect(Collectors.toCollection(() -> new ArrayList<>(Constants.DEFAULT_LIST_SIZE)));
    }

    @Override
    public void update() {
        if (first) {
            first = false;
            if (AbstractDungeon.player.drawPile.isEmpty()) {
                isDone = true;
            } else if (AbstractDungeon.player.drawPile.size() == 1) {
                AbstractCard c = AbstractDungeon.player.drawPile.getTopCard();

                if (AbstractDungeon.player.hand.size() >= BaseMod.MAX_HAND_SIZE) {
                    AbstractDungeon.player.drawPile.moveToDiscardPile(c);
                    AbstractDungeon.player.createHandIsFullDialog();
                } else {
                    AbstractDungeon.player.drawPile.moveToHand(c);
                }

                isDone = true;
            } else {
                AbstractDungeon.cardRewardScreen.customCombatOpen(generateCardChoices(), CardRewardScreen.TEXT[1], true);
            }
            return;
        }
        AbstractCard card = AbstractDungeon.cardRewardScreen.discoveryCard;
        if (card != null) {
            card.unhover();
            if (AbstractDungeon.player.hand.size() >= BaseMod.MAX_HAND_SIZE) {
                AbstractDungeon.player.drawPile.moveToDiscardPile(card);
                AbstractDungeon.player.createHandIsFullDialog();
            } else {
                AbstractDungeon.player.drawPile.moveToHand(card);
            }

            AbstractDungeon.cardRewardScreen.discoveryCard = null;
        }
        isDone = true;
    }
}
