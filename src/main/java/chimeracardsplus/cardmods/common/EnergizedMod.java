package chimeracardsplus.cardmods.common;

import CardAugments.cardmods.AbstractAugment;
import CardAugments.patches.InterruptUseCardFieldPatches.InterceptUseField;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class EnergizedMod extends AbstractAugment {
    public static final String ID = ChimeraCardsPlus.makeID(EnergizedMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;

    @Override
    public void onInitialApplication(AbstractCard card) {
        InterceptUseField.interceptUse.set(card, Boolean.TRUE);
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
    public boolean validCard(AbstractCard abstractCard) {
        return cardCheck(abstractCard, c -> noShenanigans(c)
                && c.cost >= 0
                && (c.baseDamage >= 2 || c.baseBlock >= 2)
                && customCheck(c, check -> noCardModDescriptionChanges(check) && check.rawDescription.chars().filter(ch -> ch == '.' || ch == '。').count() == 1L));
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
        return rawDescription.replaceFirst("[.。]", CARD_TEXT[0]);
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        addToBot(new UseCardPerEnergyAction(card, target));
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.COMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new EnergizedMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    private static class UseCardPerEnergyAction extends AbstractGameAction {
        private final AbstractCard card;

        private UseCardPerEnergyAction(AbstractCard card, AbstractCreature target) {
            this.card = card;
            this.target = target;
        }

        @Override
        public void update() {
            int hits = EnergyPanel.getCurrentEnergy();
            for (int i = 0; i < hits; ++i) {
                card.use(AbstractDungeon.player, (AbstractMonster) target);
            }
            isDone = true;
        }
    }
}