package chimeracardsplus.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class ExhaustAllCardsInHandAction extends AbstractGameAction {
    private final float startingDuration;
    private final CardType excludeCardType;

    public ExhaustAllCardsInHandAction(CardType cardType) {
        this.actionType = ActionType.WAIT;
        this.startingDuration = Settings.ACTION_DUR_FAST;
        this.duration = this.startingDuration;
        excludeCardType = cardType;
    }

    public void update() {
        if (this.duration == this.startingDuration) {

            for (AbstractCard c : AbstractDungeon.player.hand.group) {
                if (c.type != excludeCardType) {
                    this.addToTop(new ExhaustSpecificCardAction(c, AbstractDungeon.player.hand));
                }
            }

            this.isDone = true;
        }

    }
}
