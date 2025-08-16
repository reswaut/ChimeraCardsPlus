package chimeracardsplus.cardmods.special;

import CardAugments.cardmods.AbstractAugment;
import CardAugments.patches.InterruptUseCardFieldPatches;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class PredictedMod extends AbstractAugment {
    public static final String ID = ChimeraCardsPlus.makeID(PredictedMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;
    private boolean addedExhaust, modMagic = false;
    private int energyOnUse;

    public PredictedMod() {
        this.energyOnUse = -1;
    }

    public PredictedMod(int energyOnUse) {
        this.energyOnUse = energyOnUse;
    }

    public void setEnergyOnUse(AbstractCard card, int energyOnUse) {
        if (card.cost != -1) {
            return;
        }
        this.energyOnUse = energyOnUse;
        card.cost = energyOnUse;
        card.costForTurn = energyOnUse;
        card.initializeDescription();
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (!card.exhaust && card.type != AbstractCard.CardType.POWER) {
            addedExhaust = true;
            card.exhaust = true;
        }
        card.freeToPlayOnce = true;
        if (card.cost == -1) {
            InterruptUseCardFieldPatches.InterceptUseField.interceptUse.set(card, true);
            if (energyOnUse >= 0) {
                card.cost = energyOnUse;
                card.costForTurn = energyOnUse;
                card.initializeDescription();
            }
        }
        if (cardCheck(card, (c) -> c.baseMagicNumber >= 1 && doesntDowngradeMagic())) {
            modMagic = true;
        }
    }

    @Override
    public void onUpgradeCheck(AbstractCard card) {
        if (!card.exhaust && card.type != AbstractCard.CardType.POWER) {
            addedExhaust = true;
            card.exhaust = true;
        }
        card.initializeDescription();
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return card.cost >= -1 && isNormalCard(card) && noShenanigans(card);
    }

    @Override
    public float modifyBaseDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        return damage > 0.0F ? (damage * 4.0F / 3.0F) : damage;
    }

    @Override
    public float modifyBaseBlock(float block, AbstractCard card) {
        return block > 0.0F ? (block * 4.0F / 3.0F) : block;
    }

    @Override
    public float modifyBaseMagic(float magic, AbstractCard card) {
        return modMagic ? (magic * 4.0F / 3.0F) : magic;
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
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        if (!InterruptUseCardFieldPatches.InterceptUseField.interceptUse.get(card)) {
            return;
        }
        card.energyOnUse = energyOnUse;
        card.use(AbstractDungeon.player, target instanceof AbstractMonster ? (AbstractMonster) target : null);
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        if (energyOnUse >= 0) {
            return rawDescription.replace(CARD_TEXT[1], String.format(CARD_TEXT[2], energyOnUse))
                    .replace(CARD_TEXT[3], String.format(CARD_TEXT[4], energyOnUse));
        }
        return insertAfterText(rawDescription, addedExhaust ? CARD_TEXT[0] : "");
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.SPECIAL;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new PredictedMod(energyOnUse);
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}