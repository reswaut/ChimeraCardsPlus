package chimeracardsplus.cardmods.rare;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.interfaces.TriggerOnDiscardMod;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class NormalMod extends AbstractAugment implements TriggerOnDiscardMod {
    public static final String ID = ChimeraCardsPlus.makeID(NormalMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;
    boolean descriptionHack = false;

    @Override
    public void onInitialApplication(AbstractCard card) {
        card.cost -= 1;
        card.costForTurn = card.cost;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return cardCheck(card, (c) -> (c.cost >= 1 && doesntUpgradeCost()));
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
        String text = CARD_TEXT[0];
        if (descriptionHack) {
            int count = AbstractDungeon.actionManager.cardsPlayedThisTurn.size();
            text += String.format((count == 1) ? CARD_TEXT[1] : CARD_TEXT[2], count);
        }
        return insertAfterText(rawDescription, text);
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        descriptionHack = false;
        card.initializeDescription();
    }

    @Override
    public void onApplyPowers(AbstractCard card) {
        descriptionHack = true;
        card.initializeDescription();
    }

    @Override
    public void onMoveToDiscard(AbstractCard card) {
        descriptionHack = false;
        card.initializeDescription();
    }

    @Override
    public void onManualDiscard(AbstractCard card) {
    }

    @Override
    public boolean betterCanPlay(AbstractCard cardWithThisMod, AbstractCard cardToCheck) {
        return AbstractDungeon.actionManager.cardsPlayedThisTurn.size() < 3;
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new NormalMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}