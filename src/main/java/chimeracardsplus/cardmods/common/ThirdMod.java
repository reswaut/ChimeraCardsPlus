package chimeracardsplus.cardmods.common;

import CardAugments.cardmods.AbstractAugment;
import CardAugments.cardmods.DynvarCarrier;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.actions.utility.ScryAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static chimeracardsplus.util.CardAugmentsExt.doesntDowngradeMagicNoUseChecks;

public class ThirdMod extends AbstractAugment implements DynvarCarrier {
    public static final String ID = ChimeraCardsPlus.makeID(ThirdMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;
    public static final String DESCRIPTION_KEY = "!" + ID + "!";
    public boolean modified;
    public boolean upgraded;

    @Override
    public boolean validCard(AbstractCard card) {
        return (card.baseDamage > 1 || card.baseBlock > 1 || (card.baseMagicNumber > 1 && doesntDowngradeMagicNoUseChecks(card))) &&
                cardCheck(card, c -> (c.cost != -2));
    }

    @Override
    public float modifyBaseDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        return (damage > 1) ? (damage * 0.75F) : damage;
    }

    @Override
    public float modifyBaseBlock(float block, AbstractCard card) {
        return (block > 1) ? (block * 0.75F) : block;
    }

    @Override
    public float modifyBaseMagic(float magic, AbstractCard card) {
        return (magic > 1 && doesntDowngradeMagicNoUseChecks(card)) ? (magic * 0.75F) : magic;
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        this.addToBot(new ScryAction(getBaseVal(card)));
    }

    public int getBaseVal(AbstractCard card) {
        return 3 + 2 * getEffectiveUpgrades(card);
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
        return insertAfterText(rawDescription, String.format(CARD_TEXT[0], DESCRIPTION_KEY));
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.COMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new ThirdMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}