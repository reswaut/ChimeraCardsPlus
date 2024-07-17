package chimeracardsplus.actions;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class EnlightenedAction extends AbstractGameAction {
    private final AbstractPlayer p;

    public EnlightenedAction() {
        this.actionType = ActionType.CARD_MANIPULATION;
        this.p = AbstractDungeon.player;
        this.duration = Settings.ACTION_DUR_FAST;
    }

    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST) {
            boolean betterPossible = false;
            boolean possible = false;

            for (AbstractCard c : this.p.hand.group) {
                if (c.costForTurn > 1) {
                    betterPossible = true;
                } else if (c.cost > 1) {
                    possible = true;
                }
            }

            if (betterPossible || possible) {
                this.findAndModifyCard(betterPossible);
            }
        }

        this.tickDuration();
    }

    private void findAndModifyCard(boolean better) {
        AbstractCard c = this.p.hand.getRandomCard(AbstractDungeon.cardRandomRng);
        if (better) {
            if (c.costForTurn > 1) {
                c.costForTurn = 1;
                c.isCostModified = true;
                c.superFlash(Color.GOLD.cpy());
            } else {
                this.findAndModifyCard(true);
            }
        } else if (c.cost > 1) {
            c.costForTurn = 1;
            c.isCostModified = true;
            c.superFlash(Color.GOLD.cpy());
        } else {
            this.findAndModifyCard(false);
        }

    }
}
