package chimeracardsplus.actions;

import basemod.BaseMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class AdaptAction extends AbstractGameAction {
    private final CardType type;

    public AdaptAction(CardType type) {
        this.type = type;
        actionType = ActionType.CARD_MANIPULATION;
    }

    @Override
    public void update() {
        AbstractCard card = AbstractDungeon.player.discardPile.getRandomCard(type, true);
        if (card == null) {
            isDone = true;
            return;
        }
        if (card.canUpgrade()) {
            card.upgrade();
            card.applyPowers();
        }
        if (AbstractDungeon.player.hand.size() >= BaseMod.MAX_HAND_SIZE) {
            AbstractDungeon.player.createHandIsFullDialog();
        } else {
            AbstractDungeon.player.discardPile.removeCard(card);
            AbstractDungeon.player.hand.addToHand(card);
            card.lighten(false);
            card.applyPowers();
        }
        isDone = true;
    }
}
