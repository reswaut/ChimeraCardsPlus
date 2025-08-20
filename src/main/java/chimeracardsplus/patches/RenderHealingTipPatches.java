package chimeracardsplus.patches;

import CardAugments.cardmods.AbstractAugment;
import CardAugments.screens.ModifierScreen;
import basemod.ReflectionHacks;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import chimeracardsplus.cardmods.AbstractAugmentPlus.AugmentBonusLevel;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.screens.options.DropdownMenu;

public class RenderHealingTipPatches {
    public static final String ID = ChimeraCardsPlus.makeID(RenderHealingTipPatches.class.getSimpleName());
    private static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    private static float renderX = 0.0F, renderY = 0.0F;

    @SpirePatch(
            clz = ModifierScreen.class,
            method = "open"
    )
    public static class PositionHealingModTooltipPatch {
        @SpirePostfixPatch
        public static void Postfix() {
            renderX = ReflectionHacks.getPrivateStatic(ModifierScreen.class, "HB_X");
            renderX -= 110.0F * Settings.scale;
            renderY = ReflectionHacks.getPrivateStatic(ModifierScreen.class, "DISABLE_Y");
            renderY -= 60.0F * Settings.scale;
        }
    }

    @SpirePatch(
            clz = ModifierScreen.class,
            method = SpirePatch.CONSTRUCTOR
    )
    public static class RepositionCardsPatch {
        @SpirePostfixPatch
        public static void Postfix(ModifierScreen __instance, @ByRef float[] ___drawStartX) {
            ___drawStartX[0] += 45.0F * Settings.scale;
        }
    }

    @SpirePatch(
            clz = ModifierScreen.class,
            method = "render"
    )
    public static class RenderHealingModTooltipPatch {
        @SpirePostfixPatch
        public static void Postfix(ModifierScreen __instance, SpriteBatch sb, AbstractAugment ___selectedAugment) {
            if (!(___selectedAugment instanceof AbstractAugmentPlus)) {
                return;
            }
            AbstractAugmentPlus augmentPlus = (AbstractAugmentPlus) ___selectedAugment;
            if (augmentPlus.getModBonusLevel() == AugmentBonusLevel.NORMAL) {
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
            boolean renderedTipThisFrame = ReflectionHacks.getPrivateStatic(TipHelper.class, "renderedTipThisFrame");
            if (renderedTipThisFrame) {
                TipHelper.render(sb);
            }
            TipHelper.renderGenericTip(renderX, renderY, TEXT[0], TEXT[1]);
        }
    }
}
