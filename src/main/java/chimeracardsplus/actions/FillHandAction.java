package chimeracardsplus.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class FillHandAction extends AbstractGameAction {
    private final boolean upgraded;

    public FillHandAction(boolean upgraded) {
        this.upgraded = upgraded;
        duration = Settings.ACTION_DUR_XFAST;
        actionType = ActionType.SPECIAL;
    }

    @Override
    public void update() {
        int effect = 10 - AbstractDungeon.player.hand.size();
        for (int i = 0; i < effect; ++i) {
            AbstractCard c = AbstractDungeon.returnTrulyRandomCardInCombat().makeCopy();
            if (upgraded) {
                c.upgrade();
            }
            addToTop(new MakeTempCardInHandAction(c, 1));
        }
        isDone = true;
    }
}