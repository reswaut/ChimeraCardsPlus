package chimeracardsplus.cardmods.uncommon;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.EmptyOrbSlot;

public class LoopyMod extends AbstractAugment {
    public static final String ID = ChimeraCardsPlus.makeID(LoopyMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

    @Override
    public boolean validCard(AbstractCard card) {
        return allowOrbMods() && cardCheck(card, (c) -> c.cost >= -1);
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        if (AbstractDungeon.player.orbs.get(0) instanceof EmptyOrbSlot) {
            return;
        }
        AbstractDungeon.player.orbs.get(0).onStartOfTurn();
        AbstractDungeon.player.orbs.get(0).onEndOfTurn();
        if (AbstractDungeon.player.hasRelic("Cables")) {
            AbstractDungeon.player.orbs.get(0).onStartOfTurn();
            AbstractDungeon.player.orbs.get(0).onEndOfTurn();
        }
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
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new LoopyMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}