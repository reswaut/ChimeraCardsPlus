package chimeracardsplus.effects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class RemoveCardFromMasterDeckEffect extends AbstractGameEffect {
    private final AbstractCard card;

    public RemoveCardFromMasterDeckEffect(AbstractCard card) {
        this.card = card;
    }

    @Override
    public void update() {
        AbstractDungeon.player.masterDeck.removeCard(card);
        isDone = true;
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
    }

    @Override
    public void dispose() {
    }
}
