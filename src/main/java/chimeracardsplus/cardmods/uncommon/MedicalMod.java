package chimeracardsplus.cardmods.uncommon;

import CardAugments.util.FormatHelper;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.GameDictionary;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import com.megacrit.cardcrawl.localization.UIStrings;

public class MedicalMod extends AbstractAugmentPlus {
    public static final String ID = ChimeraCardsPlus.makeID(MedicalMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;

    @Override
    public void onInitialApplication(AbstractCard card) {
        card.cost = 0;
        card.costForTurn = card.cost;
        card.exhaust = true;
    }

    @Override
    public boolean validCard(AbstractCard abstractCard) {
        return cardCheck(abstractCard, c -> c.type == CardType.STATUS && c.cost == -2 && doesntUpgradeCost());
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
        String description = rawDescription;
        for (String s : GameDictionary.UNPLAYABLE.NAMES) {
            description = description.replace(FormatHelper.capitalize(s) + LocalizedStrings.PERIOD + " NL ", "");
            description = description.replace(FormatHelper.capitalize(s) + ' ' + LocalizedStrings.PERIOD + " NL ", "");
            description = description.replace(FormatHelper.capitalize(s) + LocalizedStrings.PERIOD, "");
            description = description.replace(FormatHelper.capitalize(s) + ' ' + LocalizedStrings.PERIOD, "");
        }
        return insertAfterText(description, CARD_TEXT[0]);
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new MedicalMod();
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