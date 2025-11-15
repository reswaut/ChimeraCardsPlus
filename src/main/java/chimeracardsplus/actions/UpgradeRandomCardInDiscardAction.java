package chimeracardsplus.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class UpgradeRandomCardInDiscardAction extends AbstractGameAction {
    public UpgradeRandomCardInDiscardAction() {
        actionType = ActionType.CARD_MANIPULATION;
    }

    @Override
    public void update() {
        CardGroup upgradeable = new CardGroup(CardGroupType.UNSPECIFIED);
        for (AbstractCard c : AbstractDungeon.player.discardPile.group) {
            if (c.canUpgrade()) {
                upgradeable.addToTop(c);
            }
        }
        if (!upgradeable.isEmpty()) {
            AbstractCard c = upgradeable.getRandomCard(true);
            c.upgrade();
            c.applyPowers();
        }
        isDone = true;
    }
}
