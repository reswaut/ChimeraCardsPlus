package chimeracardsplus.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class LoseMaxHpAction extends AbstractGameAction {
    public LoseMaxHpAction(int amount) {
        this.amount = amount;
    }

    @Override
    public void update() {
        AbstractDungeon.player.decreaseMaxHealth(1);
        CardCrawlGame.sound.play("BLOOD_SWISH");
        isDone = true;
    }
}
