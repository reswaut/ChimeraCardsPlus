package chimeracardsplus.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.NextTurnBlockPower;

public class ProlongBlockAction extends AbstractGameAction {
    public ProlongBlockAction() {
        actionType = ActionType.BLOCK;
    }

    @Override
    public void update() {
        AbstractCreature player = AbstractDungeon.player;
        if (player.currentBlock > 0) {
            addToTop(new ApplyPowerAction(player, player, new NextTurnBlockPower(player, player.currentBlock)));
        }
        isDone = true;
    }
}
