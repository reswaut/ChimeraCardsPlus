package chimeracardsplus.cardmods.rare;

import CardAugments.cardmods.util.PreviewedMod;
import CardAugments.patches.InterruptUseCardFieldPatches.InterceptUseField;
import CardAugments.util.FormatHelper;
import CardAugments.util.PortraitHelper;
import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.MultiCardPreview;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.actions.DiscoverModAction;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardTarget;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class DiscoveredMod extends AbstractAugmentPlus {
    public static final String ID = ChimeraCardsPlus.makeID(DiscoveredMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;
    private boolean inherentHack = true;

    public DiscoveredMod() {
        priority = -100;
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        inherentHack = true;
        AbstractCard preview = card.makeStatEquivalentCopy();
        inherentHack = false;
        CardModifierManager.addModifier(preview, new PreviewedMod());
        MultiCardPreview.add(card, preview);
        InterceptUseField.interceptUse.set(card, true);
        card.isEthereal = false;
        card.selfRetain = false;
        card.exhaust = true;
        card.cost = 1;
        card.costForTurn = card.cost;
        card.target = CardTarget.NONE;
        if (card.type != CardType.SKILL) {
            card.type = CardType.SKILL;
            PortraitHelper.setMaskedPortrait(card);
        }
    }

    @Override
    public float modifyBaseDamage(float damage, DamageType type, AbstractCard card, AbstractMonster target) {
        return -1.0F;
    }

    @Override
    public float modifyBaseBlock(float block, AbstractCard card) {
        return -1.0F;
    }

    @Override
    public float modifyBaseMagic(float magic, AbstractCard card) {
        return -1.0F;
    }

    @Override
    public boolean validCard(AbstractCard abstractCard) {
        return cardCheck(abstractCard, c -> isNormalCard(c) && doesntUpgradeCost() && noShenanigans(c));
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return (card.isInnate ? CARD_TEXT[1] : "") + String.format(CARD_TEXT[0], FormatHelper.prefixWords(card.name, "*"));
    }

    @Override
    public void onUpgradeCheck(AbstractCard card) {
        for (AbstractCard o : MultiCardPreview.multiCardPreview.get(card)) {
            if (CardModifierManager.hasModifier(o, PreviewedMod.ID)) {
                o.upgrade();
                o.initializeDescription();
            }
        }
        card.initializeDescription();
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        AbstractCard preview = MultiCardPreview.multiCardPreview.get(card).stream().filter(o -> CardModifierManager.hasModifier(o, PreviewedMod.ID)).findFirst().orElse(null);
        addToBot(new DiscoverModAction(preview));
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
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new DiscoveredMod();
    }

    @Override
    public boolean isInherent(AbstractCard card) {
        return inherentHack;
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    @Override
    public AugmentBonusLevel getModBonusLevel() {
        return AugmentBonusLevel.HEALING;
    }
}