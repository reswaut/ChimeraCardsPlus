package chimeracardsplus.cardmods.rare;

import CardAugments.patches.RolledModFieldPatches.RolledModField;
import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import chimeracardsplus.helpers.Constants;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardRarity;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class PackedMod extends AbstractAugmentPlus {
    public static final String ID = ChimeraCardsPlus.makeID(PackedMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;
    private Iterable<AbstractCard> rewardCards = new ArrayList<>(Constants.DEFAULT_LIST_SIZE);

    @Override
    public boolean validCard(AbstractCard abstractCard) {
        return isNormalCard(abstractCard) && abstractCard.rarity != CardRarity.BASIC && (!CardCrawlGame.isInARun() || AbstractDungeon.getCurrMapNode() != null && AbstractDungeon.getCurrRoom() instanceof MonsterRoom);
    }

    @Override
    public boolean onObtain(AbstractCard card) {
        for (AbstractCard c : rewardCards) {
            AbstractCard cardToAdd = c.makeStatEquivalentCopy();
            RolledModField.rolled.set(cardToAdd, true);
            AbstractDungeon.topLevelEffects.add(new ShowCardAndObtainEffect(cardToAdd, c.current_x, c.current_y));
        }
        return true;
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
        return insertAfterText(rawDescription, CARD_TEXT[0]);
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new PackedMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    @Override
    public AugmentBonusLevel getModBonusLevel() {
        return AugmentBonusLevel.HEALING;
    }

    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "getRewardCards"
    )
    public static class GetRewardCardsPatches {
        @SpirePostfixPatch
        public static ArrayList<AbstractCard> Postfix(ArrayList<AbstractCard> __result) {
            for (AbstractCard c : __result) {
                ArrayList<AbstractCardModifier> modifiers = CardModifierManager.getModifiers(c, ID);
                if (modifiers.isEmpty()) {
                    continue;
                }
                PackedMod mod = (PackedMod) modifiers.get(0);
                mod.rewardCards = __result.stream().filter(card -> !c.equals(card)).collect(Collectors.toCollection(ArrayList::new));
            }
            return __result;
        }
    }
}