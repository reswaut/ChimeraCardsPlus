package chimeracardsplus.cardmods.common;

import CardAugments.cardmods.AbstractAugment;
import CardAugments.cardmods.DynvarCarrier;
import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.interfaces.HealingMod;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;

import static chimeracardsplus.util.CardCheckHelpers.doesntDowngradeMagicNoUseChecks;

public class TransientMod extends AbstractAugment implements DynvarCarrier, HealingMod {
    public static final String ID = ChimeraCardsPlus.makeID(TransientMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;
    public static final String DESCRIPTION_KEY = "!" + ID + "!";
    private int uses;

    public TransientMod() {
        this.uses = 5;
    }

    public TransientMod(int uses) {
        this.uses = uses;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return card.cost >= -1 && card.rarity != AbstractCard.CardRarity.BASIC && isNormalCard(card) &&
                (card.baseDamage > 0 || card.baseBlock > 0 || (card.baseMagicNumber > 0 && doesntDowngradeMagicNoUseChecks(card)));
    }

    @Override
    public float modifyBaseDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        return (damage > 0.0F) ? (damage * 2.0F) : damage;
    }

    @Override
    public float modifyBaseBlock(float block, AbstractCard card) {
        return (block > 0.0F) ? (block * 2.0F) : block;
    }

    @Override
    public float modifyBaseMagic(float magic, AbstractCard card) {
        return (magic > 0.0F && doesntDowngradeMagicNoUseChecks(card)) ? (magic * 2.0F) : magic;
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        this.uses -= 1;
        card.initializeDescription();

        AbstractCard cardToRemove = null;
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.uuid.equals(card.uuid)) {
                cardToRemove = c;
                break;
            }
        }
        if (cardToRemove == null || !CardModifierManager.hasModifier(cardToRemove, ID)) {
            return;
        }

        TransientMod modifier = (TransientMod) CardModifierManager.getModifiers(cardToRemove, ID).get(0);
        modifier.uses -= 1;
        cardToRemove.initializeDescription();
        if (modifier.uses > 0) {
            return;
        }

        AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(cardToRemove));
        AbstractDungeon.player.masterDeck.removeCard(cardToRemove);
    }

    @Override
    public void onUpgradeCheck(AbstractCard card) {
        uses += 1;
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
        if (uses <= 1) {
            return insertAfterText(rawDescription, CARD_TEXT[1]);
        }
        return insertAfterText(rawDescription, String.format(CARD_TEXT[0], DESCRIPTION_KEY));
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.COMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new TransientMod(uses);
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    public int getBaseVal(AbstractCard card) {
        return 5 + this.getEffectiveUpgrades(card);
    }

    @Override
    public String key() {
        return ID;
    }

    @Override
    public int val(AbstractCard card) {
        return uses;
    }

    @Override
    public int baseVal(AbstractCard card) {
        return 5 + getEffectiveUpgrades(card);
    }

    @Override
    public boolean modified(AbstractCard card) {
        return val(card) != getBaseVal(card);
    }

    @Override
    public boolean upgraded(AbstractCard card) {
        return card.timesUpgraded != 0 || card.upgraded;
    }
}