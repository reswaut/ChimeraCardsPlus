package chimeracardsplus.cardmods.uncommon;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;

import static chimeracardsplus.util.CardAugmentsExt.doesntDowngradeMagicNoUseChecks;

public class ObservantMod extends AbstractAugment {
    public static final String ID = ChimeraCardsPlus.makeID(ObservantMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

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
    public boolean validCard(AbstractCard card) {
        return (card.baseDamage > 1 || card.baseBlock > 1 || (card.magicNumber > 1 && doesntDowngradeMagicNoUseChecks(card)))
                && cardCheck(card, (c) -> ((c.cost == -1 || c.cost >= 1) && doesntUpgradeCost() && usesEnemyTargeting()));
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
            text = CARD_TEXT[1];
        } else if (card.cost >= 1) {
            text = String.format(CARD_TEXT[0], card.cost);
        }
        return insertAfterText(rawDescription, text);
    }

    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        if (card.cost == 0 || card.cost <= -2) {
            return;
        }
        if (target == null) {
            return;
        }
        AbstractMonster m = (AbstractMonster) target;
        if (m.getIntentBaseDmg() >= 0) {
            this.addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player,
                    new StrengthPower(AbstractDungeon.player, (card.cost > 0) ? card.cost : card.energyOnUse),
                    (card.cost > 0) ? card.cost : card.energyOnUse));
        }
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new ObservantMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}