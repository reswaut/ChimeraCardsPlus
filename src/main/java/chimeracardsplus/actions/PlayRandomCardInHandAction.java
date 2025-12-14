package chimeracardsplus.actions;

import chimeracardsplus.helpers.Constants;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PlayRandomCardInHandAction extends AbstractGameAction {
    private final AbstractCard card;

    public PlayRandomCardInHandAction(AbstractCard card) {
        this.card = card;
    }

    @Override
    public void update() {
        List<AbstractCard> cards = AbstractDungeon.player.hand.group.stream().filter(c -> !card.equals(c)).collect(Collectors.toCollection(() -> new ArrayList<>(Constants.DEFAULT_LIST_SIZE)));
        if (cards.isEmpty()) {
            isDone = true;
            return;
        }

        AbstractCard c = cards.get(AbstractDungeon.cardRandomRng.random(cards.size() - 1));
        addToBot(new NewQueueCardAction(c, true, false, true));
        isDone = true;
    }
}
