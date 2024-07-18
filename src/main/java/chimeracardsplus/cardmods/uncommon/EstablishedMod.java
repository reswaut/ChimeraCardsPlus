package chimeracardsplus.cardmods.uncommon;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class EstablishedMod extends AbstractAugment {
    public static final String ID = ChimeraCardsPlus.makeID(EstablishedMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;
    private int turnsRetained;

    public EstablishedMod() {
        this.turnsRetained = 0;
    }

    public EstablishedMod(int turnsRetained) {
        this.turnsRetained = turnsRetained;
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        card.selfRetain = true;
        card.cost += 2;
        card.costForTurn = card.cost;
    }
    @Override
    public boolean validCard(AbstractCard card) {
        return cardCheck(card, (c) -> (c.cost >= 1 && doesntUpgradeCost() && notRetain(c)
                && doesntOverride(c, "triggerOnEndOfTurnForPlayingCard")));
    }

    @Override
    public void onRetained(AbstractCard card) {
        turnsRetained += 1;
        card.applyPowers();
    }

    @Override
    public void onApplyPowers(AbstractCard card) {
        card.setCostForTurn(Math.max(0, card.cost - turnsRetained));
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
        return CARD_TEXT[0] + insertAfterText(rawDescription, CARD_TEXT[1]);
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new EstablishedMod(turnsRetained);
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}