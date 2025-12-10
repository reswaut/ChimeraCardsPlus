package chimeracardsplus.actions;

import chimeracardsplus.helpers.Constants;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
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

                if (AbstractDungeon.player.hand.size() == 10) {
                    AbstractDungeon.player.drawPile.moveToDiscardPile(c);
                    AbstractDungeon.player.createHandIsFullDialog();
                } else {
                    c.unhover();
                    c.lighten(true);
                    c.setAngle(0.0F);
                    c.drawScale = 0.12F;
                    c.targetDrawScale = 0.75F;
                    c.current_x = CardGroup.DRAW_PILE_X;
                    c.current_y = CardGroup.DRAW_PILE_Y;
                    AbstractDungeon.player.drawPile.removeCard(c);
                    AbstractDungeon.player.hand.addToTop(c);
                    AbstractDungeon.player.hand.refreshHandLayout();
                    AbstractDungeon.player.hand.applyPowers();
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
            if (AbstractDungeon.player.hand.size() == 10) {
                AbstractDungeon.player.drawPile.moveToDiscardPile(card);
                AbstractDungeon.player.createHandIsFullDialog();
            } else {
                AbstractDungeon.player.drawPile.removeCard(card);
                AbstractDungeon.player.hand.addToTop(card);
            }

            AbstractDungeon.player.hand.refreshHandLayout();
            AbstractDungeon.player.hand.applyPowers();

            AbstractDungeon.cardRewardScreen.discoveryCard = null;
        }
        isDone = true;
    }
}
