package chimeracardsplus.cardmods.common;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class HappyMod extends AbstractAugment {
    public static final String ID = ChimeraCardsPlus.makeID(HappyMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;
    private boolean used = false;

    @Override
    public boolean validCard(AbstractCard card) {
        return cardCheck(card, (c) -> c.cost >= -1) && characterCheck((p) -> p.hasRelic("Happy Flower"));
    }

    @Override
    public void atEndOfTurn(AbstractCard card, CardGroup group) {
        used = true;
        AbstractRelic relic = AbstractDungeon.player.getRelic("Happy Flower");
        if (relic != null && relic.counter == 2) {
            used = false;
        }
    }

    @Override
    public boolean onBattleStart(AbstractCard card) {
        used = true;
        AbstractRelic relic = AbstractDungeon.player.getRelic("Happy Flower");
        if (relic != null && relic.counter == 2) {
            used = false;
        }
        return false;
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        if (used) {
            return;
        }
        AbstractRelic relic = AbstractDungeon.player.getRelic("Happy Flower");
        if (relic != null && relic.counter == 0) {
            relic.flash();
            this.addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, relic));
            this.addToBot(new GainEnergyAction(1));
            used = true;
        }
    }

    @Override
    public Color getGlow(AbstractCard card) {
        if (used) {
            return null;
        }
        AbstractRelic relic = AbstractDungeon.player.getRelic("Happy Flower");
        if (relic != null && relic.counter == 0) {
            return Color.GOLD;
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
        return insertAfterText(rawDescription, CARD_TEXT[0]);
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.COMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new HappyMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}