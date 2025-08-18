package chimeracardsplus.cardmods.uncommon;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.cards.purple.TalkToTheHand;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.watcher.BlockReturnPower;

public class HandMod extends AbstractAugment {
    public static final String ID = ChimeraCardsPlus.makeID(HandMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;
    private boolean addedExhaust = true, modMagic = false;

    @Override
    public void onInitialApplication(AbstractCard card) {
        addedExhaust = !card.exhaust;
        card.exhaust = true;
        if (cardCheck(card, c -> c.baseMagicNumber >= 1 && doesntDowngradeMagic())) {
            modMagic = true;
        }
    }

    @Override
    public float modifyBaseDamage(float damage, DamageType type, AbstractCard card, AbstractMonster target) {
        return damage > 0.0F ? damage * 2.0F / 3.0F : damage;
    }

    @Override
    public float modifyBaseBlock(float block, AbstractCard card) {
        return block > 0.0F ? block * 2.0F / 3.0F : block;
    }

    @Override
    public float modifyBaseMagic(float magic, AbstractCard card) {
        return modMagic ? magic * 2.0F / 3.0F : magic;
    }

    @Override
    public boolean validCard(AbstractCard abstractCard) {
        return cardCheck(abstractCard, c -> (c.cost == -1 || c.cost >= 1) && doesntUpgradeCost() && doesntUpgradeExhaust() && (c.baseDamage >= 2 || c.baseBlock >= 2 || c.baseMagicNumber >= 2 && doesntDowngradeMagic()) && usesEnemyTargeting() && !TalkToTheHand.ID.equals(c.cardID) && (c.type == CardType.ATTACK || c.type == CardType.SKILL));
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
            text = addedExhaust ? CARD_TEXT[2] : CARD_TEXT[3];
        } else if (card.cost >= 1) {
            text = String.format(addedExhaust ? CARD_TEXT[0] : CARD_TEXT[1], card.cost);
        }
        return insertAfterText(rawDescription, text);
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        if (card.cost == 0 || card.cost <= -2 || target == null) {
            return;
        }
        addToBot(new ApplyPowerAction(target, AbstractDungeon.player,
                new BlockReturnPower(target, card.cost > 0 ? card.cost : card.energyOnUse),
                card.cost > 0 ? card.cost : card.energyOnUse));
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new HandMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}