package chimeracardsplus.cardmods.rare;

import CardAugments.cardmods.AbstractAugment;
import CardAugments.patches.InterruptUseCardFieldPatches;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class FiendMod extends AbstractAugment {
    public static final String ID = ChimeraCardsPlus.makeID(FiendMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

    @Override
    public void onInitialApplication(AbstractCard card) {
        InterruptUseCardFieldPatches.InterceptUseField.interceptUse.set(card, true);
        card.exhaust = true;
        card.cost += 2;
        card.costForTurn = card.cost;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return cardCheck(card, (c) -> (notExhaust(c)
                && c.cost >= 0 && doesntUpgradeCost()
                && (c.type == CardType.ATTACK || c.type == CardType.SKILL)
                && customCheck(c, (check) ->
                noCardModDescriptionChanges(check)
                        && check.rawDescription.chars().filter((ch) -> ch == '.' || ch == '。').count() == 1
                        && check.rawDescription.chars().noneMatch((ch) -> ch == ',' || ch == '，'))
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
        return insertBeforeText(rawDescription.replaceFirst("[.。]", CARD_TEXT[1]), CARD_TEXT[0]);
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        int count = 0;

        for (AbstractCard c : AbstractDungeon.player.hand.group) {
            if (!card.uuid.equals(c.uuid)) {
                count += 1;
                this.addToTop(new ExhaustSpecificCardAction(c, AbstractDungeon.player.hand));
            }
        }

        for (int i = 0; i < count; ++i) {
            card.use(AbstractDungeon.player, target instanceof AbstractMonster ? (AbstractMonster) target : null);
        }
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new FiendMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}