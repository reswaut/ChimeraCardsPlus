package chimeracardsplus.cardmods.rare;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

import static chimeracardsplus.util.CardCheckHelpers.doesntDowngradeMagicNoUseChecks;

public class DistantMod extends AbstractAugment {
    public static final String ID = ChimeraCardsPlus.makeID(DistantMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

    @Override
    public boolean validCard(AbstractCard card) {
        return (card.baseDamage >= 1 || card.baseBlock >= 1 || (card.baseMagicNumber >= 1 && doesntDowngradeMagicNoUseChecks(card))) && card.cost >= 1;
    }

    @Override
    public float modifyBaseDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        return damage * 3.0F;
    }

    @Override
    public float modifyBaseBlock(float block, AbstractCard card) {
        return block * 3.0F;
    }

    @Override
    public float modifyBaseMagic(float magic, AbstractCard card) {
        return (magic >= 1 && doesntDowngradeMagicNoUseChecks(card)) ? (magic * 3.0F) : magic;
    }

    @Override
    public boolean canPlayCard(AbstractCard card) {
        boolean ret = EnergyPanel.getCurrentEnergy() == 0;
        if (!ret) {
            card.cantUseMessage = CARD_TEXT[1];
        }
        return ret;
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
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new DistantMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}