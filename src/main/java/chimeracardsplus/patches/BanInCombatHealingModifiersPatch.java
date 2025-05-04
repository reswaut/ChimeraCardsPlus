package chimeracardsplus.patches;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.patches.OnCardGeneratedPatches;
import CardAugments.screens.ModifierScreen;
import basemod.ReflectionHacks;
import chimeracardsplus.interfaces.HealingMod;
import chimeracardsplus.ui.HealingModTooltip;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.screens.options.DropdownMenu;
import javassist.CtBehavior;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class BanInCombatHealingModifiersPatch {
    public static HealingModTooltip healingModTooltip = new HealingModTooltip();
    private static boolean inCombatRoll = false;
    private static boolean isHealingMod = false;

    @SpirePatch(
            clz = OnCardGeneratedPatches.DiscoveryStyleCards.class,
            method = "roll"
    )
    public static class DiscoveryStyleCardsDoublePatch {
        @SpirePrefixPatch
        public static void Prefix() {
            inCombatRoll = true;
        }

        @SpirePostfixPatch
        public static void Postfix() {
            inCombatRoll = false;
        }
    }

    @SpirePatch(
            clz = OnCardGeneratedPatches.CreatedCards.class,
            method = "roll"
    )
    public static class CreatedCardsDoublePatch {
        @SpirePrefixPatch
        public static void Prefix() {
            inCombatRoll = true;
        }

        @SpirePostfixPatch
        public static void Postfix() {
            inCombatRoll = false;
        }
    }

    @SpirePatch(
            clz = CardAugmentsMod.class,
            method = "applyWeightedCardMod"
    )
    public static class ApplyInCombatModPatch {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"validMods"}
        )
        public static void Insert(AbstractCard c, AbstractAugment.AugmentRarity rarity, int index, @ByRef ArrayList<AbstractAugment>[] validMods) {
            if (inCombatRoll) {
                validMods[0] = validMods[0].stream().filter((mod) -> !(mod instanceof HealingMod)).collect(Collectors.toCollection(ArrayList::new));
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(ArrayList.class, "isEmpty");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = ModifierScreen.class,
            method = "open"
    )
    public static class PositionHealingModTooltipPatch {
        @SpirePostfixPatch
        public static void Postfix() {
            float HB_X = ReflectionHacks.getPrivateStatic(ModifierScreen.class, "HB_X");
            float DISABLE_Y = ReflectionHacks.getPrivateStatic(ModifierScreen.class, "DISABLE_Y");
            healingModTooltip.set(HB_X / Settings.scale - 110.0F, DISABLE_Y / Settings.scale - 60.0F);
        }
    }

    @SpirePatch(
            clz = ModifierScreen.class,
            method = "render"
    )
    public static class RenderHealingModTooltipPatch {
        @SpirePostfixPatch
        public static void Postfix(ModifierScreen __instance, SpriteBatch sb) {
            if (!isHealingMod) {
                return;
            }
            DropdownMenu modDropdown = ReflectionHacks.getPrivate(__instance, ModifierScreen.class, "modDropdown");
            if (modDropdown != null && modDropdown.isOpen) {
                return;
            }
            DropdownMenu rarityDropdown = ReflectionHacks.getPrivate(__instance, ModifierScreen.class, "rarityDropdown");
            if (rarityDropdown != null && rarityDropdown.isOpen) {
                return;
            }
            DropdownMenu augmentDropdown = ReflectionHacks.getPrivate(__instance, ModifierScreen.class, "augmentDropdown");
            if (augmentDropdown != null && augmentDropdown.isOpen) {
                return;
            }
            DropdownMenu characterDropdown = ReflectionHacks.getPrivate(__instance, ModifierScreen.class, "characterDropdown");
            if (characterDropdown != null && characterDropdown.isOpen) {
                return;
            }
            if (CardCrawlGame.cardPopup.isOpen) {
                return;
            }
            healingModTooltip.render(sb);
        }
    }

    @SpirePatch(
            clz = ModifierScreen.class,
            method = "changedSelectionTo"
    )
    public static class IsHealingModPatch {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"selectedAugment"}
        )
        public static void Insert(AbstractAugment selectedAugment) {
            isHealingMod = selectedAugment instanceof HealingMod;
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(ModifierScreen.class, "modifierDisabled");
                return LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}
