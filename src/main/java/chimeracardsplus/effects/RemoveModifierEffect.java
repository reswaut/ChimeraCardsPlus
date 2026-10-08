package chimeracardsplus.effects;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class RemoveModifierEffect extends AbstractGameEffect {
    private final AbstractCard card;
    private final AbstractCardModifier mod;
    private final boolean includeInherent;

    public RemoveModifierEffect(AbstractCard card, AbstractCardModifier mod, boolean includeInherent) {
        this.card = card;
        this.mod = mod;
        this.includeInherent = includeInherent;
    }

    @Override
    public void update() {
        CardModifierManager.removeSpecificModifier(card, mod, includeInherent);
        isDone = true;
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
    }

    @Override
    public void dispose() {
    }
}
