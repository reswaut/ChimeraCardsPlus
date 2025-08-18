package chimeracardsplus.cardmods.common;

import CardAugments.cardmods.AbstractAugment;
import CardAugments.cardmods.DynvarCarrier;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardTags;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.cards.red.PerfectedStrike;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class PerfectMod extends AbstractAugment implements DynvarCarrier {
    public static final String ID = ChimeraCardsPlus.makeID(PerfectMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;
    private static final String DESCRIPTION_KEY = '!' + ID + '!';
    private static final int[] multiplier = {6, 4, 3, 2};

    public static boolean isStrike(AbstractCard c) {
        return c.hasTag(CardTags.STRIKE);
    }

    @Override
    public boolean validCard(AbstractCard abstractCard) {
        return isStrike(abstractCard) && abstractCard.baseDamage >= 2;
    }

    @Override
    public float modifyBaseDamage(float damage, DamageType type, AbstractCard card, AbstractMonster target) {
        if (CardCrawlGame.isInARun() && PerfectedStrike.ID.equals(card.cardID)) {
            float realBaseDamage = card.baseDamage - card.magicNumber * PerfectedStrike.countCards();
            return damage - realBaseDamage * 0.5F;
        }
        return damage * 0.5F;
    }

    @Override
    public float modifyDamage(float damage, DamageType type, AbstractCard card, AbstractMonster target) {
        if (PerfectedStrike.ID.equals(card.cardID)) {
            return damage;
        }
        return damage + baseVal(card) * PerfectedStrike.countCards();
    }

    @Override
    public float modifyBaseMagic(float magic, AbstractCard card) {
        if (PerfectedStrike.ID.equals(card.cardID)) {
            return magic + baseVal(card);
        }
        return magic;
    }

    @Override
    public String key() {
        return ID;
    }

    @Override
    public int val(AbstractCard abstractCard) {
        return baseVal(abstractCard);
    }

    @Override
    public int baseVal(AbstractCard abstractCard) {
        int upgrades = getEffectiveUpgrades(abstractCard);
        if (upgrades >= multiplier.length) {
            upgrades = multiplier.length - 1;
        }
        int realBaseDamage = abstractCard.baseDamage;
        if (CardCrawlGame.isInARun() && PerfectedStrike.ID.equals(abstractCard.cardID)) {
            realBaseDamage -= abstractCard.magicNumber * PerfectedStrike.countCards();
        }
        return (realBaseDamage - 1) / multiplier[upgrades] + 1;
    }

    @Override
    public boolean modified(AbstractCard abstractCard) {
        return false;
    }

    @Override
    public boolean upgraded(AbstractCard abstractCard) {
        return abstractCard.timesUpgraded != 0 || abstractCard.upgraded;
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
        if (PerfectedStrike.ID.equals(card.cardID)) {
            return rawDescription;
        }
        return insertAfterText(rawDescription, String.format(CARD_TEXT[0], DESCRIPTION_KEY));
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.COMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new PerfectMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}