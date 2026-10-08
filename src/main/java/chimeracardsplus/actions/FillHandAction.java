package chimeracardsplus.actions;

import basemod.BaseMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.function.Supplier;

public class FillHandAction extends AbstractGameAction {
    private final Supplier<AbstractCard> cardProvider;

    public FillHandAction(Supplier<AbstractCard> cardProvider) {
        this.cardProvider = cardProvider;
    }

    @Override
    public void update() {
        int effect = BaseMod.MAX_HAND_SIZE - AbstractDungeon.player.hand.size();
        for (int i = 0; i < effect; ++i) {
            addToTop(new MakeTempCardInHandAction(cardProvider.get(), 1));
        }
        isDone = true;
    }
}
