package chimeracardsplus.cardmods.uncommon;

import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardRarity;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.localization.UIStrings;

public class ForeignMod extends AbstractAugmentPlus {
    public static final String ID = ChimeraCardsPlus.makeID(ForeignMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;

    @Override
    public void onInitialApplication(AbstractCard card) {
        card.exhaust = true;
    }

    @Override
    public boolean validCard(AbstractCard abstractCard) {
        return cardCheck(abstractCard, c -> c.cost >= -1 && notExhaust(c) && (c.type == CardType.ATTACK || c.type == CardType.SKILL));
    }

    @Override
    public void onUpgradeCheck(AbstractCard card) {
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
        String text = "";
        if (card.type == CardType.ATTACK) {
            text = card.upgraded ? CARD_TEXT[1] : CARD_TEXT[0];
        } else if (card.type == CardType.SKILL) {
            text = card.upgraded ? CARD_TEXT[3] : CARD_TEXT[2];
        }
        return insertAfterText(rawDescription, text);
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        int roll = AbstractDungeon.cardRandomRng.random(99);
        CardRarity cardRarity;
        if (roll < 55) {
            cardRarity = CardRarity.COMMON;
        } else if (roll < 85) {
            cardRarity = CardRarity.UNCOMMON;
        } else {
            cardRarity = CardRarity.RARE;
        }
        AbstractCard c = CardLibrary.getAnyColorCard(card.type, cardRarity);
        if (card.upgraded && c.canUpgrade()) {
            c.upgrade();
        }
        addToBot(new MakeTempCardInHandAction(c, true));
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new ForeignMod();
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