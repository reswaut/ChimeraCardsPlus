package chimeracardsplus.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.actions.utility.UnlimboAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class PlayDiscardedCardAction extends AbstractGameAction {
    private final AbstractCard card;

    public PlayDiscardedCardAction(AbstractCard card, AbstractCreature target) {
        this.card = card;
        this.target = target;
    }

    @Override
    public void update() {
        AbstractDungeon.player.discardPile.removeCard(card);
        AbstractDungeon.getCurrRoom().souls.remove(card);
        AbstractDungeon.player.limbo.group.add(card);
        card.current_y = -200.0F * Settings.scale;
        card.target_x = Settings.WIDTH / 2.0F + 200.0F * Settings.xScale;
        card.target_y = Settings.HEIGHT / 2.0F;
        card.targetAngle = 0.0F;
        card.lighten(false);
        card.drawScale = 0.12F;
        card.targetDrawScale = 0.75F;
        card.applyPowers();
        addToTop(new NewQueueCardAction(card, target, false, true));
        addToTop(new UnlimboAction(card));
        if (Settings.FAST_MODE) {
            addToTop(new WaitAction(Settings.ACTION_DUR_FASTER));
        } else {
            addToTop(new WaitAction(Settings.ACTION_DUR_MED));
        }
        isDone = true;
    }
}
