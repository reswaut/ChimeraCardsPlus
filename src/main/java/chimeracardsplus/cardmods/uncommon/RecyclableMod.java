package chimeracardsplus.cardmods.uncommon;

import CardAugments.patches.InterruptUseCardFieldPatches.InterceptUseField;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.actions.RecycleXAction;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;

public class RecyclableMod extends AbstractAugmentPlus {
    public static final String ID = ChimeraCardsPlus.makeID(RecyclableMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;
    private boolean inherentHack = true;
    private AbstractCard hiddenCard = null;

    // This modifier should be applied first.
    public RecyclableMod() {
        priority = -100;
    }

    @Override
    public boolean validCard(AbstractCard abstractCard) {
        return cardCheck(abstractCard, c -> c.cost == -1 && doesntUpgradeCost() && noShenanigans(c));
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        inherentHack = true;
        hiddenCard = card.makeStatEquivalentCopy();
        inherentHack = false;
        InterceptUseField.interceptUse.set(card, true);
        card.cost = 1;
        card.costForTurn = card.cost;
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
        int index = 0;
        boolean shouldInsert = true;
        StringBuilder newDescription = new StringBuilder(CARD_TEXT[0]);
        while (index < rawDescription.length()) {
            int nextIndex1 = rawDescription.indexOf(CARD_TEXT[3], index);
            int nextIndex2 = rawDescription.indexOf(CARD_TEXT[4], index);
            if (nextIndex1 == -1 && nextIndex2 == -1) {
                newDescription.append(rawDescription, index, rawDescription.length());
                break;
            }
            shouldInsert = false;
            if (nextIndex1 == -1) {
                nextIndex1 = rawDescription.length();
            }
            if (nextIndex2 == -1) {
                nextIndex2 = rawDescription.length();
            }
            int nextIndex = Math.min(nextIndex1, nextIndex2);
            int periodIndex = rawDescription.indexOf(CARD_TEXT[5], nextIndex);
            newDescription.append(rawDescription, index, periodIndex == -1 ? rawDescription.length() : periodIndex);
            if (!newDescription.toString().isEmpty() && newDescription.toString().charAt(newDescription.toString().length() - 1) == ']') {
                newDescription.append(' ');
            }
            newDescription.append(CARD_TEXT[1]);
            if (periodIndex == -1) {
                break;
            }
            index = periodIndex + 1;
        }
        String description = String.valueOf(newDescription);
        if (shouldInsert) {
            description = insertAfterText(description, CARD_TEXT[2]);
        }
        return description;
    }

    @Override
    public void onApplyPowers(AbstractCard card) {
        hiddenCard.applyPowers();
        hiddenCard.initializeDescription();
    }

    @Override
    public void onUpgradeCheck(AbstractCard card) {
        hiddenCard.upgrade();
        hiddenCard.initializeDescription();
        card.initializeDescription();
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        addToBot(new RecycleXAction(hiddenCard, target));
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new RecyclableMod();
    }

    @Override
    public boolean isInherent(AbstractCard card) {
        return inherentHack;
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