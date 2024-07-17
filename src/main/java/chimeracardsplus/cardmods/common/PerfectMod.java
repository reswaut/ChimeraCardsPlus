package chimeracardsplus.cardmods.common;

import CardAugments.cardmods.AbstractAugment;
import CardAugments.cardmods.DynvarCarrier;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class PerfectMod extends AbstractAugment implements DynvarCarrier {
    public static final String ID = ChimeraCardsPlus.makeID(PerfectMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;
    public static final String DESCRIPTION_KEY = "!" + ID + "!";
    public static final int[] multiplier = { 6, 4, 3, 2 };
    public boolean modified;
    public boolean upgraded;

    public static int countCards() {
        int count = 0;
        for (AbstractCard c : AbstractDungeon.player.hand.group) {
            if (isStrike(c)) {
                ++count;
            }
        }

        for (AbstractCard c : AbstractDungeon.player.drawPile.group) {
            if (isStrike(c)) {
                ++count;
            }
        }

        for (AbstractCard c : AbstractDungeon.player.discardPile.group) {
            if (isStrike(c)) {
                ++count;
            }
        }

        return count;
    }

    public static boolean isStrike(AbstractCard c) {
        return c.hasTag(AbstractCard.CardTags.STRIKE);
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return cardCheck(card, (c) -> isStrike(c) && c.baseDamage > 1);
    }

    @Override
    public float modifyBaseDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        return damage * 0.5F;
    }

    @Override
    public float modifyDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        return damage + getBaseVal(card) * countCards();
    }

    public int getBaseVal(AbstractCard card) {
        int upgrades = this.getEffectiveUpgrades(card);
        if (upgrades >= multiplier.length) {
            upgrades = multiplier.length - 1;
        }
        return (card.baseDamage - 1) / multiplier[upgrades] + 1;
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
        return new PerfectMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}