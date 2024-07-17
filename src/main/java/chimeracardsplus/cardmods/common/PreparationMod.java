package chimeracardsplus.cardmods.common;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class PreparationMod extends AbstractAugment {
    public static final String ID = ChimeraCardsPlus.makeID(PreparationMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;
    private boolean used = false;

    @Override
    public boolean validCard(AbstractCard card) {
        return cardCheck(card, (c) -> c.cost >= -1) && characterCheck((p) -> p.hasRelic("Bag of Preparation"));
    }

    @Override
    public boolean onBattleStart(AbstractCard card) {
        used = false;
        return false;
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
        return insertAfterText(rawDescription, CARD_TEXT[0]);
    }

    @Override
    public Color getGlow(AbstractCard card) {
        return (!used && GameActionManager.turn <= 1) ? Color.GOLD : null;
    }

    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        if (!used && GameActionManager.turn <= 1) {
            AbstractRelic relic = AbstractDungeon.player.getRelic("Bag of Preparation");
            if (relic != null) {
                relic.flash();
                this.addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, relic));
            }
            this.addToBot(new DrawCardAction(2));
            used = true;
        }
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.COMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new PreparationMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}