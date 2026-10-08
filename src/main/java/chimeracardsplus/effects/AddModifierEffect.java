package chimeracardsplus.effects;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class AddModifierEffect extends AbstractGameEffect {
    private final AbstractCard card;
    private final AbstractCardModifier mod;

    public AddModifierEffect(AbstractCard card, AbstractCardModifier mod) {
        this.card = card;
        this.mod = mod;
    }

    @Override
    public void update() {
        CardModifierManager.addModifier(card, mod);
        isDone = true;
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
    }

    @Override
    public void dispose() {
    }
}
