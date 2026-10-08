package chimeracardsplus.actions;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;

public class RemoveModifierAction extends AbstractGameAction {
    private final AbstractCard card;
    private final AbstractCardModifier mod;
    private final boolean includeInherent;

    public RemoveModifierAction(AbstractCard card, AbstractCardModifier mod, boolean includeInherent) {
        this.card = card;
        this.mod = mod;
        this.includeInherent = includeInherent;
    }

    @Override
    public void update() {
        CardModifierManager.removeSpecificModifier(card, mod, includeInherent);
        isDone = true;
    }
}
