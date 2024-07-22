package chimeracardsplus.cardmods.common;

import CardAugments.cardmods.AbstractAugment;
import CardAugments.cardmods.DynvarCarrier;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.patches.MultiBaseDamageMod;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class SwipeMod extends AbstractAugment implements DynvarCarrier, MultiBaseDamageMod {
    public static final String ID = ChimeraCardsPlus.makeID(SwipeMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;
    public static final String DESCRIPTION_KEY = "!" + ID + "!";
    public boolean modified;
    public boolean upgraded;

    @Override
    public boolean validCard(AbstractCard card) {
        return cardCheck(card, (c) -> (c.cost >= -1 && c.baseDamage >= 3 && usesEnemyTargeting()));
    }

    @Override
    public float modifyBaseDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        return damage * 0.8F;
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        int temp = AbstractDungeon.getCurrRoom().monsters.monsters.size();

        for (int i = 0; i < temp; ++i) {
            if (!AbstractDungeon.getCurrRoom().monsters.monsters.get(i).isDeadOrEscaped()
                    && AbstractDungeon.getCurrRoom().monsters.monsters.get(i) != target) {
                AbstractDungeon.getCurrRoom().monsters.monsters.get(i).damage(
                        new DamageInfo(AbstractDungeon.player, card.multiDamage[i], card.damageTypeForTurn));
            }
        }
    }

    @Override
    public float modifyMultiBaseDamage(float baseDamage, AbstractMonster monster, AbstractCard card, AbstractMonster target) {
        if (monster == target) {
            return baseDamage;
        }
        return baseDamage / 2.0F;
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

    public int getBaseVal(AbstractCard card) {
        return card.baseDamage * 2 / 5;
    }

    public String key() {
        return ID;
    }

    public int val(AbstractCard card) {
        return getBaseVal(card);
    }

    public int baseVal(AbstractCard card) {
        return getBaseVal(card);
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
    public AugmentRarity getModRarity() {
        return AugmentRarity.COMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new SwipeMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}