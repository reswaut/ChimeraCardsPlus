package chimeracardsplus.cardmods.common;

import CardAugments.patches.InterruptUseCardFieldPatches.InterceptUseField;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.actions.UseCardMultipleTimesAction;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import chimeracardsplus.helpers.ShuffleModifierManager;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import com.megacrit.cardcrawl.localization.UIStrings;

import java.util.regex.Pattern;

public class TallyingMod extends AbstractAugmentPlus {
    public static final String ID = ChimeraCardsPlus.makeID(TallyingMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;
    private boolean descriptionHack = false;

    @Override
    public void onInitialApplication(AbstractCard card) {
        InterceptUseField.interceptUse.set(card, true);
    }

    @Override
    public boolean validCard(AbstractCard abstractCard) {
        return cardCheck(abstractCard, c -> noShenanigans(c)
                && c.cost >= -1 && (c.type == CardType.ATTACK || c.type == CardType.SKILL)
                && customCheck(c, check -> noCardModDescriptionChanges(check) && check.rawDescription.chars().filter(ch -> ch == LocalizedStrings.PERIOD.charAt(0)).count() == 1L));
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
            int count = ShuffleModifierManager.drawPileShufflesThisCombat;
            text += String.format(count == 1 ? CARD_TEXT[1] : CARD_TEXT[2], count);
        }
        return rawDescription.replaceFirst(Pattern.quote(LocalizedStrings.PERIOD), text);
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        addToBot(new UseCardMultipleTimesAction(card, target, () -> ShuffleModifierManager.drawPileShufflesThisCombat));
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
    public AugmentRarity getModRarity() {
        return AugmentRarity.COMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new TallyingMod();
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