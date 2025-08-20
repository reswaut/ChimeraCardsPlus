package chimeracardsplus.cardmods.rare;

import CardAugments.patches.InterruptUseCardFieldPatches.InterceptUseField;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class FiendMod extends AbstractAugmentPlus {
    public static final String ID = ChimeraCardsPlus.makeID(FiendMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;

    @Override
    public void onInitialApplication(AbstractCard card) {
        InterceptUseField.interceptUse.set(card, true);
        card.exhaust = true;
        card.cost += 2;
        card.costForTurn = card.cost;
    }

    @Override
    public boolean validCard(AbstractCard abstractCard) {
        return cardCheck(abstractCard, c -> notExhaust(c)
                && c.cost >= 0 && doesntUpgradeCost()
                && (c.type == CardType.ATTACK || c.type == CardType.SKILL)
                && customCheck(c, check ->
                noCardModDescriptionChanges(check)
                        && check.rawDescription.chars().filter(ch -> ch == '.' || ch == '。').count() == 1L
                        && check.rawDescription.chars().noneMatch(ch -> ch == ',' || ch == '，')));
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
                addToTop(new ExhaustSpecificCardAction(c, AbstractDungeon.player.hand));
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

    @Override
    public AugmentBonusLevel getModBonusLevel() {
        return AugmentBonusLevel.NORMAL;
    }
}