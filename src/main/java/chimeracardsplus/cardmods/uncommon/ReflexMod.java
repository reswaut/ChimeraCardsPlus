package chimeracardsplus.cardmods.uncommon;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.interfaces.TriggerOnDiscardMod;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.green.Reflex;
import com.megacrit.cardcrawl.core.CardCrawlGame;

import static chimeracardsplus.util.CardCheckHelpers.hasCardWithKeywordInDeck;

public class ReflexMod extends AbstractAugment implements TriggerOnDiscardMod {
    public static final String ID = ChimeraCardsPlus.makeID(ReflexMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

    @Override
    public boolean validCard(AbstractCard card) {
        return characterCheck((p) -> hasCardWithKeywordInDeck(p, CARD_TEXT[1]));
    }

    @Override
    public float modifyBaseMagic(float magic, AbstractCard card) {
        if (Reflex.ID.equals(card.cardID)) {
            return magic + 1;
        }
        return magic;
    }

    @Override
    public String getPrefix() {
        return TEXT[0];
    }

    @Override
    public String getSuffix() {
        return TEXT[1];
    }

    @Override
    public String getAugmentDescription() {
        return TEXT[2];
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        if (Reflex.ID.equals(card.cardID)) {
            return rawDescription;
        }
        return insertAfterText(rawDescription, CARD_TEXT[0]);
    }

    public void onManualDiscard(AbstractCard card) {
        if (Reflex.ID.equals(card.cardID)) {
            return;
        }
        this.addToBot(new DrawCardAction(1));
    }

    public void onMoveToDiscard(AbstractCard card) {
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new ReflexMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}