package chimeracardsplus.actions;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class EnlightenedAction extends AbstractGameAction {
    @Override
    public void update() {
        boolean betterPossible = false, possible = false;
        for (AbstractCard c : AbstractDungeon.player.hand.group) {
            if (c.costForTurn > 1) {
                betterPossible = true;
            } else if (c.cost > 1) {
                possible = true;
            }
        }
        if (betterPossible || possible) {
            while (true) {
                AbstractCard c = AbstractDungeon.player.hand.getRandomCard(AbstractDungeon.cardRandomRng);
                if (betterPossible) {
                    if (c.costForTurn > 1) {
                        c.costForTurn = 1;
                        c.isCostModified = true;
                        c.superFlash(Color.GOLD.cpy());
                    } else {
                        continue;
                    }
                } else {
                    if (c.cost > 1) {
                        c.costForTurn = 1;
                        c.isCostModified = true;
                        c.superFlash(Color.GOLD.cpy());
                    } else {
                        continue;
                    }
                }
                break;
            }
        }
        isDone = true;
    }
}
