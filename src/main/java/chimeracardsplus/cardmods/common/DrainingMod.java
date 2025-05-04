package chimeracardsplus.cardmods.common;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static chimeracardsplus.util.CardCheckHelpers.doesntDowngradeMagicNoUseChecks;

public class DrainingMod extends AbstractAugment {
    public static final String ID = ChimeraCardsPlus.makeID(DrainingMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

    @Override
    public float modifyBaseDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        return (damage >= 0.0F) ? (damage * 1.4F) : damage;
    }

    @Override
    public float modifyBaseBlock(float block, AbstractCard card) {
        return (block >= 0.0F) ? (block * 1.4F) : block;
    }

    @Override
    public float modifyBaseMagic(float magic, AbstractCard card) {
        return (magic >= 0.0F && doesntDowngradeMagicNoUseChecks(card)) ? (magic * 1.4F) : magic;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return (card.baseDamage >= 3 || card.baseBlock >= 3 || (card.baseMagicNumber >= 3 && doesntDowngradeMagicNoUseChecks(card)));
    }

    @Override
    public void onDrawn(AbstractCard card) {
        this.addToBot(new LoseHPAction(AbstractDungeon.player, AbstractDungeon.player, 1));
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
        return new DrainingMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}