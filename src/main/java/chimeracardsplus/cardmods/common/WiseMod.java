package chimeracardsplus.cardmods.common;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.MultiCardPreview;
import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.tempCards.Insight;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static chimeracardsplus.util.CardAugmentsExt.doesntDowngradeMagicNoUseChecks;

public class WiseMod extends AbstractAugment {
    public static final String ID = ChimeraCardsPlus.makeID(WiseMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

    @Override
    public void onInitialApplication(AbstractCard card) {
        MultiCardPreview.add(card, new Insight());
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return cardCheck(card, c -> ((c.cost == -1 || c.cost >= 1) && doesntUpgradeCost())) &&
                (card.baseDamage > 1 || card.baseBlock > 1 || (card.baseMagicNumber > 1 && doesntDowngradeMagicNoUseChecks(card)));
    }

    @Override
    public float modifyBaseDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        return (damage > 1) ? (damage * 2.0F / 3.0F) : damage;
    }

    @Override
    public float modifyBaseBlock(float block, AbstractCard card) {
        return (block > 1) ? (block * 2.0F / 3.0F) : block;
    }

    @Override
    public float modifyBaseMagic(float magic, AbstractCard card) {
        return (magic > 1 && doesntDowngradeMagicNoUseChecks(card)) ? (magic * 2.0F / 3.0F) : magic;
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
        if (card.cost == -1) {
            text = CARD_TEXT[2];
        } else if (card.cost == 1) {
            text = String.format(CARD_TEXT[0], card.cost);
        } else if (card.cost > 1) {
            text = String.format(CARD_TEXT[1], card.cost);
        }
        return insertAfterText(rawDescription, text);
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        if (card.cost == 0 || card.cost <= -2) {
            return;
        }
        this.addToBot(new MakeTempCardInDrawPileAction(new Insight(), (card.cost > 0) ? card.cost : card.energyOnUse, true, true));
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.COMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new WiseMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}