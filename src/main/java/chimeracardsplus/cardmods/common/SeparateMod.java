package chimeracardsplus.cardmods.common;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.actions.ExhaustAllCardsInHandAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.red.SeverSoul;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static chimeracardsplus.util.CardCheckHelpers.doesntDowngradeMagicNoUseChecks;

public class SeparateMod extends AbstractAugment {
    public static final String ID = ChimeraCardsPlus.makeID(SeparateMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

    @Override
    public boolean validCard(AbstractCard card) {
        return (card.baseDamage >= 3 || card.baseBlock >= 3 || (card.magicNumber >= 3 && doesntDowngradeMagicNoUseChecks(card)))
                && cardCheck(card, (c) -> (c.cost >= -1 && !(c instanceof SeverSoul)
                && (c.type == AbstractCard.CardType.ATTACK ||
                c.type == AbstractCard.CardType.SKILL ||
                c.type == AbstractCard.CardType.POWER)));
    }

    @Override
    public float modifyBaseDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        return (damage >= 3.0F) ? (damage * 4.0F / 3.0F) : damage;
    }

    @Override
    public float modifyBaseBlock(float block, AbstractCard card) {
        return (block >= 3.0F) ? (block * 4.0F / 3.0F) : block;
    }

    @Override
    public float modifyBaseMagic(float magic, AbstractCard card) {
        return (magic >= 3.0F && doesntDowngradeMagicNoUseChecks(card)) ? (magic * 4.0F / 3.0F) : magic;
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
        String text = "";
        if (card.type == AbstractCard.CardType.ATTACK) {
            text = CARD_TEXT[0];
        } else if (card.type == AbstractCard.CardType.SKILL) {
            text = CARD_TEXT[1];
        } else if (card.type == AbstractCard.CardType.POWER) {
            text = CARD_TEXT[2];
        }
        return insertAfterText(rawDescription, text);
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        this.addToBot(new ExhaustAllCardsInHandAction(card.type));
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.COMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new SeparateMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}