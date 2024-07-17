package chimeracardsplus.cardmods.rare;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class CorruptedMod extends AbstractAugment {
    public static final String ID = ChimeraCardsPlus.makeID(CorruptedMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;
    private boolean addedExhaust = false;

    @Override
    public void onInitialApplication(AbstractCard card) {
        card.cost = 0;
        card.costForTurn = card.cost;
        addedExhaust = !card.exhaust;
        card.exhaust = true;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return card.cost >= 1
                && card.type == AbstractCard.CardType.SKILL
                && cardCheck(card, (c) -> doesntUpgradeCost());
    }

    @Override
    public void onUpgradeCheck(AbstractCard card) {
        if (!card.exhaust) {
            addedExhaust = true;
            card.exhaust = true;
        }
        card.initializeDescription();
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
        return addedExhaust ? insertAfterText(rawDescription, CARD_TEXT[0]) : rawDescription;
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new CorruptedMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}