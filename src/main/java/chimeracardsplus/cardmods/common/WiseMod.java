package chimeracardsplus.cardmods.common;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.MultiCardPreview;
import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.purple.Evaluate;
import com.megacrit.cardcrawl.cards.purple.Pray;
import com.megacrit.cardcrawl.cards.tempCards.Insight;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static chimeracardsplus.util.CardCheckHelpers.doesntDowngradeMagicNoUseChecks;

public class WiseMod extends AbstractAugment {
    public static final String ID = ChimeraCardsPlus.makeID(WiseMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (!(card instanceof Evaluate || card instanceof Pray)) {
            MultiCardPreview.add(card, new Insight());
        }
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return cardCheck(card, c -> ((c.cost == -1 || c.cost >= 1) && doesntUpgradeCost()
                && (c.type == AbstractCard.CardType.ATTACK || c.type == AbstractCard.CardType.SKILL))) &&
                (card.baseDamage > 1 || card.baseBlock > 1 || (card.baseMagicNumber > 1 && doesntDowngradeMagicNoUseChecks(card)));
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
        int cost = card.cost;
        if (card instanceof Evaluate || card instanceof Pray) {
            cost += 1;
        }
        String text = "";
        if (cost == -1) {
            text = CARD_TEXT[2];
        } else if (cost == 1) {
            text = CARD_TEXT[0];
        } else if (cost > 1) {
            text = String.format(CARD_TEXT[1], cost);
        }
        if (card instanceof Evaluate || card instanceof Pray) {
            return rawDescription.replace(CARD_TEXT[3], text).replace(CARD_TEXT[4], text);
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