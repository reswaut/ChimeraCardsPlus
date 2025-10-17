package chimeracardsplus.cardmods.uncommon;

import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.green.Reflex;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;

public class ReflexMod extends AbstractAugmentPlus {
    public static final String ID = ChimeraCardsPlus.makeID(ReflexMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;

    @Override
    public boolean validCard(AbstractCard abstractCard) {
        return characterCheck(p -> hasCardWithKeywordInDeck(p, CARD_TEXT[1]));
    }

    @Override
    public float modifyBaseMagic(float magic, AbstractCard card) {
        if (Reflex.ID.equals(card.cardID)) {
            return magic + 1.0F;
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
        if (Reflex.ID.equals(card.cardID) && ChimeraCardsPlus.configs.enableBaseGameFixes()) {
            return rawDescription;
        }
        return insertAfterText(rawDescription, CARD_TEXT[0]);
    }

    @Override
    public void onManualDiscard(AbstractCard card) {
        if (Reflex.ID.equals(card.cardID)) {
            return;
        }
        addToBot(new DrawCardAction(1));
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

    @Override
    public AugmentBonusLevel getModBonusLevel() {
        return AugmentBonusLevel.NORMAL;
    }
}