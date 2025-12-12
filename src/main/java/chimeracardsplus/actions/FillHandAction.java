package chimeracardsplus.actions;

import basemod.BaseMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class FillHandAction extends AbstractGameAction {
    private final boolean upgraded;

    public FillHandAction(boolean upgraded) {
        this.upgraded = upgraded;
        actionType = ActionType.SPECIAL;
    }

    @Override
    public void update() {
        int effect = BaseMod.MAX_HAND_SIZE - AbstractDungeon.player.hand.size();
        for (int i = 0; i < effect; ++i) {
            AbstractCard c = AbstractDungeon.returnTrulyRandomCardInCombat().makeCopy();
            if (upgraded && c.canUpgrade()) {
                c.upgrade();
            }
            addToTop(new MakeTempCardInHandAction(c, 1));
        }
        isDone = true;
    }
}