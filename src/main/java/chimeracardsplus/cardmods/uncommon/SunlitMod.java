package chimeracardsplus.cardmods.uncommon;

import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;

public class SunlitMod extends AbstractAugmentPlus {
    public static final String ID = ChimeraCardsPlus.makeID(SunlitMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;
    private boolean descriptionHack = false;

    @Override
    public boolean validCard(AbstractCard abstractCard) {
        return abstractCard.cost >= -1;
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        if (ChimeraCardsPlus.gameActionInfoManager.getDrawPileShufflesThisCombat() == 3) {
            addToBot(new GainEnergyAction(2));
        }
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
    public Color getGlow(AbstractCard card) {
        if (ChimeraCardsPlus.gameActionInfoManager.getDrawPileShufflesThisCombat() == 3) {
            return Color.GOLD.cpy();
        }
        return null;
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
            int count = ChimeraCardsPlus.gameActionInfoManager.getDrawPileShufflesThisCombat();
            text += String.format(count == 1 ? CARD_TEXT[1] : CARD_TEXT[2], count);
        }
        return insertAfterText(rawDescription, text);
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new SunlitMod();
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