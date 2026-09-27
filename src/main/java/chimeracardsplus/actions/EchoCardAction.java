package chimeracardsplus.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class EchoCardAction extends AbstractGameAction {
    private final AbstractCard card;

    public EchoCardAction(AbstractCard card) {
        this.card = card;
    }

    @Override
    public void update() {
        AbstractMonster monster = AbstractDungeon.getRandomMonster();
        if (monster != null) {
            AbstractCard copy = card.makeSameInstanceOf();
            AbstractDungeon.player.limbo.addToBottom(copy);
            copy.current_x = card.current_x;
            copy.current_y = card.current_y;
            copy.target_x = Settings.WIDTH / 2.0F - 300.0F * Settings.scale;
            copy.target_y = Settings.HEIGHT / 2.0F;
            copy.calculateCardDamage(monster);
            copy.purgeOnUse = true;
            AbstractDungeon.actionManager.addCardQueueItem(new CardQueueItem(copy, monster, card.energyOnUse, true, true), true);
        }
        isDone = true;
    }
}
