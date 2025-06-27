package chimeracardsplus.cardmods.uncommon;

import CardAugments.cardmods.AbstractAugment;
import CardAugments.patches.InterruptUseCardFieldPatches;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.actions.RecycleXAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class RecyclableMod extends AbstractAugment {
    public static final String ID = ChimeraCardsPlus.makeID(RecyclableMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;
    private boolean inherentHack = true;
    private AbstractCard hiddenCard;

    // This modifier should be applied first.
    public RecyclableMod() {
        this.priority = -100;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return cardCheck(card, (c) -> (c.cost == -1 && doesntUpgradeCost() && noShenanigans(c)));
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        this.inherentHack = true;
        hiddenCard = card.makeStatEquivalentCopy();
        this.inherentHack = false;
        InterruptUseCardFieldPatches.InterceptUseField.interceptUse.set(card, true);
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
        boolean inserted = false;
        StringBuilder newDescription = new StringBuilder(CARD_TEXT[0]);
        while (index < rawDescription.length()) {
            int nextIndex1 = rawDescription.indexOf(CARD_TEXT[3], index);
            int nextIndex2 = rawDescription.indexOf(CARD_TEXT[4], index);
            if (nextIndex1 == -1 && nextIndex2 == -1) {
                newDescription.append(rawDescription, index, rawDescription.length());
                if (!inserted) {
                    newDescription.append(CARD_TEXT[2]);
                }
                break;
            }
            inserted = true;
            if (nextIndex1 == -1) {
                nextIndex1 = rawDescription.length();
            }
            if (nextIndex2 == -1) {
                nextIndex2 = rawDescription.length();
            }
            int nextIndex = Math.min(nextIndex1, nextIndex2);
            int periodIndex = rawDescription.indexOf(CARD_TEXT[5], nextIndex);

            if (periodIndex == -1) {
                newDescription.append(rawDescription, index, rawDescription.length());
                if (newDescription.toString().endsWith("]")) {
                    newDescription.append(" ");
                }
                newDescription.append(CARD_TEXT[1]);
                break;
            }
            newDescription.append(rawDescription, index, periodIndex);
            if (newDescription.toString().endsWith("]")) {
                newDescription.append(" ");
            }
            newDescription.append(CARD_TEXT[1]);
            index = periodIndex + 1;
        }
        return String.valueOf(newDescription);
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
        this.addToBot(new RecycleXAction(hiddenCard, target));
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new RecyclableMod();
    }

    public boolean isInherent(AbstractCard card) {
        return this.inherentHack;
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}