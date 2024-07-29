package chimeracardsplus.cardmods.uncommon;

import CardAugments.cardmods.AbstractAugment;
import CardAugments.patches.InterruptUseCardFieldPatches;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.actions.common.BetterDiscardPileToHandAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.blue.Hologram;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class HoloMod extends AbstractAugment {
    public static final String ID = ChimeraCardsPlus.makeID(HoloMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;
    private boolean addedExhaust;

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (getEffectiveUpgrades(card) == 0) {
            addedExhaust = !card.exhaust;
            card.exhaust = true;
        }
        if (card instanceof Hologram) {
            InterruptUseCardFieldPatches.InterceptUseField.interceptUse.set(card, true);
        }
    }

    @Override
    public void onUpgradeCheck(AbstractCard card) {
        if (card.exhaust && addedExhaust) {
            card.exhaust = false;
        }
        addedExhaust = false;
        card.initializeDescription();
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return cardCheck(card, (c) -> (c.cost != -2 && (c.baseDamage >= 3 || c.baseBlock >= 3)));
    }

    @Override
    public float modifyBaseDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        return (damage >= 3) ? (damage / 3.0F) : damage;
    }

    @Override
    public float modifyBaseBlock(float block, AbstractCard card) {
        return (block >= 3) ? (block / 3.0F) : block;
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        if (card instanceof Hologram) {
            this.addToBot(new GainBlockAction(AbstractDungeon.player, AbstractDungeon.player, card.block));
            this.addToBot(new BetterDiscardPileToHandAction(2));
            return;
        }
        this.addToBot(new BetterDiscardPileToHandAction(1));
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
        if (card instanceof Hologram) {
            return rawDescription.replace(CARD_TEXT[2], CARD_TEXT[3]);
        }
        return insertAfterText(rawDescription, addedExhaust ? CARD_TEXT[0] : CARD_TEXT[1]);
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new HoloMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}