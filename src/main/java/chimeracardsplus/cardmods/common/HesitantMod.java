package chimeracardsplus.cardmods.common;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.WeakPower;

import static chimeracardsplus.util.CardCheckHelpers.doesntDowngradeMagicNoUseChecks;

public class HesitantMod extends AbstractAugment {
    public static final String ID = ChimeraCardsPlus.makeID(HesitantMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

    @Override
    public float modifyBaseDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        return (damage >= 0.0F) ? (damage * 1.25F) : damage;
    }

    @Override
    public float modifyBaseBlock(float block, AbstractCard card) {
        return (block >= 0.0F) ? (block * 1.25F) : block;
    }

    @Override
    public float modifyBaseMagic(float magic, AbstractCard card) {
        return (magic >= 0.0F && doesntDowngradeMagicNoUseChecks(card)) ? (magic * 1.25F) : magic;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return (card.baseDamage >= 4 || card.baseBlock >= 4 || (card.baseMagicNumber >= 4 && doesntDowngradeMagicNoUseChecks(card)));
    }

    @Override
    public void atEndOfTurn(AbstractCard card, CardGroup group) {
        if (group.type != CardGroup.CardGroupType.HAND) {
            return;
        }
        this.addToTop(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new WeakPower(AbstractDungeon.player, 1, true), 1));
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
        return AugmentRarity.COMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new HesitantMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}