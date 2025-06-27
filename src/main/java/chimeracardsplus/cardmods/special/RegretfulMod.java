package chimeracardsplus.cardmods.special;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class RegretfulMod extends AbstractAugment {
    public static final String ID = ChimeraCardsPlus.makeID(RegretfulMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

    @Override
    public boolean validCard(AbstractCard card) {
        return isNormalCard(card);
    }

    @Override
    public void atEndOfTurn(AbstractCard card, CardGroup group) {
        if (group.type != CardGroup.CardGroupType.HAND) {
            return;
        }
        this.addToBot(new LoseHPAction(AbstractDungeon.player, AbstractDungeon.player, AbstractDungeon.player.hand.size(), AbstractGameAction.AttackEffect.FIRE));
        card.flash(Color.RED);
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
        return AugmentRarity.SPECIAL;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new RegretfulMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}