package chimeracardsplus.cardmods.rare;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.cardRandomRng;

public class StrangeMod extends AbstractAugment {
    public static final String ID = ChimeraCardsPlus.makeID(StrangeMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;

    @Override
    public void onInitialApplication(AbstractCard card) {
        card.exhaust = false;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return cardCheck(card, (c) -> c.exhaust && doesntUpgradeExhaust() && c.cost >= -1);
    }

    @Override
    public boolean onBattleStart(AbstractCard card) {
        card.exhaust = cardRandomRng.randomBoolean();
        return false;
    }

    @Override
    public void onCreatedMidCombat(AbstractCard card) {
        card.exhaust = cardRandomRng.randomBoolean();
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
        int lastExhaust = rawDescription.lastIndexOf(CARD_TEXT[1]);
        if (lastExhaust == -1) {
            return insertAfterText(rawDescription, CARD_TEXT[0]);
        }
        return rawDescription.substring(0, lastExhaust) +
                rawDescription.substring(lastExhaust).replaceFirst(CARD_TEXT[1], CARD_TEXT[0]);
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature cardTarget, UseCardAction action) {
        card.exhaust = cardRandomRng.randomBoolean();
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new StrangeMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}