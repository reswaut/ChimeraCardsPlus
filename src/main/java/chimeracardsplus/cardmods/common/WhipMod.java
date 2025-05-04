package chimeracardsplus.cardmods.common;

import CardAugments.cardmods.AbstractAugment;
import CardAugments.cardmods.DynvarCarrier;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.actions.watcher.HeadStompAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.purple.SashWhip;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class WhipMod extends AbstractAugment implements DynvarCarrier {
    public static final String ID = ChimeraCardsPlus.makeID(WhipMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;
    public static final String DESCRIPTION_KEY = "!" + ID + "!";
    public boolean modified;
    public boolean upgraded;

    @Override
    public boolean validCard(AbstractCard card) {
        return cardCheck(card, c -> ((c.baseDamage > 1 || c.baseBlock > 1) && c.cost != -2 && usesEnemyTargeting()));
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
        if (card instanceof SashWhip) {
            return magic + getBaseVal(card);
        }
        return magic;
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        if (!(card instanceof SashWhip) && target != null) {
            this.addToBot(new HeadStompAction((AbstractMonster) target, getBaseVal(card)));
        }
    }

    @Override
    public Color getGlow(AbstractCard card) {
        if (!(card instanceof SashWhip) && !AbstractDungeon.actionManager.cardsPlayedThisCombat.isEmpty()
                && AbstractDungeon.actionManager.cardsPlayedThisCombat.get(AbstractDungeon.actionManager.cardsPlayedThisCombat.size() - 1).type == AbstractCard.CardType.ATTACK) {
            return Color.GOLD.cpy();
        }
        return null;
    }

    public int getBaseVal(AbstractCard card) {
        return 1 + getEffectiveUpgrades(card);
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
        if (card instanceof SashWhip) {
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
        return new WhipMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}