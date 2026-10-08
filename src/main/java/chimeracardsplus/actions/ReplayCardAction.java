package chimeracardsplus.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class ReplayCardAction extends AbstractGameAction {
    private final AbstractCard card;
    private final boolean randomTarget;

    public ReplayCardAction(AbstractCard card, boolean randomTarget) {
        this.card = card;
        this.randomTarget = randomTarget;
    }

    public ReplayCardAction(AbstractCard card, AbstractMonster target) {
        this.card = card;
        this.target = target;
        randomTarget = false;
    }

    @Override
    public void update() {
        AbstractCard copy = card.makeSameInstanceOf();
        AbstractDungeon.player.limbo.addToBottom(copy);
        copy.current_x = card.current_x;
        copy.current_y = card.current_y;
        copy.target_x = Settings.WIDTH / 2.0F - 300.0F * Settings.scale;
        copy.target_y = Settings.HEIGHT / 2.0F;
        copy.purgeOnUse = true;
        if (randomTarget) {
            target = AbstractDungeon.getRandomMonster();
        }
        if (target != null && target instanceof AbstractMonster) {
            copy.calculateCardDamage((AbstractMonster) target);
            AbstractDungeon.actionManager.addCardQueueItem(new CardQueueItem(copy, (AbstractMonster) target, card.energyOnUse, true, true), true);
        } else {
            AbstractDungeon.actionManager.addCardQueueItem(new CardQueueItem(copy, null, card.energyOnUse, true, true), true);
        }
        isDone = true;
    }
}
