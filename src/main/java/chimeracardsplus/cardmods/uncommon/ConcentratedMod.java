package chimeracardsplus.cardmods.uncommon;

import CardAugments.cardmods.AbstractAugment;
import CardAugments.cardmods.DynvarCarrier;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class ConcentratedMod extends AbstractAugment implements DynvarCarrier {
    public static final String ID = ChimeraCardsPlus.makeID(ConcentratedMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;
    public static final String DESCRIPTION_KEY = "!" + ID + "!";
    public boolean modified;
    public boolean upgraded;

    @Override
    public boolean validCard(AbstractCard card) {
        return cardCheck(card, (c) -> c.cost >= 0);
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        int discards = getBaseVal(card);
        if (discards > 0) {
            this.addToBot(new DiscardAction(AbstractDungeon.player, AbstractDungeon.player, discards, false));
        }
        this.addToBot(new GainEnergyAction(2));
    }

    public int getBaseVal(AbstractCard card) {
        int upgrades = this.getEffectiveUpgrades(card);
        if (upgrades >= 3) {
            upgrades = 3;
        }
        return 3 - upgrades;
    }

    public String key() {
        return ID;
    }

    public int val(AbstractCard card) {
        return this.getBaseVal(card);
    }

    public int baseVal(AbstractCard card) {
        return this.getBaseVal(card);
    }

    public boolean modified(AbstractCard card) {
        return this.modified;
    }

    public boolean upgraded(AbstractCard card) {
        this.modified = card.timesUpgraded != 0 || card.upgraded;
        this.upgraded = card.timesUpgraded != 0 || card.upgraded;
        return this.upgraded;
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
        String text;
        int discards = getBaseVal(card);
        if (discards > 1) {
            text = String.format(CARD_TEXT[0], DESCRIPTION_KEY);
        } else if (discards == 1) {
            text = String.format(CARD_TEXT[1], DESCRIPTION_KEY);
        } else {
            text = CARD_TEXT[2];
        }
        return insertAfterText(rawDescription, text);
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new ConcentratedMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}