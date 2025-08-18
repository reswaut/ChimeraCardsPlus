package chimeracardsplus.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class ShareBlockAction extends AbstractGameAction {
    public ShareBlockAction(int block) {
        amount = block;
    }

    @Override
    public void update() {
        for (int i = AbstractDungeon.getMonsters().monsters.size(); i > 0; --i) {
            AbstractMonster mo = AbstractDungeon.getMonsters().monsters.get(i);
            if (!mo.isDeadOrEscaped()) {
                addToTop(new GainBlockAction(mo, amount));
            }
        }
        isDone = true;
    }
}
