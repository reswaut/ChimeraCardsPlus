package chimeracardsplus.cardmods.rare;

import CardAugments.cardmods.AbstractAugment;
import CardAugments.cardmods.DynvarCarrier;
import CardAugments.patches.InterruptUseCardFieldPatches;
import CardAugments.util.PortraitHelper;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.interfaces.BonusMod;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.purple.Wish;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.watcher.LiveForeverPower;

public class ImmortalityMod extends AbstractAugment implements DynvarCarrier, BonusMod {
    public static final String ID = ChimeraCardsPlus.makeID(ImmortalityMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;
    public static final String DESCRIPTION_KEY = "!" + ID + "!";
    public boolean modified;
    public boolean upgraded;

    public ImmortalityMod() {
        this.priority = -100;
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        InterruptUseCardFieldPatches.InterceptUseField.interceptUse.set(card, true);
        card.exhaust = false;
        if (card.type != AbstractCard.CardType.POWER) {
            card.type = AbstractCard.CardType.POWER;
            PortraitHelper.setMaskedPortrait(card);
        }
    }

    @Override
    public float modifyBaseDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        return -1.0F;
    }

    @Override
    public float modifyBaseMagic(float magic, AbstractCard card) {
        return -1.0F;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return cardCheck(card, (c) -> (c instanceof Wish && noShenanigans(c)))
                && characterCheck((p) -> {
            int count = 0;
            for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                if (c instanceof Wish) {
                    ++count;
                    if (count >= 2) {
                        return true;
                    }
                }
            }
            return false;
        });
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        this.addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new LiveForeverPower(AbstractDungeon.player, getBaseVal(card))));
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
        return rawDescription.replace(CARD_TEXT[0], String.format(CARD_TEXT[1], DESCRIPTION_KEY));
    }

    public int getBaseVal(AbstractCard card) {
        return 3 + this.getEffectiveUpgrades(card);
    }

    public String key() {
        return ID;
    }

    public int val(AbstractCard card) {
        return this.getBaseVal(card);
    }

    public int baseVal(AbstractCard card) {
        return this.getBaseVal(card);
    }

    public boolean modified(AbstractCard card) {
        return this.modified;
    }

    public boolean upgraded(AbstractCard card) {
        this.modified = card.timesUpgraded != 0 || card.upgraded;
        this.upgraded = card.timesUpgraded != 0 || card.upgraded;
        return this.upgraded;
    }

    @Override
    public void onUpgradeCheck(AbstractCard card) {
        card.initializeDescription();
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new ImmortalityMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}