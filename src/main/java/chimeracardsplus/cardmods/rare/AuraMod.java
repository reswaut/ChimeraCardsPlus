package chimeracardsplus.cardmods.rare;

import CardAugments.util.CalcHelper;
import basemod.BaseMod;
import basemod.abstracts.AbstractCardModifier;
import basemod.abstracts.DynamicVariable;
import basemod.helpers.CardModifierManager;
import basemod.interfaces.EditCardsSubscriber;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.actions.watcher.PressEndTurnButtonAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;

public class AuraMod extends AbstractAugmentPlus {
    public static final String ID = ChimeraCardsPlus.makeID(AuraMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;
    private int tokenDamage = -1, tokenBlock = -1;
    private boolean token;
    private boolean modMagic = false;

    @Override
    public boolean validCard(AbstractCard abstractCard) {
        return cardCheck(abstractCard, c -> c.cost >= 0 &&
                (c.baseDamage >= 4 || c.baseBlock >= 4 || c.baseMagicNumber >= 4 && doesntDowngradeMagic()) &&
                isCleanCard(c) && notRetain(c) && notExhaust(c) && notEthereal(c) && noShenanigans(c) &&
                !usesAction(c, PressEndTurnButtonAction.class) &&
                (c.type == CardType.ATTACK || c.type == CardType.SKILL) &&
                doesntOverride(c, "triggerOnEndOfTurnForPlayingCard") &&
                customCheck(c, check -> noCardModDescriptionChanges(check) &&
                        check.rawDescription.chars().filter(ch -> ch == LocalizedStrings.PERIOD.charAt(0)).count() == 1L &&
                        check.rawDescription.chars().noneMatch(ch -> ch == ',' || ch == '，')));
    }

    public AuraMod() {
        this(false);
    }
    public AuraMod(boolean token) {
        this.token = token;
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        card.selfRetain = true;
        if (cardCheck(card, c -> c.baseMagicNumber >= 1 && doesntDowngradeMagic())) {
            modMagic = true;
        }
        tokenDamage = TokenDynamicVariableInitializer.dynamicDamage.baseValue(card);
        tokenBlock = TokenDynamicVariableInitializer.dynamicBlock.baseValue(card);
    }

    @Override
    public float modifyBaseDamage(float damage, DamageType type, AbstractCard card, AbstractMonster target) {
        return token ? TokenDynamicVariableInitializer.dynamicDamage.baseValue(card) : damage > 0.0F ? damage - (int) (damage / 4.0F) * 2.0F : damage;
    }

    @Override
    public float modifyBaseBlock(float block, AbstractCard card) {
        return token ? TokenDynamicVariableInitializer.dynamicBlock.baseValue(card) : block > 0.0F ? block - (int) (block / 4.0F) * 2.0F : block;
    }

    @Override
    public float modifyBaseMagic(float magic, AbstractCard card) {
        return modMagic ? token ? magic / 4.0F : magic - (int) (magic / 4.0F) * 2.0F : magic;
    }

    @Override
    public void updateDynvar(AbstractCard card) {
        tokenDamage = TokenDynamicVariableInitializer.dynamicDamage.baseValue(card);
        tokenBlock = TokenDynamicVariableInitializer.dynamicBlock.baseValue(card);
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
            target = AbstractDungeon.getRandomMonster();
        }
        copy.calculateCardDamage(target);
        copy.use(AbstractDungeon.player, target);
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        char[] c = rawDescription.replace(CARD_TEXT[2], "")
                .replace("!D!", '!' + TokenDynamicVariableInitializer.dynamicDamage.key() + '!')
                .replace("!B!", '!' + TokenDynamicVariableInitializer.dynamicBlock.key() + '!')
                .replace("!M!", String.valueOf(modMagic ? card.baseMagicNumber / 4 : card.baseMagicNumber))
                .toCharArray();
        c[0] = Character.toLowerCase(c[0]);
        return insertAfterText(insertBeforeText(rawDescription, CARD_TEXT[0]), CARD_TEXT[1] + new String(c));
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
    public void onApplyPowers(AbstractCard card) {
        updateDynvar(card);
        if (tokenDamage >= 0) {
            tokenDamage = CalcHelper.applyPowers(tokenDamage);
        }
        if (tokenBlock >= 0) {
            tokenBlock = CalcHelper.applyPowersToBlock(tokenBlock);
        }
    }

    @Override
    public void onUpgradeCheck(AbstractCard card) {
        updateDynvar(card);
    }

    @SpireInitializer
    public static class TokenDynamicVariableInitializer implements EditCardsSubscriber {
        private static final TokenDamageVariable dynamicDamage = new TokenDamageVariable();
        private static final TokenBlockVariable dynamicBlock = new TokenBlockVariable();

        public static void initialize() {
            BaseMod.subscribe(new TokenDynamicVariableInitializer());
            ChimeraCardsPlus.logger.info("AuraMod DynVar Initializer subscribed to BaseMod.");
        }

        @Override
        public void receiveEditCards() {
            BaseMod.addDynamicVariable(dynamicDamage);
            BaseMod.addDynamicVariable(dynamicBlock);
        }

        private static class TokenDamageVariable extends DynamicVariable {
            @Override
            public String key() {
                return ID + ":D";
            }

            @Override
            public int value(AbstractCard abstractCard) {
                ArrayList<AbstractCardModifier> modifiers = CardModifierManager.getModifiers(abstractCard, ID);
                if (modifiers.isEmpty()) {
                    return -1;
                }
                AbstractCardModifier modifier = modifiers.get(0);
                if (!(modifier instanceof AuraMod)) {
                    return -1;
                }
                return ((AuraMod) modifier).tokenDamage;
            }

            @Override
            public int baseValue(AbstractCard abstractCard) {
                return abstractCard.baseDamage > 0 ? abstractCard.baseDamage / 4 : abstractCard.baseDamage;
            }

            @Override
            public boolean isModified(AbstractCard abstractCard) {
                return value(abstractCard) != baseValue(abstractCard);
            }

            @Override
            public boolean upgraded(AbstractCard abstractCard) {
                return abstractCard.timesUpgraded != 0 || abstractCard.upgraded;
            }
        }

        private static class TokenBlockVariable extends DynamicVariable {
            @Override
            public String key() {
                return ID + ":B";
            }

            @Override
            public int value(AbstractCard abstractCard) {
                ArrayList<AbstractCardModifier> modifiers = CardModifierManager.getModifiers(abstractCard, ID);
                if (modifiers.isEmpty()) {
                    return -1;
                }
                AbstractCardModifier modifier = modifiers.get(0);
                if (!(modifier instanceof AuraMod)) {
                    return -1;
                }
                return ((AuraMod) modifier).tokenBlock;
            }

            @Override
            public int baseValue(AbstractCard abstractCard) {
                return abstractCard.baseBlock > 0 ? abstractCard.baseBlock / 4 : abstractCard.baseBlock;
            }

            @Override
            public boolean isModified(AbstractCard abstractCard) {
                return value(abstractCard) != baseValue(abstractCard);
            }

            @Override
            public boolean upgraded(AbstractCard abstractCard) {
                return abstractCard.timesUpgraded != 0 || abstractCard.upgraded;
            }
        }
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

    @Override
    public AugmentBonusLevel getModBonusLevel() {
        return AugmentBonusLevel.NORMAL;
    }
}