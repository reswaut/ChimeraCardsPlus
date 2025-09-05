package chimeracardsplus.cardmods.common;

import CardAugments.cardmods.DynvarCarrier;
import CardAugments.util.FormatHelper;
import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import com.evacipated.cardcrawl.mod.stslib.StSLib;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardRarity;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.GameDictionary;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;

public class TransientMod extends AbstractAugmentPlus implements DynvarCarrier {
    public static final String ID = ChimeraCardsPlus.makeID(TransientMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;
    private static final String DESCRIPTION_KEY = '!' + ID + '!';
    private int uses;
    private boolean modMagic = false, removeUnplayable = false;

    public TransientMod() {
        this(5);
    }
    public TransientMod(int uses) {
        this.uses = uses;
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (cardCheck(card, c -> c.baseMagicNumber >= 1 && doesntDowngradeMagic())) {
            modMagic = true;
        }
        if (cardCheck(card, c -> c.type == CardType.CURSE && c.cost == -2 && doesntUpgradeCost())) {
            removeUnplayable = true;
            card.cost = 3;
            card.costForTurn = 3;
        }
    }

    @Override
    public boolean validCard(AbstractCard abstractCard) {
        return cardCheck(abstractCard, c -> (c.cost >= -1 && c.rarity != CardRarity.BASIC && isNormalCard(c) || c.type == CardType.CURSE && c.cost == -2 && doesntUpgradeCost()) && isCardRemovable(abstractCard, false));
    }

    @Override
    public float modifyBaseDamage(float damage, DamageType type, AbstractCard card, AbstractMonster target) {
        return damage > 0.0F ? damage * 2.0F : damage;
    }

    @Override
    public float modifyBaseBlock(float block, AbstractCard card) {
        return block > 0.0F ? block * 2.0F : block;
    }

    @Override
    public float modifyBaseMagic(float magic, AbstractCard card) {
        return modMagic ? magic * 2.0F : magic;
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        uses -= 1;
        card.initializeDescription();

        AbstractCard cardToRemove = StSLib.getMasterDeckEquivalent(card);
        if (cardToRemove == null || !CardModifierManager.hasModifier(cardToRemove, ID)) {
            return;
        }

        TransientMod modifier = (TransientMod) CardModifierManager.getModifiers(cardToRemove, ID).get(0);
        modifier.uses -= 1;
        cardToRemove.initializeDescription();
        if (modifier.uses > 0 || !isCardRemovable(card, false)) {
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
        String description = rawDescription;
        if (removeUnplayable) {
            for (String s : GameDictionary.UNPLAYABLE.NAMES) {
                description = description.replace(FormatHelper.capitalize(s) + LocalizedStrings.PERIOD + " NL ", "");
                description = description.replace(FormatHelper.capitalize(s) + ' ' + LocalizedStrings.PERIOD + " NL ", "");
                description = description.replace(FormatHelper.capitalize(s) + LocalizedStrings.PERIOD, "");
                description = description.replace(FormatHelper.capitalize(s) + ' ' + LocalizedStrings.PERIOD, "");
            }
        }
        if (uses <= 1) {
            return insertAfterText(description, CARD_TEXT[1]);
        }
        return insertAfterText(description, String.format(CARD_TEXT[0], DESCRIPTION_KEY));
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

    @Override
    public AugmentBonusLevel getModBonusLevel() {
        return AugmentBonusLevel.HEALING;
    }

    @Override
    public String key() {
        return ID;
    }

    @Override
    public int val(AbstractCard abstractCard) {
        return uses;
    }

    @Override
    public int baseVal(AbstractCard abstractCard) {
        return 5 + getEffectiveUpgrades(abstractCard);
    }

    @Override
    public boolean modified(AbstractCard abstractCard) {
        return val(abstractCard) != baseVal(abstractCard);
    }

    @Override
    public boolean upgraded(AbstractCard abstractCard) {
        return abstractCard.timesUpgraded != 0 || abstractCard.upgraded;
    }
}