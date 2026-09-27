package chimeracardsplus.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class TriggerAllPoisonAction extends AbstractGameAction {
    @Override
    public void update() {
        // The card's own effect may have killed monsters, so the monsters are only looked at now.
        // addToTop is LIFO, so each monster is queued in reverse order.
        for (int i = AbstractDungeon.getMonsters().monsters.size() - 1; i >= 0; --i) {
            AbstractMonster mo = AbstractDungeon.getMonsters().monsters.get(i);
            if (!mo.isDeadOrEscaped()) {
                addToTop(new TriggerPoisonAction(mo));
            }
        }
        isDone = true;
    }
}
