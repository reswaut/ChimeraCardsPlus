package chimeracardsplus.cardmods.rare;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.unique.GainEnergyIfDiscardAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.green.SneakyStrike;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;

import static chimeracardsplus.util.CardCheckHelpers.hasCardWithKeywordInDeck;

public class StealthyMod extends AbstractAugment {
    public static final String ID = ChimeraCardsPlus.makeID(StealthyMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

    @Override
    public boolean validCard(AbstractCard card) {
        return card.cost >= 0 && cardCheck(card, (c) -> doesntUpgradeCost()) && characterCheck((p) -> hasCardWithKeywordInDeck(p, CARD_TEXT[3]));
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        card.cost += 1;
        card.costForTurn = card.cost;
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        this.addToBot(new GainEnergyIfDiscardAction(2));
    }

    @Override
    public Color getGlow(AbstractCard card) {
        return (GameActionManager.totalDiscardedThisTurn > 0) ? Color.GOLD.cpy() : null;
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
        if (card instanceof SneakyStrike) {
            return rawDescription.replace(CARD_TEXT[1], CARD_TEXT[2]);
        }
        return insertAfterText(rawDescription, CARD_TEXT[0]);
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new StealthyMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}