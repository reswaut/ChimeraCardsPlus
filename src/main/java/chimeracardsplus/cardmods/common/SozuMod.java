package chimeracardsplus.cardmods.common;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.interfaces.TriggerOnUsePotionMod;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.potions.AbstractPotion;

public class SozuMod extends AbstractAugment implements TriggerOnUsePotionMod {
    public static final String ID = ChimeraCardsPlus.makeID(SozuMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;
    private boolean used = true;

    @Override
    public boolean validCard(AbstractCard card) {
        return cardCheck(card, (c) -> c.cost >= -1);
    }

    @Override
    public void atEndOfTurn(AbstractCard card, CardGroup group) {
        used = true;
    }

    @Override
    public boolean onBattleStart(AbstractCard card) {
        used = true;
        return false;
    }

    @Override
    public void onUsePotion(AbstractCard card, CardGroup group, AbstractPotion potion) {
        used = false;
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        if (!used) {
            this.addToBot(new GainEnergyAction(1));
        }
    }

    @Override
    public Color getGlow(AbstractCard card) {
        return used ? null : Color.GOLD.cpy();
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
        return insertAfterText(rawDescription, CARD_TEXT[0]);
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.COMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new SozuMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}