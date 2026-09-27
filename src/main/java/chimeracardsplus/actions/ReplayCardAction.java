package chimeracardsplus.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class ReplayCardAction extends AbstractGameAction {
    private final AbstractCard card;
    private final AbstractMonster target;

    public ReplayCardAction(AbstractCard card, AbstractMonster target) {
        this.card = card;
        this.target = target;
    }

    @Override
    public void update() {
        AbstractCard copy = card.makeSameInstanceOf();
        AbstractDungeon.player.limbo.addToBottom(copy);
        copy.current_x = card.current_x;
        copy.current_y = card.current_y;
        copy.target_x = Settings.WIDTH / 2.0F - 300.0F * Settings.scale;
        copy.target_y = Settings.HEIGHT / 2.0F;
        if (target != null) {
            copy.calculateCardDamage(target);
        }
        copy.purgeOnUse = true;
        AbstractDungeon.actionManager.addCardQueueItem(new CardQueueItem(copy, target, card.energyOnUse, true, true), true);
        isDone = true;
    }
}
