package chimeracardsplus.cardmods.common;

import CardAugments.cardmods.AbstractAugment;
import CardAugments.cardmods.DynvarCarrier;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.red.PerfectedStrike;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static com.megacrit.cardcrawl.core.CardCrawlGame.isInARun;

public class PerfectMod extends AbstractAugment implements DynvarCarrier {
    public static final String ID = ChimeraCardsPlus.makeID(PerfectMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;
    private static final String DESCRIPTION_KEY = "!" + ID + "!";
    public static final int[] multiplier = { 6, 4, 3, 2 };
    private boolean modified = false;

    public static boolean isStrike(AbstractCard c) {
        return c.hasTag(AbstractCard.CardTags.STRIKE);
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return isStrike(card) && card.baseDamage >= 2;
    }

    @Override
    public float modifyBaseDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        if (isInARun() && PerfectedStrike.ID.equals(card.cardID)) {
            float realBaseDamage = card.baseDamage - card.magicNumber * PerfectedStrike.countCards();
            return damage - realBaseDamage * 0.5F;
        }
        return damage * 0.5F;
    }

    @Override
    public float modifyDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        if (PerfectedStrike.ID.equals(card.cardID)) {
            return damage;
        }
        return damage + getBaseVal(card) * PerfectedStrike.countCards();
    }

    @Override
    public float modifyBaseMagic(float magic, AbstractCard card) {
        if (PerfectedStrike.ID.equals(card.cardID)) {
            return magic + getBaseVal(card);
        }
        return magic;
    }

    public int getBaseVal(AbstractCard card) {
        int upgrades = this.getEffectiveUpgrades(card);
        if (upgrades >= multiplier.length) {
            upgrades = multiplier.length - 1;
        }
        int realBaseDamage = card.baseDamage;
        if (isInARun() && PerfectedStrike.ID.equals(card.cardID)) {
            realBaseDamage -= card.magicNumber * PerfectedStrike.countCards();
        }
        return (realBaseDamage - 1) / multiplier[upgrades] + 1;
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
        return this.modified;
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