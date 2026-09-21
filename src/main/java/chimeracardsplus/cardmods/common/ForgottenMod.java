package chimeracardsplus.cardmods.common;

import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;

public class ForgottenMod extends AbstractAugmentPlus {
    public static final String ID = ChimeraCardsPlus.makeID(ForgottenMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;
    private boolean addedExhaust = true;

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (!card.exhaust && card.type != CardType.POWER) {
            addedExhaust = true;
            card.exhaust = true;
        } else {
            addedExhaust = false;
        }
    }

    @Override
    public boolean validCard(AbstractCard abstractCard) {
        return cardCheck(abstractCard, c -> c.cost >= -1 && doesntUpgradeExhaust());
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        if (ChimeraCardsPlus.gameActionInfoManager.isExhaustedCardThisTurn()) {
            addToBot(new GainEnergyAction(1));
        }
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
        return insertAfterText(rawDescription, addedExhaust ? CARD_TEXT[0] : CARD_TEXT[1]);
    }

    @Override
    public Color getGlow(AbstractCard card) {
        return ChimeraCardsPlus.gameActionInfoManager.isExhaustedCardThisTurn() ? Color.GOLD.cpy() : null;
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.COMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new ForgottenMod();
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