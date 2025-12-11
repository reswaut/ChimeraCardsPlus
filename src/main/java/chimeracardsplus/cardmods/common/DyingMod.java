package chimeracardsplus.cardmods.common;

import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;

public class DyingMod extends AbstractAugmentPlus {
    public static final String ID = ChimeraCardsPlus.makeID(DyingMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;

    @Override
    public float modifyBaseBlock(float block, AbstractCard card) {
        return block * 0.75F;
    }

    @Override
    public boolean validCard(AbstractCard abstractCard) {
        return abstractCard.cost >= -1 && abstractCard.baseBlock >= 3 && characterCheck(p -> hasCardWithKeywordInDeck(p, CARD_TEXT[1]));
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
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        if (ChimeraCardsPlus.gameActionInfoManager.isAppliedDoomThisTurn()) {
            addToBot(new GainBlockAction(AbstractDungeon.player, AbstractDungeon.player, card.block));
            addToBot(new GainBlockAction(AbstractDungeon.player, AbstractDungeon.player, card.block));
        }
    }

    @Override
    public Color getGlow(AbstractCard card) {
        return ChimeraCardsPlus.gameActionInfoManager.isAppliedDoomThisTurn() ? Color.GOLD.cpy() : null;
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.COMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new DyingMod();
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