package chimeracardsplus.actions;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.helpers.CardModifierManager;
import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.MultiCardPreview;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;

public class IllusionAction extends AbstractGameAction {
    private final AbstractCard card;
    private final AbstractAugment modifier;

    public IllusionAction(AbstractCard card, AbstractAugment modifier) {
        this.card = card;
        this.modifier = modifier;
    }

    @Override
    public void update() {
        AbstractCard copy = card.makeStatEquivalentCopy();
        CardModifierManager.removeSpecificModifier(card, modifier, true);
        CardAugmentsMod.applyTrulyRandomCardMod(card);
        CardAugmentsMod.applyTrulyRandomCardMod(card);
        MultiCardPreview.add(card, copy);
        isDone = true;
    }
}
