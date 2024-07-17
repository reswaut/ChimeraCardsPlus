package chimeracardsplus.cardmods.common;

import CardAugments.cardmods.AbstractAugment;
import CardAugments.patches.InterruptUseCardFieldPatches;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.EmptyOrbSlot;

public class BarrageMod extends AbstractAugment {
    public static final String ID = ChimeraCardsPlus.makeID(BarrageMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

    @Override
    public void onInitialApplication(AbstractCard card) {
        InterruptUseCardFieldPatches.InterceptUseField.interceptUse.set(card, true);
    }

    @Override
    public float modifyBaseDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        return damage * 0.5F;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return allowOrbMods() && cardCheck(card, (c) -> (c.baseDamage > 1
                && noShenanigans(c)
                && usesEnemyTargeting()
                && c.type == CardType.ATTACK
                && customCheck(c, (check) ->
                    noCardModDescriptionChanges(check)
                    && check.rawDescription.chars().filter((ch) -> ch == 46 || ch == 12290).count() == 1L)
        ));
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
        if (target instanceof AbstractMonster) {
            int hits = 0;
            for (int i = 0; i < AbstractDungeon.player.orbs.size(); ++i) {
                if (!(AbstractDungeon.player.orbs.get(i) instanceof EmptyOrbSlot)) {
                    ++hits;
                }
            }
            for (int i = 0; i < hits; ++i) {
                card.use(AbstractDungeon.player, (AbstractMonster)target);
            }
        }
    }

    @Override
    public AbstractAugment.AugmentRarity getModRarity() {
        return AugmentRarity.COMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new BarrageMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}