package chimeracardsplus.cardmods.rare;

import CardAugments.cardmods.AbstractAugment;
import CardAugments.cardmods.util.PreviewedMod;
import CardAugments.patches.InterruptUseCardFieldPatches;
import CardAugments.util.FormatHelper;
import CardAugments.util.PortraitHelper;
import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.MultiCardPreview;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.actions.DiscoverModAction;
import chimeracardsplus.interfaces.HealingMod;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class DiscoveredMod extends AbstractAugment implements HealingMod {
    public static final String ID = ChimeraCardsPlus.makeID(DiscoveredMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;
    private boolean inherentHack = true;

    // This modifier should be applied first.
    public DiscoveredMod() {
        this.priority = -100;
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        this.inherentHack = true;
        AbstractCard preview = card.makeStatEquivalentCopy();
        this.inherentHack = false;
        CardModifierManager.addModifier(preview, new PreviewedMod());
        MultiCardPreview.add(card, preview);
        InterruptUseCardFieldPatches.InterceptUseField.interceptUse.set(card, true);
        card.isEthereal = false;
        card.selfRetain = false;
        card.exhaust = true;
        card.cost = 1;
        card.costForTurn = card.cost;
        card.target = AbstractCard.CardTarget.NONE;
        if (card.type != AbstractCard.CardType.SKILL) {
            card.type = AbstractCard.CardType.SKILL;
            PortraitHelper.setMaskedPortrait(card);
        }
    }

    @Override
    public float modifyBaseDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
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
    public boolean validCard(AbstractCard card) {
        return cardCheck(card, (c) -> (isNormalCard(c) && doesntUpgradeCost() && noShenanigans(c)));
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
        AbstractCard preview = null;
        for (AbstractCard o : MultiCardPreview.multiCardPreview.get(card)) {
            if (CardModifierManager.hasModifier(o, PreviewedMod.ID)) {
                preview = o;
                break;
            }
        }
        this.addToBot(new DiscoverModAction(preview));
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

    public boolean isInherent(AbstractCard card) {
        return this.inherentHack;
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}