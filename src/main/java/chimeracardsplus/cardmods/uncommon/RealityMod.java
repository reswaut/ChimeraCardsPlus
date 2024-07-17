package chimeracardsplus.cardmods.uncommon;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.MultiCardPreview;
import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.tempCards.Safety;
import com.megacrit.cardcrawl.cards.tempCards.Smite;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class RealityMod extends AbstractAugment {
    public static final String ID = ChimeraCardsPlus.makeID(RealityMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;
    private boolean damageReduction = false, blockReduction = false;

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (card.baseDamage > 12 && card.cost >= 1) {
            damageReduction = true;
            card.cost -= 1;
            card.costForTurn = card.cost;
            MultiCardPreview.add(card, new Smite());
        }
        if (card.baseBlock > 12 && card.cost >= 1) {
            blockReduction = true;
            card.cost -= 1;
            card.costForTurn = card.cost;
            MultiCardPreview.add(card, new Safety());
        }
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return cardCheck(card, c -> (c.cost >= 1 && doesntUpgradeCost() && (c.baseDamage > 12 || c.baseBlock > 12)));
    }

    @Override
    public float modifyBaseDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        return (damage > 1 && damageReduction) ? Math.max(1, damage - 12) : damage;
    }

    @Override
    public float modifyBaseBlock(float block, AbstractCard card) {
        return (block > 1 && blockReduction) ? Math.max(1, block - 12) : block;
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        if (damageReduction) {
            this.addToBot(new MakeTempCardInHandAction(new Smite(), 1));
        }
        if (blockReduction) {
            this.addToBot(new MakeTempCardInHandAction(new Safety(), 1));
        }
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
        if (damageReduction) {
            text += CARD_TEXT[0];
        }
        if (blockReduction) {
            text += CARD_TEXT[1];
        }
        return insertAfterText(rawDescription, text);
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new RealityMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}