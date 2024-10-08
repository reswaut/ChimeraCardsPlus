package chimeracardsplus.cardmods.rare;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.actions.watcher.PressEndTurnButtonAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static chimeracardsplus.util.CardCheckHelpers.doesntDowngradeMagicNoUseChecks;

public class AuraMod extends AbstractAugment {
    public static final String ID = ChimeraCardsPlus.makeID(AuraMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;
    private boolean token;

    public AuraMod() {
        this.priority = -100;
        this.token = false;
    }

    public AuraMod(boolean token) {
        this.priority = -100;
        this.token = token;
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        card.selfRetain = true;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return (card.baseDamage >= 4 || card.baseBlock >= 4 || (card.baseMagicNumber >= 4 && doesntDowngradeMagicNoUseChecks(card)))
                && cardCheck(card, (c) -> (c.cost >= 0
                && notRetain(c) && notExhaust(c) && notEthereal(c) && noShenanigans(c)
                && !usesAction(c, PressEndTurnButtonAction.class)
                && (c.type == AbstractCard.CardType.ATTACK || c.type == AbstractCard.CardType.SKILL)
                && doesntOverride(c, "triggerOnEndOfTurnForPlayingCard")
                && customCheck(c, AbstractAugment::noCardModDescriptionChanges)));
    }

    @Override
    public float modifyBaseDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        return (damage >= 1) ? Math.max(1.0F, damage / (token ? 4.0F : 2.0F)) : damage;
    }

    @Override
    public float modifyBaseBlock(float block, AbstractCard card) {
        return (block >= 1) ? Math.max(1.0F, block / (token ? 4.0F : 2.0F)) : block;
    }

    @Override
    public float modifyBaseMagic(float magic, AbstractCard card) {
        return (magic >= 1 && doesntDowngradeMagicNoUseChecks(card)) ? Math.max(1.0F, magic / (token ? 4.0F : 2.0F)) : magic;
    }

    @Override
    public void onRetained(AbstractCard card) {
        AbstractCard copy = card.makeStatEquivalentCopy();
        for (AbstractCardModifier mod : CardModifierManager.modifiers(copy)) {
            if (mod instanceof AuraMod) {
                ((AuraMod) mod).token = true;
            }
        }
        AbstractMonster target = null;
        if (targetsEnemy(card)) {
            target = AbstractDungeon.getMonsters().getRandomMonster(null, true, AbstractDungeon.cardRandomRng);
        }
        copy.calculateCardDamage(target);
        copy.use(AbstractDungeon.player, target);
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
        char[] c = rawDescription.replace(CARD_TEXT[2], "")
                .replace("!D!", String.valueOf(Math.max(1, card.baseDamage / 4)))
                .replace("!B!", String.valueOf(Math.max(1, card.baseBlock / 4)))
                .replace("!M!", String.valueOf(Math.max(1, card.baseMagicNumber / 4)))
                .toCharArray();
        c[0] = Character.toLowerCase(c[0]);
        return CARD_TEXT[0] + insertAfterText(rawDescription, CARD_TEXT[1] + new String(c));
    }

    @Override
    public void onApplyPowers(AbstractCard card) {
        card.initializeDescription();
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
        return new AuraMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}